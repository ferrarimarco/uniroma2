package info.ferrarimarco.uniroma2.msa.resourcesharing.app.services.intent;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;

import javax.inject.Inject;

import dagger.ObjectGraph;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.R;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.activities.ShowResourcesActivity;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.Resource;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.task.ResourceTaskResult;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.task.TaskResultType;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.services.gcm.GcmMessagingServiceImpl;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.services.persistence.ResourceService;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.util.ObjectGraphUtils;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * helper methods.
 */
public class ResourceIntentService extends IntentService {

    private static final int NEW_RESOURCE_NOTIFICATION_ID = 1;
    private static final int BOOKED_RESOURCE_NOTIFICATION_ID = 2;

    private static final String ACTION_SAVE_RESOURCE_FROM_ME = "info.ferrarimarco.uniroma2.msa.resourcesharing.app.services.intent.action.SAVE_RESOURCE_FROM_ME";
    private static final String ACTION_RECEIVE_RESOURCE_FROM_OTHERS = "info.ferrarimarco.uniroma2.msa.resourcesharing.app.services.intent.action.RECEIVE_RESOURCE_FROM_OTHERS";
    private static final String ACTION_BOOK_RESOURCE_FROM_ME = "info.ferrarimarco.uniroma2.msa.resourcesharing.app.services.intent.action.BOOK_RESOURCE_FROM_ME";
    private static final String ACTION_BOOK_RESOURCE_FROM_OTHERS = "info.ferrarimarco.uniroma2.msa.resourcesharing.app.services.intent.action.BOOK_RESOURCE_FROM_OTHERS";
    private static final String ACTION_DELETE_RESOURCE_FROM_ME = "info.ferrarimarco.uniroma2.msa.resourcesharing.app.services.intent.action.DELETE_RESOURCE_FROM_ME";

    public static final String EXTRA_PARAM_RESOURCE = "info.ferrarimarco.uniroma2.msa.resourcesharing.app.services.intent.extra.RESOURCE";

    @Inject
    ResourceService resourceService;

    @Inject
    GcmMessagingServiceImpl gcmMessagingService;

