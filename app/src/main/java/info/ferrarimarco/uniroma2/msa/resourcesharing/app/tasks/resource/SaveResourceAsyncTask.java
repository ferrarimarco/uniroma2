package info.ferrarimarco.uniroma2.msa.resourcesharing.app.tasks.resource;

import org.joda.time.DateTime;

import java.sql.SQLException;

import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.Resource;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.task.ResourceTaskResult;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.task.ResourceTaskType;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.task.TaskResultType;

public class SaveResourceAsyncTask extends AbstractResourceAsyncTask {

    @Override
    protected ResourceTaskResult doInBackground(String... params) {
        String title = params[0];
        String description = params[1];
        String acquisitionMode = params[2];
        String location = params[3];
        String currentUserId = params[4];

        Resource newRes = new Resource(title, description, location, DateTime.now(), acquisitionMode, currentUserId, Resource.ResourceType.CREATED_BY_ME, Boolean.FALSE);

        try {
            resourceDao.open(Resource.class);
            resourceDao.save(newRes);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ResourceTaskResult(ResourceTaskType.SAVE_RESOURCE, this.getTaskId(), TaskResultType.FAILURE);
        } finally {
            resourceDao.close();
        }

        gcmMessagingService.sendNewResource(newRes);

        return new ResourceTaskResult(ResourceTaskType.SAVE_RESOURCE, this.getTaskId(), TaskResultType.SUCCESS);
    }
}
