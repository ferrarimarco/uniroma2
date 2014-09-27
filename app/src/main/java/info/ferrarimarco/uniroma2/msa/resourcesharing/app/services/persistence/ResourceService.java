package info.ferrarimarco.uniroma2.msa.resourcesharing.app.services.persistence;

import android.content.Context;

import com.squareup.otto.Bus;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.ObjectGraph;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.dao.GenericDao;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.Resource;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.event.ResourceListAvailableEvent;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.task.ResourceTaskResult;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.task.ResourceTaskType;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.task.TaskResultType;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.util.ObjectGraphUtils;

public class ResourceService{

    protected ObjectGraph objectGraph;

    @Inject
    Bus bus;

    @Inject
    GenericDao<Resource> resourceDao;

    // For caching
    private List<Resource> createdByMeResources;
    private List<Resource> resourcesByOthers;
    private List<Resource> bookedByMeResources;

    @Inject
    public ResourceService(Context context){
        objectGraph = ObjectGraphUtils.getObjectGraph(context.getApplicationContext());
        objectGraph.inject(this);
        createdByMeResources = new ArrayList<>();
        resourcesByOthers = new ArrayList<>();
        bookedByMeResources = new ArrayList<>();
    }

    public Resource readResourceFromLocalStorage(Resource criterion){
        try{
            resourceDao.open(Resource.class);
        }catch(SQLException e){
            throw new RuntimeException(e);
        }

        Resource result = null;

        try{
            result = resourceDao.readUniqueResult(criterion);
        }catch(SQLException e){
            throw new RuntimeException(e);
        }finally{
            resourceDao.close();
        }

        return result;
    }

    public void readResourcesLocal(ResourceTaskType resourceTaskType){
        ResourceTaskResult result = new ResourceTaskResult(resourceTaskType);

        List<Resource> resources = null;

        Resource res = new Resource();

        switch(resourceTaskType){
            case READ_NEW_RESOURCES_LOCAL:
                res.setType(Resource.ResourceType.NEW);
                resources = resourcesByOthers;
                break;
            case READ_CREATED_BY_ME_RESOURCES_LOCAL:
                res.setType(Resource.ResourceType.CREATED_BY_ME);
                resources = createdByMeResources;
                break;
            case READ_BOOKED_BY_ME_RESOURCES:
                res.setType(Resource.ResourceType.BOOKED_BY_ME);
                resources = bookedByMeResources;
                break;
        }

        // No items in cache. Load from db
        if(resources != null && resources.isEmpty()){
            try{
                resourceDao.open(Resource.class);
                resources.addAll(resourceDao.read(res));
            }catch(SQLException e){
                e.printStackTrace();
                result.setTaskResultType(TaskResultType.FAILURE);
            }finally{
                if(resourceDao != null){
                    resourceDao.close();
                }
            }
        }

        result.setTaskResultType(TaskResultType.SUCCESS);
        result.setResources(resources);

        bus.post(new ResourceListAvailableEvent(result));
    }

    public ResourceTaskResult saveResourceLocal(Resource resource){
        ResourceTaskResult result = new ResourceTaskResult(ResourceTaskType.SAVE_RESOURCE_FROM_ME_LOCAL);
        result.addResource(resource);

        try{
            resourceDao.open(Resource.class);
            resourceDao.save(resource);
        }catch(SQLException e){
            e.printStackTrace();
            result.setTaskResultType(TaskResultType.FAILURE);
            return result;
        }finally{
            resourceDao.close();
        }

        switch(resource.getType()){
            case NEW:
                resourcesByOthers.add(resource);
                break;
            case CREATED_BY_ME:
                createdByMeResources.add(resource);
                break;
            case BOOKED_BY_ME:
                bookedByMeResources.add(resource);
                break;
        }

        return result;
    }

    public ResourceTaskResult deleteResourceLocal(Resource resource){
        ResourceTaskResult result = new ResourceTaskResult(ResourceTaskType.DELETE_RESOURCE);

        try{
            resourceDao.delete(resource);
            result.setTaskResultType(TaskResultType.SUCCESS);
        }catch(SQLException e){
            e.printStackTrace();
            result.setTaskResultType(TaskResultType.FAILURE);
            result.setMessage(e.getMessage());
            e.printStackTrace();
        }

        return result;
    }
}
