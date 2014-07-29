package info.ferrarimarco.uniroma2.msa.resourcesharing.app.tasks;

import android.content.Context;
import android.os.AsyncTask;

import javax.inject.Inject;

import info.ferrarimarco.uniroma2.msa.resourcesharing.app.callers.AsyncCaller;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.dao.GenericDao;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.User;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.UserTaskResult;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.services.impl.HashingServiceImpl;

/**
 * Created by Marco on 28/07/2014.
 */
public abstract class UserAsyncTask extends AsyncTask<String, Void, UserTaskResult> {

    private AsyncCaller caller;

    protected Context context;

    @Inject
    GenericDao<User> userDao;

    @Inject
    HashingServiceImpl hashingService;

    public void initTask(AsyncCaller caller, Context context) {
        this.caller = caller;
        this.context = context;
    }

    protected abstract UserTaskResult doInBackground(String... params);

    @Override
    protected void onPostExecute(final UserTaskResult result) {
        caller.onBackgroundTaskCompleted(result);
    }

    @Override
    protected void onCancelled() {
        caller.onBackgroundTaskCancelled();
    }
}
