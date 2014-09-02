package info.ferrarimarco.uniroma2.msa.resourcesharing.app.services.persistence;

import android.content.Context;
import android.os.AsyncTask;

import java.sql.SQLException;
import java.util.List;

import javax.inject.Inject;

import info.ferrarimarco.uniroma2.msa.resourcesharing.app.dao.GenericDao;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.Resource;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.event.ResourceListAvailableEvent;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.event.ResourceLocalSaveCompletedEvent;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.task.ResourceTaskResult;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.task.ResourceTaskType;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.task.TaskResultType;

public class ResourceService extends AbstractPersistenceService {

    @Inject
    GenericDao<Resource> resourceDao;

    @Inject
    public ResourceService(Context context) {
        super(context);
    }

    public void readResourcesFromLocalStorage(ResourceTaskType resourceTaskType) {
        new AsyncTask<ResourceTaskType, Void, ResourceTaskResult>() {
            @Override
            protected ResourceTaskResult doInBackground(ResourceTaskType... params) {

                // TODO: provide this with Dagger
                ResourceTaskResult result = new ResourceTaskResult();

                try {
                    resourceDao.open(Resource.class);
                } catch (SQLException e) {
                    e.printStackTrace();
                    result.setTaskResultType(TaskResultType.FAILURE);
                    return result;
                }

                List<Resource> resources = result.getResources();
                Resource res = new Resource();

                if (ResourceTaskType.READ_CREATED_BY_ME_RESOURCES.equals(params[0])) {
                    res.setType(Resource.ResourceType.CREATED_BY_ME);
                }

                try {
                    resources = resourceDao.read(res);
                } catch (SQLException e) {
                    e.printStackTrace();
                    result.setTaskResultType(TaskResultType.FAILURE);
                    return result;
                } finally {
                    resourceDao.close();
                }

                result.setTaskResultType(TaskResultType.SUCCESS);
                result.setResources(resources);

                return result;
            }

            @Override
            protected void onPostExecute(ResourceTaskResult result) {
                bus.post(new ResourceListAvailableEvent(result));
            }
        }.execute(resourceTaskType);
    }

    public void saveResourceLocal(Resource resource, final Boolean sentToBackend) {
        new AsyncTask<Resource, Void, ResourceTaskResult>() {
            @Override
            protected ResourceTaskResult doInBackground(Resource... params) {
                ResourceTaskResult result;
                try {
                    resourceDao.open(Resource.class);

                    Resource res = params[0];
                    res.setSentToBackend(sentToBackend);

                    resourceDao.save(params[0]);
                    result = new ResourceTaskResult(ResourceTaskType.SAVE_RESOURCE_LOCAL, TaskResultType.RESOURCE_SAVED);
                } catch (SQLException e) {
                    e.printStackTrace();
                    result = new ResourceTaskResult(ResourceTaskType.SAVE_RESOURCE_LOCAL, TaskResultType.RESOURCE_NOT_SAVED);
                } finally {
                    resourceDao.close();
                }

                result.addResource(params[0]);

                return result;
            }

            @Override
            protected void onPostExecute(ResourceTaskResult result) {
                bus.post(new ResourceLocalSaveCompletedEvent(result));
            }
        }.execute(resource);
    }
}
