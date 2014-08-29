package info.ferrarimarco.uniroma2.msa.resourcesharing.app.tasks.resource;

import java.sql.SQLException;
import java.util.List;

import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.Resource;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.task.ResourceTaskResult;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.task.ResourceTaskType;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.task.TaskResultType;

public class ReadAllResourcesAsyncTask extends AbstractResourceAsyncTask {

    @Override
    protected ResourceTaskResult doInBackground(String... params) {

        // There was a failure while initialization
        if (result.getTaskResultType().equals(TaskResultType.FAILURE)) {
            return result;
        }

        List<Resource> resources = result.getResources();
        Resource res = new Resource();

        if (ResourceTaskType.READ_CREATED_BY_ME_RESOURCES.equals(taskType)) {
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
}
