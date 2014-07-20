package info.ferrarimarco.uniroma2.msa.resourcesharing.app.tasks;

import org.apache.commons.codec.binary.Hex;
import org.joda.time.DateTime;

import java.sql.SQLException;

import info.ferrarimarco.uniroma2.msa.resourcesharing.app.R;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.User;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.UserTaskResult;

/**
 * Created by FerrariM on 08/04/2014.
 */
public class RegisterNewUserAsyncTask extends UserLoginAsyncTask {

    @Override
    protected UserTaskResult doInBackground(Void... params) {
        // Register the user in local DB

        try {
            userDao.open(User.class);
        } catch (SQLException e) {
            return new UserTaskResult(UserTaskResult.UserTaskType.REGISTER_NEW_USER, UserTaskResult.UserTaskResultType.FAILURE, e.getMessage());
        }
        String hashedPassword = new String(Hex.encodeHex(hashingService.hash(password)));
        User user = new User(userId, userId, hashedPassword, DateTime.now());

        Long registered_user_id = Long.parseLong(context.getResources().getString(R.string.registered_user_id));
        user.setId(registered_user_id);

        try {
            userDao.save(user);
            userDao.close();
        } catch (SQLException e) {
            return new UserTaskResult(UserTaskResult.UserTaskType.REGISTER_NEW_USER, UserTaskResult.UserTaskResultType.FAILURE, e.getMessage());
        }

        return new UserTaskResult(UserTaskResult.UserTaskType.REGISTER_NEW_USER, UserTaskResult.UserTaskResultType.SUCCESS);
    }
}
