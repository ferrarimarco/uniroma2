package info.ferrarimarco.uniroma2.msa.resourcesharing.app.tasks;

import android.content.Context;
import android.os.AsyncTask;

import info.ferrarimarco.uniroma2.msa.resourcesharing.app.callers.AsyncCaller;


/**
 * Created by Ferrarim on 30/07/2014.
 */
public abstract class AbstractAsyncTask<R> extends AsyncTask<String, Void, R> {

    private AsyncCaller caller;

    protected Context context;

    public void initTask(AsyncCaller caller, Context context) {
        this.caller = caller;
        this.context = context;
    }

    @Override
    protected void onPostExecute(final R result) {
        caller.onBackgroundTaskCompleted(result);
    }

    @Override
    protected void onCancelled() {
        caller.onBackgroundTaskCancelled(this);
    }

    protected abstract R doInBackground(String... params);
}
