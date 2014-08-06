package info.ferrarimarco.uniroma2.msa.resourcesharing.app.callers;

public interface AsyncCaller {
	void onBackgroundTaskCompleted(Object result);

	void onBackgroundTaskCancelled(Object cancelledTask);
}
