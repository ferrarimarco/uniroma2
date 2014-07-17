package info.ferrarimarco.uniroma2.msa.resourcesharing.app.tasks;

import org.apache.commons.codec.binary.Hex;
import org.joda.time.DateTime;

import java.sql.SQLException;

import info.ferrarimarco.uniroma2.msa.resourcesharing.app.R;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.callers.AsyncCaller;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.User;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.UserTaskResult;

/**
 * Created by FerrariM on 08/04/2014.
 */
public class RegisterNewUserAsyncTask extends UserLoginAsyncTask {

    private AsyncCaller caller;

    private String userId;
    private String password;

    @Override
    protected UserTaskResult doInBackground(Void... params) {
        // Register the user in local DB
        try{
            userDao.open(User.class);
        }catch(SQLException e){
            e.printStackTrace();
        }

        String hashedPassword = Hex.encodeHexString(hashingService.hash(password));
        User user = new User(userId, userId, hashedPassword, DateTime.now());
        user.setId((long) R.string.registered_user_id);

        try{
            userDao.save(user);
        }catch(SQLException e){
            e.printStackTrace();
        }

        userDao.close();

        try {
            // Simulate network access.
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            return new UserTaskResult(UserTaskResult.UserTaskType.REGISTER_NEW_USER, UserTaskResult.UserTaskResultType.FAILURE);
        }

        return new UserTaskResult(UserTaskResult.UserTaskType.REGISTER_NEW_USER, UserTaskResult.UserTaskResultType.SUCCESS);
    }
}
