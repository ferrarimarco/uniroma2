package info.ferrarimarco.uniroma2.msa.resourcesharing.app.tasks;

import java.sql.SQLException;
import java.util.List;

import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.User;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.UserTaskResult;

/**
 * Created by Marco on 25/07/2014.
 */
public class RegisteredUserCheckTask extends UserAsyncTask {


    @Override
    protected UserTaskResult doInBackground(String... params) {

        UserTaskResult result = new UserTaskResult(UserTaskResult.UserTaskType.CHECK_REGISTERED_USER);

        try {
            userDao.open(User.class);
        } catch (SQLException e) {
            e.printStackTrace();
            result.setUserTaskResultType(UserTaskResult.UserTaskResultType.FAILURE);
            result.setDetails(e.getMessage());
            return result;
        }

        User registeredUser = new User();

        String registeredUserId = String.valueOf(params[0]);
        Long registered_user_id = Long.parseLong(registeredUserId);
        registeredUser.setId(registered_user_id);

        List<User> users = null;

        try {
            users = userDao.read(registeredUser);
            userDao.close();
        } catch (SQLException e) {
            e.printStackTrace();
            result.setUserTaskResultType(UserTaskResult.UserTaskResultType.FAILURE);
            result.setDetails(e.getMessage());
            return result;
        }

        result.setUserTaskResultType(UserTaskResult.UserTaskResultType.SUCCESS);

        // Check if there is a registered user
        if (users != null && !users.isEmpty()) {
            result.setRegisteredUserPresent(Boolean.TRUE);
        }

        return result;
    }
}
