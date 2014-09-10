package info.ferrarimarco.uniroma2.msa.resourcesharing.app.services.intent;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.location.Location;

import javax.inject.Inject;

import info.ferrarimarco.uniroma2.msa.resourcesharing.app.services.gcm.GcmMessagingServiceImpl;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
public class UserIntentService extends IntentService{
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_UPDATE_USER_INFO = "info.ferrarimarco.uniroma2.msa.resourcesharing.app.services.intent.action.UPDATE_USER_INFO";

    private static final String EXTRA_PARAM_LOCATION = "info.ferrarimarco.uniroma2.msa.resourcesharing.app.services.intent.extra.LOCATION";

    /**
     * Starts this service to perform action UPDATE_USER_INFO with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionUpdateUserInfo(Context context, Location location){
        Intent intent = new Intent(context, UserIntentService.class);
        intent.setAction(ACTION_UPDATE_USER_INFO);
        intent.putExtra(EXTRA_PARAM_LOCATION, location);
        context.startService(intent);
    }

    @Inject
    GcmMessagingServiceImpl gcmMessagingService;

    public UserIntentService(){
        super("UserIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent){
        if(intent != null){
            final String action = intent.getAction();
            if(ACTION_UPDATE_USER_INFO.equals(action)){
                Location location = intent.getParcelableExtra(EXTRA_PARAM_LOCATION);
                handleActionUpdateUserInfo(location);
            }
        }
    }

    /**
     * Handle action UPDATE_USER_INFO in the provided background thread with the provided
     * parameters.
     */
    private void handleActionUpdateUserInfo(Location location){
        gcmMessagingService.updateUserDetails(location);
    }
}
