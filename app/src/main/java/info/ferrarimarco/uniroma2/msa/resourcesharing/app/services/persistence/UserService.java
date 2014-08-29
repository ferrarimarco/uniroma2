package info.ferrarimarco.uniroma2.msa.resourcesharing.app.services.persistence;

import android.content.Context;

import javax.inject.Inject;

import info.ferrarimarco.uniroma2.msa.resourcesharing.app.R;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.callers.AsyncCaller;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.User;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.task.UserTaskResult;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.task.UserTaskType;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.tasks.user.RegisterNewUserAsyncTask;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.tasks.user.RegisteredUserCheckAsyncTask;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.tasks.user.UserLoginAsyncTask;

public class UserService extends AbstractPersistenceService {

    private static User currentUser;

    @Inject
    public UserService(Context context) {
        super(context);
    }

    public void readRegisteredUserAsync(AsyncCaller caller) {
        if (currentUser == null) {
            RegisteredUserCheckAsyncTask registeredUserCheckTask = objectGraph.get(RegisteredUserCheckAsyncTask.class);
            registeredUserCheckTask.initTask(this);
            taskIdToCallerMap.put(registeredUserCheckTask.getTaskId(), caller);
            registeredUserCheckTask.execute(context.getResources().getString(R.string.registered_user_id));
        } else {
            UserTaskResult result = new UserTaskResult(-1);
            result.setResultUser(currentUser);
            onBackgroundTaskCompleted(result);
        }
    }

    public User readRegisteredUserSync() {
        if (currentUser == null) {
            RegisteredUserCheckAsyncTask task = new RegisteredUserCheckAsyncTask();
            currentUser = task.readRegisteredUserSync(context.getResources().getString(R.string.registered_user_id));
        }
        return currentUser;
    }

    public void checkForUserValidity(AsyncCaller caller, String userId) {

    }

    public void registerNewUser(AsyncCaller caller, String userId, String password) {
        RegisterNewUserAsyncTask registerNewUserAsyncTask = objectGraph.get(RegisterNewUserAsyncTask.class);
        registerNewUserAsyncTask.initTask(this);
        taskIdToCallerMap.put(registerNewUserAsyncTask.getTaskId(), caller);
        registerNewUserAsyncTask.execute(userId, password);
    }

    public void loginExistingUser(AsyncCaller caller, String userId, String hashedPassword) {
        UserLoginAsyncTask userLoginAsyncTask = objectGraph.get(UserLoginAsyncTask.class);
        userLoginAsyncTask.initTask(this);
        taskIdToCallerMap.put(userLoginAsyncTask.getTaskId(), caller);
        userLoginAsyncTask.execute(userId, hashedPassword);
    }

    @Override
    public void onBackgroundTaskCompleted(Object result) {
        if (result instanceof UserTaskResult) {
            UserTaskResult taskResult = (UserTaskResult) result;
            AsyncCaller caller = taskIdToCallerMap.get(taskResult.getCompletedTaskId());

            if (taskResult.getTaskType().equals(UserTaskType.CHECK_REGISTERED_USER)) {
                if (currentUser == null && taskResult.getResultUser() != null) {
                    currentUser = taskResult.getResultUser();
                }

                caller.onBackgroundTaskCompleted(taskResult.getResultUser());
            } else if (taskResult.getTaskType().equals(UserTaskType.REGISTER_NEW_USER)) {
                caller.onBackgroundTaskCompleted(taskResult);
            } else if (taskResult.getTaskType().equals(UserTaskType.USER_LOGIN)) {
                caller.onBackgroundTaskCompleted(taskResult);
            }
        }

        super.onBackgroundTaskCompleted(result);
    }
}
