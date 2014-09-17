package info.ferrarimarco.uniroma2.msa.resourcesharing.app.services.persistence;

import android.content.Context;
import android.os.AsyncTask;

import com.squareup.otto.Bus;

import java.sql.SQLException;
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

    @Inject
    public ResourceService(Context context){
        objectGraph = ObjectGraphUtils.getObjectGraph(context);
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

    public void readResourcesFromLocalStorage(ResourceTaskType resourceTaskType){
        new AsyncTask<ResourceTaskType, Void, ResourceTaskResult>(){
            @Override
            protected ResourceTaskResult doInBackground(ResourceTaskType... params){
                ResourceTaskResult result = new ResourceTaskResult(params[0]);
                try{
                    resourceDao.open(Resource.class);
                }catch(SQLException e){
                    e.printStackTrace();
                    result.setTaskResultType(TaskResultType.FAILURE);
                    return result;
                }

                List<Resource> resources = result.getResources();
                Resource res = new Resource();

                switch(params[0]){
                    case READ_NEW_RESOURCES_LOCAL:
                        res.setType(Resource.ResourceType.NEW);
                        break;
                    case READ_CREATED_BY_ME_RESOURCES_LOCAL:
                        res.setType(Resource.ResourceType.CREATED_BY_ME);
                        break;
                    case READ_BOOKED_BY_ME_RESOURCES:
                        res.setType(Resource.ResourceType.BOOKED_BY_ME);
                        break;
                }

                try{
                    resources = resourceDao.read(res);
                }catch(SQLException e){
                    e.printStackTrace();
                    result.setTaskResultType(TaskResultType.FAILURE);
                    return result;
                }finally{
                    resourceDao.close();
                }

                result.setTaskResultType(TaskResultType.SUCCESS);
                result.setResources(resources);
                return result;
            }

            @Override
            protected void onPostExecute(ResourceTaskResult result){
                bus.post(new ResourceListAvailableEvent(result));
            }
        }.execute(resourceTaskType);
    }

    public ResourceTaskResult saveResourceLocal(Resource resource){
        ResourceTaskResult result;
        try{
            resourceDao.open(Resource.class);
            resourceDao.save(resource);
            result = new ResourceTaskResult(ResourceTaskType.SAVE_RESOURCE_FROM_ME_LOCAL, TaskResultType.SUCCESS);
        }catch(SQLException e){
            e.printStackTrace();
            result = new ResourceTaskResult(ResourceTaskType.SAVE_RESOURCE_FROM_ME_LOCAL, TaskResultType.FAILURE);
        }finally{
            resourceDao.close();
        }

        result.addResource(resource);
        return result;
    }
}
