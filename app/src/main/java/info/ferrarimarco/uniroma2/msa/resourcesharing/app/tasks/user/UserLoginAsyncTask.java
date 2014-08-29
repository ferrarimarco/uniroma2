package info.ferrarimarco.uniroma2.msa.resourcesharing.app.tasks.user;

import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.task.TaskResultType;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.task.UserTaskResult;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.task.UserTaskType;

/**
 * Represents an asynchronous login/registration task used to authenticate
 * the user.
 */
public class UserLoginAsyncTask extends AbstractUserAsyncTask {

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };

    @Override
    protected UserTaskResult doInBackground(String... params) {
        // TODO: attempt authentication against a network service.

        String userId = params[0];
        String password = params[0];

        try {
            // Simulate network access.
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            return new UserTaskResult(UserTaskType.USER_LOGIN, TaskResultType.FAILURE, this.getTaskId());
        }

        for (String credential : DUMMY_CREDENTIALS) {
            String[] pieces = credential.split(":");
            if (pieces[0].equals(userId)) {
                // Account exists, return true if the password matches.
                if (pieces[1].equals(password)) {
                    return new UserTaskResult(UserTaskType.USER_LOGIN, TaskResultType.SUCCESS, this.getTaskId());
                }
            }
        }

        return new UserTaskResult(UserTaskType.USER_LOGIN, TaskResultType.SUCCESS, this.getTaskId());
    }


}
