package info.ferrarimarco.uniroma2.msa.resourcesharing.app.services.gcm;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;

import info.ferrarimarco.uniroma2.msa.resourcesharing.app.R;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.Resource;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.User;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.services.impl.SharedPreferencesServiceImpl;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.services.persistence.UserService;

public class GcmMessagingServiceImpl {

    enum GcmMessage {

        USER_ID_CHECK("info.ferrarimarco.uniroma2.msa.resourcesharing.app.gcm.message.USER_ID_CHECK"),
        REGISTRATION("info.ferrarimarco.uniroma2.msa.resourcesharing.app.gcm.message.REGISTRATION"),
        NEW_RESOURCE_FROM_ME("info.ferrarimarco.uniroma2.msa.resourcesharing.app.gcm.message.CREATE_NEW_RESOURCE"),
        DELETE_MY_RESOURCE("info.ferrarimarco.uniroma2.msa.resourcesharing.app.gcm.message.DELETE_RESOURCE"),
        UPDATE_USER_DETAILS("info.ferrarimarco.uniroma2.msa.resourcesharing.app.gcm.message.UPDATE_USER_DETAILS"),
        NEW_RESOURCE_FROM_OTHERS("info.ferrarimarco.uniroma2.msa.resourcesharing.app.gcm.message.NEW_RESOURCE_BY_OTHERS"),
        DELETE_RESOURCE_BY_OTHERS("info.ferrarimarco.uniroma2.msa.resourcesharing.app.gcm.message.DELETE_RESOURCE_BY_OTHERS"),

        // D2D = Device to Device
        CLEAR_RESOURCE_NOTIFICATION("info.ferrarimarco.uniroma2.msa.resourcesharing.app.gcm.message.CLEAR_RESOURCE");

        private String stringValue;

        private GcmMessage(String stringValue) {
            this.stringValue = stringValue;
        }

        public String getStringValue() {
            return stringValue;
        }
    }

    private GoogleCloudMessaging gcm;

    @Inject
    SharedPreferencesServiceImpl sharedPreferencesService;

    @Inject
    UserService userService;

    @Inject
    Context context;

    private Long gcmTtl;
    private static final String GCM_SUFFIX = "@gcm.googleapis.com";
    protected AtomicInteger messageId;
    private String gcmRegistrationId;
    private String gcmProjectRecipient;

    @Inject
    public GcmMessagingServiceImpl() {
        gcmTtl = Long.parseLong(String.valueOf(context.getResources().getString(R.string.gcm_ttl)));
        String gcmProjectId = context.getResources().getString(R.string.gcm_project_id);
        gcmProjectRecipient = gcmProjectId + GCM_SUFFIX;
        messageId = new AtomicInteger();
    }

    /**
     * Registers the application with GCM servers asynchronously.
     * <p/>
     * Stores the registration ID in the application's
     * shared preferences.
     */
    public void registerWithGcm(User currentUser) {
        new AsyncTask<User, Void, Void>() {
            @Override
            protected Void doInBackground(User... params) {
                if (params.length != 1) {
                    throw new IllegalArgumentException("No user specified while registering with backend");
                }

                String userId = params[0].getEmail();
                Log.d(GcmMessagingServiceImpl.class.getName(), "Send GCM registration Id to backend for: " + userId);

                try {
                    gcm = GoogleCloudMessaging.getInstance(context);
                    gcmRegistrationId = gcm.register(context.getResources().getString(R.string.gcm_project_id));

                    Log.d(GcmMessagingServiceImpl.class.getName(), "GCM Registration ID: " + gcmRegistrationId);

                    Bundle data = new Bundle();
                    data.putString("userId", userId);
                    data.putString("action", GcmMessage.REGISTRATION.getStringValue());
                    data.putBoolean("needsAck", true);

                    sendGcmMessage(data);

                    // Persist the regID - no need to register again.
                    sharedPreferencesService.storeGcmRegistrationId(gcmRegistrationId);
                } catch (IOException ex) {
                    Log.e(GcmMessagingServiceImpl.class.getName(), "Error :" + ex.getMessage());
                    // TODO: handle this error
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }

                return null;
            }
        }.execute(currentUser);
    }

    public void sendNewResource(Resource resource) {
        Bundle data = new Bundle();

        User currentUser = userService.readRegisteredUserSync();

        data.putString("action", GcmMessage.NEW_RESOURCE_FROM_ME.getStringValue());
        data.putString("title", resource.getTitle());
        data.putString("description", resource.getDescription());
        data.putString("location", resource.getLocation());
        data.putLong("creationTime", resource.getCreationTime().getMillis());
        data.putString("acquisitionMode", resource.getAcquisitionMode());
        data.putString("creatorId", currentUser.getEmail());
        data.putString("creatorPassword", currentUser.getHashedPassword());
        data.putBoolean("needsAck", true);

        sendGcmMessage(data);
    }

    public void deleteResource(Resource resource) {
        Bundle data = new Bundle();

        User currentUser = userService.readRegisteredUserSync();

        data.putString("action", GcmMessage.DELETE_MY_RESOURCE.getStringValue());
        data.putLong("creationTime", resource.getCreationTime().getMillis());
        data.putString("creatorId", currentUser.getEmail());
        data.putString("creatorPassword", currentUser.getHashedPassword());
        data.putBoolean("needsAck", true);

        sendGcmMessage(data);
    }

    public void updateUserDetails(User currentUser, String currentPosition) {
        Bundle data = new Bundle();
        data.putString("action", GcmMessage.UPDATE_USER_DETAILS.getStringValue());
        data.putString("userName", currentUser.getName());
        data.putString("userId", currentUser.getEmail());
        data.putString("hashedPassword", currentUser.getHashedPassword());
        data.putString("currentPosition", currentPosition);

        sendGcmMessage(data);
    }

    public void checkUserIdValidity(String userId) {
        Bundle data = new Bundle();
        data.putString("action", GcmMessage.USER_ID_CHECK.getStringValue());
        data.putString("userId", userId);
        data.putBoolean("needsAck", true);

        sendGcmMessage(data);
    }

    public void registerNewUser(User currentUser) {
        Bundle data = new Bundle();
        data.putString("action", GcmMessage.REGISTRATION.getStringValue());
        data.putString("userName", currentUser.getName());
        data.putString("userId", currentUser.getEmail());
        data.putString("hashedPassword", currentUser.getHashedPassword());
        data.putBoolean("needsAck", true);

        sendGcmMessage(data);
    }

    private void sendGcmMessage(Bundle data) {
        if (gcmRegistrationId == null || gcmRegistrationId.isEmpty()) {
            // TODO: handle this error condition
            return;
        }

        new AsyncTask<Bundle, Void, Integer>() {
            @Override
            protected Integer doInBackground(Bundle... params) {
                if (params.length != 1) {
                    throw new IllegalArgumentException("No (or wrong) bundle specified while sending GCM message to backend");
                }

                Bundle data = params[0];

                if (!data.containsKey("needsAck")) {
                    data.putBoolean("needsAck", false);
                }

                String id = Integer.toString(messageId.incrementAndGet());

                try {
                    gcm = GoogleCloudMessaging.getInstance(context);
                    gcm.send(gcmProjectRecipient, id, gcmTtl, data);
                } catch (IOException ex) {
                    Log.e(GcmMessagingServiceImpl.class.getName(), "Error :" + ex.getMessage());
                    return -1;
                }

                return Integer.parseInt(id);
            }

            @Override
            protected void onPostExecute(Integer returnCode) {
                // TODO: is this needed?
                // Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            }
        }.execute(data);
    }
}
