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

public class GcmMessagingServiceImpl {

    enum GcmMessage {

        // D2C = Device to Cloud
        REGISTRATION_REQUEST("info.ferrarimarco.uniroma2.msa.resourcesharing.app.gcm.message.REGISTER"),
        NEW_RESOURCE_FROM_ME("info.ferrarimarco.uniroma2.msa.resourcesharing.app.gcm.message.CREATE_NEW_RESOURCE"),
        DELETE_MY_RESOURCE("info.ferrarimarco.uniroma2.msa.resourcesharing.app.gcm.message.DELETE_RESOURCE"),
        UPDATE_USER_DETAILS("info.ferrarimarco.uniroma2.msa.resourcesharing.app.gcm.message.UPDATE_USER_DETAILS"),

        // C2D = Cloud to Device
        NEW_RESOURCE_FROM_OTHERS("info.ferrarimarco.uniroma2.msa.resourcesharing.app.gcm.message.NEW_RESOURCE_BY_OTHERS"),
        DELETE_RESOURCE_BY_OTHERS("info.ferrarimarco.uniroma2.msa.resourcesharing.app.gcm.message.DELETE_RESOURCE_BY_OTHERS"),

        // D2D = Device to Device
        CLEAR_RESOURCE_NOTIFICATION("info.ferrarimarco.uniroma2.msa.resourcesharing.app.gcm.message.CLEAR_RESOURCE"),

        // Replies
        C2D_RESPONSE("info.ferrarimarco.uniroma2.msa.resourcesharing.app.gcm.message.C2D_RESPONSE"),
        D2C_RESPONSE("info.ferrarimarco.uniroma2.msa.resourcesharing.app.gcm.message.D2C_RESPONSE"),
        D2D_RESPONSE("info.ferrarimarco.uniroma2.msa.resourcesharing.app.gcm.message.D2D_RESPONSE");

        private String stringValue;

        private GcmMessage(String stringValue) {
            this.stringValue = stringValue;
        }

        public String getStringValue() {
            return stringValue;
        }
    }

    @Inject
    GoogleCloudMessaging gcm;

    @Inject
    SharedPreferencesServiceImpl sharedPreferencesService;

    @Inject
    Context context;

    private Long gcmTtl;
    private static final String GCM_SUFFIX = "@gcm.googleapis.com";
    protected AtomicInteger messageId;
    private String gcmRegistrationId;
    private String gcmProjectRecipient;

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
                    gcmRegistrationId = gcm.register(context.getResources().getString(R.string.gcm_project_id));

                    Log.d(GcmMessagingServiceImpl.class.getName(), "GCM Registration ID: " + gcmRegistrationId);

                    Bundle data = new Bundle();
                    data.putString("userId", userId);
                    data.putString("action", GcmMessage.REGISTRATION_REQUEST.getStringValue());

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

    public void sendNewResource(Resource resource, User currentUser) {
        Bundle data = new Bundle();

        data.putString("action", GcmMessage.NEW_RESOURCE_FROM_ME.getStringValue());
        data.putString("title", resource.getTitle());
        data.putString("description", resource.getDescription());
        data.putString("location", resource.getLocation());
        data.putLong("creationTime", resource.getCreationTime().getMillis());
        data.putString("acquisitionMode", resource.getAcquisitionMode());
        data.putString("creatorId", currentUser.getEmail());
        data.putString("creatorPassword", currentUser.getHashedPassword());

        sendGcmMessage(data);
    }

    public void deleteResource(Resource resource, User currentUser) {
        Bundle data = new Bundle();

        data.putString("action", GcmMessage.DELETE_MY_RESOURCE.getStringValue());
        data.putLong("creationTime", resource.getCreationTime().getMillis());
        data.putString("creatorId", currentUser.getEmail());
        data.putString("creatorPassword", currentUser.getHashedPassword());

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

    private void sendGcmMessage(Bundle data) {
        if (gcmRegistrationId == null || gcmRegistrationId.isEmpty()) {
            // TODO: handle this error condition
            return;
        }

        new AsyncTask<Bundle, Void, Void>() {
            @Override
            protected Void doInBackground(Bundle... params) {
                if (params.length != 1) {
                    throw new IllegalArgumentException("No (or wrong) bundle specified while sending GCM message to backend");
                }

                Bundle data = params[0];

                try {
                    String id = Integer.toString(messageId.incrementAndGet());
                    gcm.send(gcmProjectRecipient, id, gcmTtl, data);
                } catch (IOException ex) {
                    Log.e(GcmMessagingServiceImpl.class.getName(), "Error :" + ex.getMessage());
                    // TODO: handle this error
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void v) {
                // TODO: is this needed?
                // Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            }
        }.execute(data);
    }
}
