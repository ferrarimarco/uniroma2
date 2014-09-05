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
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.event.ResourceSaveCompletedEvent;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.task.ResourceTaskResult;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.task.ResourceTaskType;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.task.TaskResultType;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.util.ObjectGraphUtils;

public class ResourceService {

    protected ObjectGraph objectGraph;

    @Inject
    Bus bus;

    @Inject
    GenericDao<Resource> resourceDao;

    @Inject
    public ResourceService(Context context) {
        objectGraph = ObjectGraphUtils.getObjectGraph(context);
    }

    public void readResourcesFromLocalStorage(ResourceTaskType resourceTaskType) {
        new AsyncTask<ResourceTaskType, Void, ResourceTaskResult>() {
            @Override
            protected ResourceTaskResult doInBackground(ResourceTaskType... params) {

                // TODO: provide this with Dagger
                ResourceTaskResult result = new ResourceTaskResult(params[0]);

                try {
                    resourceDao.open(Resource.class);
                } catch (SQLException e) {
                    e.printStackTrace();
                    result.setTaskResultType(TaskResultType.FAILURE);
                    return result;
                }

                List<Resource> resources = result.getResources();
                Resource res = new Resource();

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

    public void saveResourceLocal(Resource resource) {
        new AsyncTask<Resource, Void, ResourceTaskResult>() {
            @Override
            protected ResourceTaskResult doInBackground(Resource... params) {
                ResourceTaskResult result;
                try {
                    resourceDao.open(Resource.class);
                    resourceDao.save(params[0]);
                    result = new ResourceTaskResult(ResourceTaskType.SAVE_RESOURCE_FROM_ME_LOCAL, TaskResultType.RESOURCE_SAVED);
                } catch (SQLException e) {
                    e.printStackTrace();
                    result = new ResourceTaskResult(ResourceTaskType.SAVE_RESOURCE_FROM_ME_LOCAL, TaskResultType.RESOURCE_NOT_SAVED);
                } finally {
                    resourceDao.close();
                }

                result.addResource(params[0]);

                return result;
            }

            @Override
            protected void onPostExecute(ResourceTaskResult result) {
                bus.post(new ResourceSaveCompletedEvent(result));
            }
        }.execute(resource);
    }

    public void updateResourceSentToBackend(Long id) {
        new AsyncTask<Long, Void, ResourceTaskResult>() {
            @Override
            protected ResourceTaskResult doInBackground(Long... params) {
                ResourceTaskResult result;

                List<Resource> resources = null;

                try {
                    resourceDao.open(Resource.class);

                    Resource res = new Resource();
                    res.setAndroidId(params[0]);

                    resources = resourceDao.read(res);

                    if (resources == null || resources.size() > 1) {
                        throw new IllegalStateException("Error while reading resource from DB");
                    }

                    res = resources.get(0);
                    res.setSentToBackend(true);
                    resourceDao.update(res);

                    result = new ResourceTaskResult(ResourceTaskType.UPDATE_RESOURCE_SENT_TO_BACKEND, TaskResultType.SUCCESS);
                } catch (SQLException e) {
                    e.printStackTrace();
                    result = new ResourceTaskResult(ResourceTaskType.UPDATE_RESOURCE_SENT_TO_BACKEND, TaskResultType.FAILURE);
                } finally {
                    resourceDao.close();
                }

                if (resources != null) {
                    result.addResource(resources.get(0));
                }

                return result;
            }

            @Override
            protected void onPostExecute(ResourceTaskResult result) {
                bus.post(new ResourceSaveCompletedEvent(result));
            }
        }.execute(id);
    }
}
