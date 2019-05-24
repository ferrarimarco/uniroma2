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

import java.util.List;

import javax.inject.Inject;

import dagger.ObjectGraph;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.R;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.activities.ShowResourcesActivity;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.Resource;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.services.config.SharedPreferencesServiceImpl;
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
    private static final int RESOURCE_ALREADY_BOOKED_NOTIFICATION_ID = 3;
    private static final int BOOKED_RESOURCE_DELETED_NOTIFICATION_ID = 4;

    private static final String ACTION_SAVE_RESOURCE_FROM_ME = "info.ferrarimarco.uniroma2.msa.resourcesharing.app.services.intent.action.SAVE_RESOURCE_FROM_ME";
    private static final String ACTION_RECEIVE_RESOURCE_FROM_OTHERS = "info.ferrarimarco.uniroma2.msa.resourcesharing.app.services.intent.action.RECEIVE_RESOURCE_FROM_OTHERS";
    private static final String ACTION_BOOK_RESOURCE_FROM_ME = "info.ferrarimarco.uniroma2.msa.resourcesharing.app.services.intent.action.BOOK_RESOURCE_FROM_ME";
    private static final String ACTION_BOOK_RESOURCE_FROM_OTHERS = "info.ferrarimarco.uniroma2.msa.resourcesharing.app.services.intent.action.BOOK_RESOURCE_FROM_OTHERS";
    private static final String ACTION_DELETE_RESOURCE_FROM_ME = "info.ferrarimarco.uniroma2.msa.resourcesharing.app.services.intent.action.DELETE_RESOURCE_FROM_ME";
    private static final String ACTION_RESOURCE_ALREADY_BOOKED = "info.ferrarimarco.uniroma2.msa.resourcesharing.app.services.intent.action.RESOURCE_ALREADY_BOOKED";
    private static final String ACTION_BOOKED_RESOURCE_DELETED = "info.ferrarimarco.uniroma2.msa.resourcesharing.app.services.intent.action.BOOKED_RESOURCE_DELETED";

    public static final String EXTRA_PARAM_RESOURCE = "info.ferrarimarco.uniroma2.msa.resourcesharing.app.services.intent.extra.RESOURCE";

    @Inject
    ResourceService resourceService;

    @Inject
    GcmMessagingServiceImpl gcmMessagingService;

    @Inject
    SharedPreferencesServiceImpl sharedPreferencesService;

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

    public static void startActionResourceFromOthersAlreadyBooked(Context context, Resource resource) {
        Intent intent = new Intent(context, ResourceIntentService.class);
        intent.setAction(ACTION_RESOURCE_ALREADY_BOOKED);
        intent.putExtra(EXTRA_PARAM_RESOURCE, resource);
        context.startService(intent);
    }

    public static void startActionBookedResourceDeleted(Context context, Resource resource) {
        Intent intent = new Intent(context, ResourceIntentService.class);
        intent.setAction(ACTION_BOOKED_RESOURCE_DELETED);
        intent.putExtra(EXTRA_PARAM_RESOURCE, resource);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            final Resource resource = intent.getParcelableExtra(EXTRA_PARAM_RESOURCE);
            switch (action) {

                case ACTION_SAVE_RESOURCE_FROM_ME: {
                    handleActionSaveResourceFromMe(resource);
                    break;
                }
                case ACTION_RECEIVE_RESOURCE_FROM_OTHERS: {
                    handleActionReceiveResourceFromOthers(resource);
                    break;
                }
                case ACTION_BOOK_RESOURCE_FROM_ME: {
                    handleActionBookResourceFromMe(resource);
                    break;
                }
                case ACTION_BOOK_RESOURCE_FROM_OTHERS: {
                    handleActionBookResourceFromOthers(resource);
                    break;
                }
                case ACTION_DELETE_RESOURCE_FROM_ME: {
                    handleActionDeleteResourceFromMe(resource);
                    break;
                }
                case ACTION_RESOURCE_ALREADY_BOOKED: {
                    handleActionResourceFromOthersAlreadyBooked(resource);
                    break;
                }
                case ACTION_BOOKED_RESOURCE_DELETED: {
                    handleActionBookedResourceFromOthersDeleted(resource);
                    break;
                }
            }
        }
    }

    private void handleActionSaveResourceFromMe(Resource resource) {
        resourceService.saveResourceLocal(resource);
        gcmMessagingService.sendNewResource(resource);
    }

    private void handleActionBookResourceFromOthers(Resource resource) {
        // delete resource from available resources list
        Resource tempResource = new Resource();
        tempResource.setAndroidId(resource.getAndroidId());
        resourceService.deleteResourceLocal(tempResource);

        // save the resource as booked by me
        resource.setBookerId(sharedPreferencesService.readRegisteredUserId());
        resource.setType(Resource.ResourceType.BOOKED_BY_ME);
        resourceService.saveResourceLocal(resource);

        // send booking request
        gcmMessagingService.bookResourceFromOthers(resource);
    }

    private void handleActionReceiveResourceFromOthers(Resource resource) {
        resourceService.saveResourceLocal(resource);
        showNewResourceNotification(resource);
    }

    private void handleActionDeleteResourceFromMe(Resource resource) {
        resourceService.deleteResourceLocal(resource);
        gcmMessagingService.deleteResourceFromMe(resource);
    }

    private void handleActionResourceFromOthersAlreadyBooked(Resource resource) {
        // delete resource from booked resources list
        Resource localResource = getLocalResourceByCriteria(resource);
        resourceService.deleteResourceLocal(localResource);

        showResourceAlreadyBookedNotification(localResource);
    }

    private void handleActionBookedResourceFromOthersDeleted(Resource resource) {
        // delete resource from booked resources list
        Resource localResource = getLocalResourceByCriteria(resource);
        resourceService.deleteResourceLocal(localResource);

        showBookedResourceDeletedNotification(localResource);
    }

    /**
     * Handle action book resource from me. This action sets the booker ID for a resource created
     * by the user.
     */
    private void handleActionBookResourceFromMe(Resource resource) {

        // For reference
        String bookerId = resource.getBookerId();

        // Remove booker id as it's not saved in local storage yet
        // and we have to build a criteria
        resource.setBookerId(null);

        // Update resource type
        resource.setType(Resource.ResourceType.CREATED_BY_ME);

        Resource resultResource = getLocalResourceByCriteria(resource);

        resultResource.setBookerId(bookerId);
        resourceService.updateResourceLocal(resultResource);

        showBookedResourceNotification(resource);
    }

    private Resource getLocalResourceByCriteria(Resource criteria) {
        List<Resource> result = resourceService.readResourceLocal(criteria, ResourceService.ResourceServiceOperationMode.SYNC).getResources();

        if (result == null || result.size() != 1) {
            throw new IllegalArgumentException("Cannot find a resource to book");
        }

        return result.get(0);
    }

    private void showNewResourceNotification(Resource resource) {
        showNotification(NEW_RESOURCE_NOTIFICATION_ID, getResources().getString(R.string.new_resource_notification_title), resource);
    }

    private void showBookedResourceNotification(Resource resource) {
        showNotification(BOOKED_RESOURCE_NOTIFICATION_ID, getResources().getString(R.string.booked_resource_notification_title), resource);
    }

    private void showResourceAlreadyBookedNotification(Resource resource) {
        showNotification(RESOURCE_ALREADY_BOOKED_NOTIFICATION_ID, getResources().getString(R.string.resource_already_booked_notification_title), resource);
    }

    private void showBookedResourceDeletedNotification(Resource resource) {
        showNotification(BOOKED_RESOURCE_DELETED_NOTIFICATION_ID, getResources().getString(R.string.booked_resource_deleted_notification_title), resource);
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
