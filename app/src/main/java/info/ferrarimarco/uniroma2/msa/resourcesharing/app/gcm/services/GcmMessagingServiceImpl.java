package info.ferrarimarco.uniroma2.msa.resourcesharing.app.gcm.services;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;

import info.ferrarimarco.uniroma2.msa.resourcesharing.app.R;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.gcm.GcmMessage;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.services.impl.SharedPreferencesServiceImpl;

public class GcmMessagingServiceImpl {

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
    public void registerWithGcm(String userId) {
        new AsyncTask<String, Void, Void>() {
            @Override
            protected Void doInBackground(String... params) {
                String userId = params[0];

                if (params.length == 0 || params[0].isEmpty()) {
                    throw new IllegalArgumentException("No user id specified while registering with backend");
                }
                try {
                    gcmRegistrationId = gcm.register(context.getResources().getString(R.string.gcm_project_id));

                    Log.d(GcmMessagingServiceImpl.class.getName(), "GCM Registration ID: " + gcmRegistrationId);
                    Log.d(GcmMessagingServiceImpl.class.getName(), "Send GCM registration Id to backend for: " + userId);

                    Bundle data = new Bundle();
                    data.putString("name", userId);
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
        }.execute(userId);
    }

    private void sendGcmMessage(final Bundle data) {
        if (gcmRegistrationId == null || gcmRegistrationId.isEmpty()) {
            // TODO: handle this error condition
            return;
        }

        new AsyncTask<String, Void, Void>() {
            @Override
            protected Void doInBackground(String... params) {
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
        }.execute();
    }
}
