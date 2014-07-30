package info.ferrarimarco.uniroma2.msa.resourcesharing.app.tasks.resource;

import java.sql.SQLException;
import java.util.List;

import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.Resource;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.ResourceType;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.task.ResourceTaskResult;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.task.ResourceTaskType;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.task.TaskResultType;

/**
 * Created by Ferrarim on 30/07/2014.
 */
public class ReadAllResourcesAsyncTask extends AbstractResourceAsyncTask {

    private ResourceTaskType taskType;

    @Override
    protected ResourceTaskResult doInBackground(String... params) {

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

        switch (taskType) {
            case READ_NEW_RESOURCES:
                res.setType(ResourceType.NEW);
                break;
            case READ_CREATED_BY_ME_RESOURCES:
                res.setType(ResourceType.CREATED_BY_ME);
                break;
            case READ_ARCHIVED_RESOURCES:
                res.setType(ResourceType.ARCHIVED);
                break;
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

    public ResourceTaskType getTaskType() {
        return taskType;
    }

    public void setTaskType(ResourceTaskType taskType) {
        this.taskType = taskType;
    }
}
