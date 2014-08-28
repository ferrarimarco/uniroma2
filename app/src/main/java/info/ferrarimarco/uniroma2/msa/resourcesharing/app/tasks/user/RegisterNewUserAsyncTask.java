package info.ferrarimarco.uniroma2.msa.resourcesharing.app.tasks.user;

import org.apache.commons.codec.binary.Hex;
import org.joda.time.DateTime;

import java.sql.SQLException;

import info.ferrarimarco.uniroma2.msa.resourcesharing.app.R;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.User;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.task.TaskResult;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.task.UserTaskResult;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.task.UserTaskType;

public class RegisterNewUserAsyncTask extends AbstractUserAsyncTask {

    @Override
    protected UserTaskResult doInBackground(String... params) {
        // Register the user in local DB

        String userId = params[0];
        String password = params[1];

        try {
            userDao.open(User.class);
        } catch (SQLException e) {
            return new UserTaskResult(UserTaskType.REGISTER_NEW_USER, TaskResult.FAILURE, this.getTaskId(), e.getMessage());
        }
        String hashedPassword = new String(Hex.encodeHex(hashingService.hash(password)));
        User user = new User(userId, userId, hashedPassword, DateTime.now());

        Long registered_user_id = Long.parseLong(context.getResources().getString(R.string.registered_user_id));
        user.setId(registered_user_id);

        try {
            userDao.save(user);
            userDao.close();
        } catch (SQLException e) {
            return new UserTaskResult(UserTaskType.REGISTER_NEW_USER, TaskResult.FAILURE, this.getTaskId(), e.getMessage());
        }

        return new UserTaskResult(UserTaskType.REGISTER_NEW_USER, TaskResult.SUCCESS, this.getTaskId());
    }
}
