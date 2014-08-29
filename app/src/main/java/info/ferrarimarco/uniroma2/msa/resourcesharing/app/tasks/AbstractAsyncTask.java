package info.ferrarimarco.uniroma2.msa.resourcesharing.app.tasks;

import android.content.Context;
import android.os.AsyncTask;

import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;

import info.ferrarimarco.uniroma2.msa.resourcesharing.app.callers.AsyncCaller;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.services.gcm.GcmMessagingServiceImpl;

public abstract class AbstractAsyncTask<I, R> extends AsyncTask<I, Void, R> {

    private AsyncCaller caller;

    @Inject
    protected GcmMessagingServiceImpl gcmMessagingService;

    @Inject
    protected
    Context context;

    protected static AtomicInteger taskIdGenerator;

    private int taskId;

    public AbstractAsyncTask() {
        if (taskIdGenerator == null) {
            taskIdGenerator = new AtomicInteger(0);
        }

        taskId = taskIdGenerator.getAndAdd(1);
    }

    public void initTask(AsyncCaller caller) {
        this.caller = caller;
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

    public int getTaskId() {
        return taskId;
    }
}