    public ResourceIntentService() {
        super("ResourceIntentService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ObjectGraph objectGraph = ObjectGraphUtils.getObjectGraph(this);
        objectGraph.inject(this);
    }

    /**
     * Starts this service to perform action RECEIVE_RESOURCE_FROM_OTHERS with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionReceiveResourceFromOthers(Context context, Resource resource) {
        Intent intent = new Intent(context, ResourceIntentService.class);
        intent.setAction(ACTION_RECEIVE_RESOURCE_FROM_OTHERS);
        intent.putExtra(EXTRA_PARAM_RESOURCE, resource);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action SAVE_RESOURCE_FROM_ME with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionSaveResourceFromMe(Context context, Resource resource) {
        Intent intent = new Intent(context, ResourceIntentService.class);
        intent.setAction(ACTION_SAVE_RESOURCE_FROM_ME);
        intent.putExtra(EXTRA_PARAM_RESOURCE, resource);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action BOOK_RESOURCE_FROM_ME with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionBookResourceFromMe(Context context, Resource resource) {
        Intent intent = new Intent(context, ResourceIntentService.class);
        intent.setAction(ACTION_BOOK_RESOURCE_FROM_ME);
        intent.putExtra(EXTRA_PARAM_RESOURCE, resource);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action BOOK_RESOURCE_FROM_OTHERS with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionBookResourceFromOthers(Context context, Resource resource) {
        Intent intent = new Intent(context, ResourceIntentService.class);
        intent.setAction(ACTION_BOOK_RESOURCE_FROM_OTHERS);
        intent.putExtra(EXTRA_PARAM_RESOURCE, resource);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action DELETE_RESOURCE_FROM_ME with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionDeleteResourceFromMe(Context context, Resource resource) {
        Intent intent = new Intent(context, ResourceIntentService.class);
        intent.setAction(ACTION_DELETE_RESOURCE_FROM_ME);
        intent.putExtra(EXTRA_PARAM_RESOURCE, resource);
        context.startService(intent);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            switch (action) {
                case ACTION_SAVE_RESOURCE_FROM_ME: {
                    final Resource resource = intent.getParcelableExtra(EXTRA_PARAM_RESOURCE);
                    handleActionSaveResourceFromMe(resource);
                    break;
                }
                case ACTION_RECEIVE_RESOURCE_FROM_OTHERS: {
                    final Resource resource = intent.getParcelableExtra(EXTRA_PARAM_RESOURCE);
                    handleActionReceiveResourceFromOthers(resource);
                    break;
                }
                case ACTION_BOOK_RESOURCE_FROM_ME: {
                    final Resource resource = intent.getParcelableExtra(EXTRA_PARAM_RESOURCE);
                    handleActionBookResourceFromMe(resource);
                    break;
                }
                case ACTION_BOOK_RESOURCE_FROM_OTHERS: {
                    final Resource resource = intent.getParcelableExtra(EXTRA_PARAM_RESOURCE);
                    handleActionBookResourceFromOthers(resource);
                    break;
                }
                case ACTION_DELETE_RESOURCE_FROM_ME: {
                    final Resource resource = intent.getParcelableExtra(EXTRA_PARAM_RESOURCE);
                    handleActionDeleteResourceFromMe(resource);
                    break;
                }
            }
        }
    }

    /**
     * Handle action Save resource in the provided background thread with the provided
     * parameters.
     */
    private void handleActionSaveResourceFromMe(Resource resource) {
        ResourceTaskResult resourceTaskResult = resourceService.saveResourceLocal(resource);

        if (TaskResultType.FAILURE.equals(resourceTaskResult.getTaskResultType())) {
            throw new RuntimeException("Unable to save the resource into local storage");
        }

        gcmMessagingService.sendNewResource(resource);
    }

    /**
     * Handle action Save resource in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBookResourceFromOthers(Resource resource) {
        gcmMessagingService.bookResourceFromOthers(resource);
    }

    /**
     * Handle action Receive resource in the provided background thread with the provided
     * parameters.
     */
    private void handleActionReceiveResourceFromOthers(Resource resource) {
        ResourceTaskResult resourceTaskResult = resourceService.saveResourceLocal(resource);

        if (TaskResultType.FAILURE.equals(resourceTaskResult.getTaskResultType())) {
            throw new RuntimeException("Unable to save the resource into local storage");
        }

        showNewResourceNotification(resource);
    }

    /**
     * Handle action Book resource in the provided background thread with the provided
     * parameters.
     */
    private void handleActionDeleteResourceFromMe(Resource resource) {
        ResourceTaskResult resourceTaskResult = resourceService.deleteResourceLocal(resource);

        if (TaskResultType.FAILURE.equals(resourceTaskResult.getTaskResultType())) {
            throw new RuntimeException("Unable to delete the resource from local storage");
        }

        gcmMessagingService.deleteResourceFromMe(resource);
    }

    /**
     * Handle action delete resource in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBookResourceFromMe(Resource resource) {

        // For reference
        String bookerId = resource.getBookerId();

        // Remove booker id as it's not saved in local storage yet
        // and we have to build a criteria
        resource.setBookerId(null);

        Resource result = resourceService.readResourceFromLocalStorage(resource);
        result.setBookerId(bookerId);
        ResourceTaskResult resourceTaskResult = resourceService.saveResourceLocal(resource);

        if (TaskResultType.FAILURE.equals(resourceTaskResult.getTaskResultType())) {
            throw new RuntimeException("Unable to save the resource into local storage");
        }

        showBookedResourceNotification(resource);
    }

    private void showNewResourceNotification(Resource resource) {
        showNotification(NEW_RESOURCE_NOTIFICATION_ID, getResources().getString(R.string.new_resource_notification_title), resource);
    }

    private void showBookedResourceNotification(Resource resource) {
        showNotification(BOOKED_RESOURCE_NOTIFICATION_ID, getResources().getString(R.string.booked_resource_notification_title), resource);
    }

    private void showNotification(Integer notificationId, String notificationTitle, Resource resource) {
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(this, ShowResourcesActivity.class).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        Bundle bundle = new Bundle();
        bundle.putParcelable(EXTRA_PARAM_RESOURCE, resource);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack
        stackBuilder.addParentStack(ShowResourcesActivity.class);
        // Adds the Intent to the top of the stack
        stackBuilder.addNextIntent(intent);

        // Gets a PendingIntent containing the entire back stack
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder mBuilder = new Notification.Builder(this).setSmallIcon(R.drawable.ic_action_event).setContentTitle(resource.getTitle()).setStyle(new Notification.BigTextStyle().bigText(resource.getDescription())).setContentText(resource.getTitle()).setTicker(notificationTitle).setAutoCancel(true).setSound(Settings.System.DEFAULT_NOTIFICATION_URI).setContentIntent(contentIntent);

        notificationManager.notify(notificationId, mBuilder.build());
    }
}
