package info.ferrarimarco.uniroma2.msa.resourcesharing.app.tasks;

import android.content.Context;
import android.os.AsyncTask;

import info.ferrarimarco.uniroma2.msa.resourcesharing.app.callers.AsyncCaller;

public abstract class AbstractAsyncTask<I, R> extends AsyncTask<I, Void, R> {

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

    protected abstract R doInBackground(I... params);
}
