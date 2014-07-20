package info.ferrarimarco.uniroma2.msa.resourcesharing.app.tasks;

import android.content.Context;
import android.os.AsyncTask;

import javax.inject.Inject;

import info.ferrarimarco.uniroma2.msa.resourcesharing.app.callers.AsyncCaller;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.dao.GenericDao;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.User;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.UserTaskResult;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.services.HashingService;

/**
 * Represents an asynchronous login/registration task used to authenticate
 * the user.
 */
public class UserLoginAsyncTask extends AsyncTask<Void, Void, UserTaskResult> {

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };

    protected String userId;
    protected String password;

    private AsyncCaller caller;
    protected Context context;

    @Inject
    HashingService hashingService;

    @Inject
    GenericDao<User> userDao;

    public void initTask(AsyncCaller caller, Context context, String userId, String password){
        this.caller = caller;
        this.context = context;
        this.userId = userId;
        this.password = password;
    }

    @Override
    protected UserTaskResult doInBackground(Void... params) {
        // TODO: attempt authentication against a network service.

        try {
            // Simulate network access.
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            return new UserTaskResult(UserTaskResult.UserTaskType.USER_LOGIN, UserTaskResult.UserTaskResultType.FAILURE);
        }

        for (String credential : DUMMY_CREDENTIALS) {
            String[] pieces = credential.split(":");
            if(pieces[0].equals(userId)){
                // Account exists, return true if the password matches.
                if(pieces[1].equals(password)){
                    return new UserTaskResult(UserTaskResult.UserTaskType.USER_LOGIN, UserTaskResult.UserTaskResultType.SUCCESS);
                }
            }
        }

        return new UserTaskResult(UserTaskResult.UserTaskType.USER_LOGIN, UserTaskResult.UserTaskResultType.SUCCESS);
    }

    @Override
    protected void onPostExecute(final UserTaskResult result) {
        caller.onBackgroundTaskCompleted(result);
    }

    @Override
    protected void onCancelled() {
        caller.onBackgroundTaskCancelled();
    }
}
