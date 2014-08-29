package info.ferrarimarco.uniroma2.msa.resourcesharing.app.tasks.user;

import java.sql.SQLException;
import java.util.List;

import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.User;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.task.TaskResultType;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.task.UserTaskResult;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.task.UserTaskType;

public class RegisteredUserCheckAsyncTask extends AbstractUserAsyncTask {

    @Override
    protected UserTaskResult doInBackground(String... params) {

        UserTaskResult result = new UserTaskResult(UserTaskType.CHECK_REGISTERED_USER, this.getTaskId());

        try {
            userDao.open(User.class);
        } catch (SQLException e) {
            e.printStackTrace();
            result.setTaskResultType(TaskResultType.FAILURE);
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
            result.setTaskResultType(TaskResultType.FAILURE);
            result.setMessage(e.getMessage());
            return result;
        }

        result.setTaskResultType(TaskResultType.SUCCESS);

        // Check if there is a registered user
        if (users != null && !users.isEmpty()) {
            result.setResultUser(users.get(0));
        }

        return result;
    }

    public User readRegisteredUserSync(String registeredUserId) {

        try {
            userDao.open(User.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        User registeredUser = new User();

        Long registered_user_id = Long.parseLong(registeredUserId);
        registeredUser.setId(registered_user_id);

        List<User> users = null;

        try {
            users = userDao.read(registeredUser);
            userDao.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Check if there is a registered user
        if (users != null && !users.isEmpty()) {
            registeredUser = users.get(0);
        }

        return registeredUser;
    }
}
