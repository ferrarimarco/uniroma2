package info.ferrarimarco.uniroma2.msa.resourcesharing.app.tasks;

import info.ferrarimarco.uniroma2.msa.resourcesharing.app.callers.AsyncCaller;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.UserTaskResult;

/**
 * Created by FerrariM on 08/04/2014.
 */
public class RegisterNewUserAsyncTask extends UserLoginAsyncTask {


    public RegisterNewUserAsyncTask(AsyncCaller caller, String email, String password) {
        super(caller, email, password);
    }

    @Override
    protected UserTaskResult doInBackground(Void... params) {

        try {
            // Simulate network access.
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            return new UserTaskResult(UserTaskResult.UserTaskType.REGISTER_NEW_USER, UserTaskResult.UserTaskResultType.FAILURE);
        }

        return new UserTaskResult(UserTaskResult.UserTaskType.REGISTER_NEW_USER, UserTaskResult.UserTaskResultType.SUCCESS);
    }
}
