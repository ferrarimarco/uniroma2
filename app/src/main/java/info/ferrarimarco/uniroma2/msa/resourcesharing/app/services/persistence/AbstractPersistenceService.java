package info.ferrarimarco.uniroma2.msa.resourcesharing.app.services.persistence;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import dagger.ObjectGraph;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.callers.AsyncCaller;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.task.AbstractTaskResult;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.tasks.AbstractAsyncTask;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.tasks.user.AbstractUserAsyncTask;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.util.ObjectGraphUtils;

public abstract class AbstractPersistenceService implements AsyncCaller {

    protected Map<Integer, AsyncCaller> taskIdToCallerMap;

    protected ObjectGraph objectGraph;

    @Inject
    Context context;

    @Inject
    public AbstractPersistenceService(Context context) {
        taskIdToCallerMap = new HashMap<>();
        objectGraph = ObjectGraphUtils.getObjectGraph(context);
    }

    @Override
    public void onBackgroundTaskCompleted(Object result) {
        if (result instanceof AbstractTaskResult) {
            AbstractTaskResult taskResult = (AbstractTaskResult) result;
            taskIdToCallerMap.remove(taskResult.getCompletedTaskId());
        }
    }

    @Override
    public void onBackgroundTaskCancelled(Object cancelledTask) {
        if (cancelledTask instanceof AbstractAsyncTask) {
            AbstractUserAsyncTask task = (AbstractUserAsyncTask) cancelledTask;
            taskIdToCallerMap.remove(task.getTaskId());
            AsyncCaller caller = taskIdToCallerMap.get(task.getTaskId());
            caller.onBackgroundTaskCancelled(cancelledTask);
        }
    }
}
