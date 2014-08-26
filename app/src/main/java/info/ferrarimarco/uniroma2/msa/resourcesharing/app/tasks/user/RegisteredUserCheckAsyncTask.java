package info.ferrarimarco.uniroma2.msa.resourcesharing.app.tasks.user;

import java.sql.SQLException;
import java.util.List;

import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.User;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.task.TaskResult;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.task.UserTaskResult;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.task.UserTaskType;

public class RegisteredUserCheckAsyncTask extends AbstractUserAsyncTask {


    @Override
    protected UserTaskResult doInBackground(String... params) {

        UserTaskResult result = new UserTaskResult(UserTaskType.CHECK_REGISTERED_USER);

        try {
            userDao.open(User.class);
        } catch (SQLException e) {
            e.printStackTrace();
            result.setTaskResult(TaskResult.FAILURE);
            result.setMessage(e.getMessage());
            return result;
        }

        User registeredUser = new User();

        String registeredUserId = String.valueOf(params[0]);
        Long registered_user_id = Long.parseLong(registeredUserId);
        registeredUser.setId(registered_user_id);

        List<User> users;

        try {
            users = userDao.read(registeredUser);
            userDao.close();
        } catch (SQLException e) {
            e.printStackTrace();
            result.setTaskResult(TaskResult.FAILURE);
            result.setMessage(e.getMessage());
            return result;
        }

        result.setTaskResult(TaskResult.SUCCESS);

        // Check if there is a registered user
        if (users != null && !users.isEmpty()) {
            result.setRegisteredUserPresent(Boolean.TRUE);
            result.setResultUser(users.get(0));
        }

        return result;
    }
}
