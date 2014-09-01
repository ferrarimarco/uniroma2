package info.ferrarimarco.uniroma2.msa.resourcesharing.app.tasks.user;

import java.sql.SQLException;

import info.ferrarimarco.uniroma2.msa.resourcesharing.app.R;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.User;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.task.TaskResultType;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.task.UserTaskResult;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.task.UserTaskType;

public class DeleteRegisteredUserAsyncTask extends AbstractUserAsyncTask {

    @Override
    protected UserTaskResult doInBackground(String... params) {
        // Register the user in local DB

        try {
            userDao.open(User.class);
        } catch (SQLException e) {
            return new UserTaskResult(UserTaskType.DELETE_REGISTERED_USER, TaskResultType.FAILURE, this.getTaskId(), e.getMessage());
        }
        User user = new User();
        Long registered_user_id = Long.parseLong(context.getResources().getString(R.string.registered_user_id));
        user.setId(registered_user_id);

        try {
            userDao.delete(user);
            userDao.close();
        } catch (SQLException e) {
            return new UserTaskResult(UserTaskType.DELETE_REGISTERED_USER, TaskResultType.FAILURE, this.getTaskId(), e.getMessage());
        }

        return new UserTaskResult(UserTaskType.DELETE_REGISTERED_USER, TaskResultType.SUCCESS, this.getTaskId());
    }
}
