package info.ferrarimarco.uniroma2.msa.resourcesharing.app.tasks.resource;

import org.joda.time.DateTime;

import java.sql.SQLException;

import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.Resource;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.ResourceType;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.task.ResourceTaskResult;

public class SaveResourceAsyncTask extends AbstractResourceAsyncTask {

    @Override
    protected ResourceTaskResult doInBackground(String... params) {

        try {
            resourceDao.open(Resource.class);

            String title = params[0];
            String description = params[1];
            String acquisitionMode = params[2];
            String location = params[3];
            String currentUserName = params[4];

            Resource newRes = new Resource(title, description, location, DateTime.now(), acquisitionMode, currentUserName, ResourceType.CREATED_BY_ME, Boolean.FALSE);
            resourceDao.save(newRes);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            resourceDao.close();
        }


        return null;
    }
}
