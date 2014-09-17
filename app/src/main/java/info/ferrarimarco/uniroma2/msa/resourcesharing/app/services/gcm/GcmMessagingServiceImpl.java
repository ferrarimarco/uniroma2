package info.ferrarimarco.uniroma2.msa.resourcesharing.app.services.gcm;

import android.content.Context;
import android.location.Address;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;

import info.ferrarimarco.uniroma2.msa.resourcesharing.app.R;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.Resource;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.services.config.SharedPreferencesServiceImpl;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.services.persistence.UserService;

public class GcmMessagingServiceImpl{

    public enum GcmMessage{

        NEW_RESOURCE_FROM_ME("info.ferrarimarco.uniroma2.msa.resourcesharing.app.gcm.message.CREATE_NEW_RESOURCE"),
        DELETE_MY_RESOURCE("info.ferrarimarco.uniroma2.msa.resourcesharing.app.gcm.message.DELETE_RESOURCE"),
        UPDATE_USER_DETAILS("info.ferrarimarco.uniroma2.msa.resourcesharing.app.gcm.message.UPDATE_USER_DETAILS"),
        NEW_RESOURCE_FROM_OTHERS("info.ferrarimarco.uniroma2.msa.resourcesharing.app.gcm.message.NEW_RESOURCE_BY_OTHERS"),
        BOOK_RESOURCE("info.ferrarimarco.uniroma2.msa.resourcesharing.app.gcm.message.BOOK_RESOURCE");

        private String stringValue;

        private GcmMessage(String stringValue){
            this.stringValue = stringValue;
        }

        public String getStringValue(){
            return stringValue;
        }
    }

    public enum GcmMessageField{
        DATA_ACTION("action"),
        DATA_TITLE("title"),
        DATA_DESCRIPTION("description"),
        DATA_LOCATION("location"),
        DATA_CREATION_TIME("creation_time"),
        DATA_ACQUISITION_MODE("acquisition_mode"),
        DATA_CREATOR_ID("creator_id"),
        DATA_TTL("ttl"),
        DATA_NEEDS_ACK("needs_ack"),
        DATA_ADDRESS("address"),
        DATA_LOCALITY("locality"),
        DATA_COUNTRY("country"),
        DATA_LATITUDE("latitude"),
        DATA_LONGITUDE("longitude"),
        DATA_BOOKER_ID("booker_id");

        private String stringValue;

        private GcmMessageField(String stringValue){
            this.stringValue = stringValue;
        }

        public String getStringValue(){
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

    private Long defaultGcmTtl;
    private Long maxGcmTtl;
    private static final String GCM_SUFFIX = "@gcm.googleapis.com";
    protected AtomicInteger messageId;
    private String gcmRegistrationId;
    private String gcmProjectRecipient;

    @Inject
    public GcmMessagingServiceImpl(){
        defaultGcmTtl = Long.parseLong(String.valueOf(context.getResources().getString(R.string.gcm_ttl)));
        maxGcmTtl = Long.parseLong(String.valueOf(context.getResources().getString(R.string.gcm_max_ttl)));
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
    public void registerWithGcm(){
        new AsyncTask<String, Void, Void>(){
            @Override
            protected Void doInBackground(String... params){
                if(params.length != 1){
                    throw new IllegalArgumentException("No user specified while registering with backend");
                }

                String userId = params[0];
                Log.d(GcmMessagingServiceImpl.class.getName(), "Send GCM registration Id to backend for: " + userId);

                try{
                    gcm = GoogleCloudMessaging.getInstance(context);
                    gcmRegistrationId = gcm.register(context.getResources().getString(R.string.gcm_project_id));

                    Log.d(GcmMessagingServiceImpl.class.getName(), "GCM Registration ID: " + gcmRegistrationId);

                    // Persist the regID - no need to register again.
                    sharedPreferencesService.storeGcmRegistrationId(gcmRegistrationId);
                }catch(IOException ex){
                    Log.e(GcmMessagingServiceImpl.class.getName(), "Error :" + ex.getMessage());
                    // TODO: handle this error
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }

                return null;
            }
        }.execute(userService.readRegisteredUserId());
    }

    public boolean isGcmRegistrationCompleted(){
        return sharedPreferencesService.isGcmRegistrationCompleted();
    }

    public void sendNewResource(Resource resource){
        Bundle data = new Bundle();
        data.putString(GcmMessageField.DATA_ACTION.getStringValue(), GcmMessage.NEW_RESOURCE_FROM_ME.getStringValue());
        data.putString(GcmMessageField.DATA_TITLE.getStringValue(), resource.getTitle());
        data.putString(GcmMessageField.DATA_DESCRIPTION.getStringValue(), resource.getDescription());
        data.putParcelable(GcmMessageField.DATA_LOCATION.getStringValue(), resource.getLocation());
        data.putLong(GcmMessageField.DATA_CREATION_TIME.getStringValue(), resource.getCreationTime().getMillis());
        data.putString(GcmMessageField.DATA_ACQUISITION_MODE.getStringValue(), resource.getAcquisitionMode());
        data.putString(GcmMessageField.DATA_CREATOR_ID.getStringValue(), userService.readRegisteredUserId());
        data.putLong(GcmMessageField.DATA_TTL.getStringValue(), resource.getTimeToLive());
        data.putBoolean(GcmMessageField.DATA_NEEDS_ACK.getStringValue(), true);

        sendGcmMessage(data, resource.getTimeToLive());
    }

    public void bookResourceFromOthers(Resource resource){
        Bundle data = new Bundle();
        data.putString(GcmMessageField.DATA_ACTION.getStringValue(), GcmMessage.BOOK_RESOURCE.getStringValue());
        data.putLong(GcmMessageField.DATA_CREATION_TIME.getStringValue(), resource.getCreationTime().getMillis());
        data.putString(GcmMessageField.DATA_CREATOR_ID.getStringValue(), resource.getCreatorId());
        data.putString(GcmMessageField.DATA_BOOKER_ID.getStringValue(), userService.readRegisteredUserId());
        data.putBoolean(GcmMessageField.DATA_NEEDS_ACK.getStringValue(), true);

        sendGcmMessage(data, maxGcmTtl);
    }

    public void updateUserDetails(Address address){
        Bundle data = new Bundle();

        data.putString(GcmMessageField.DATA_ACTION.getStringValue(), GcmMessage.UPDATE_USER_DETAILS.getStringValue());
        data.putString(GcmMessageField.DATA_CREATOR_ID.getStringValue(), userService.readRegisteredUserId());
        if(address.getMaxAddressLineIndex() > 0){
            data.putString(GcmMessageField.DATA_ADDRESS.getStringValue(), address.getAddressLine(0));
        }
        data.putString(GcmMessageField.DATA_LOCALITY.getStringValue(), address.getLocality());
        data.putString(GcmMessageField.DATA_COUNTRY.getStringValue(), address.getCountryName());
        data.putString(GcmMessageField.DATA_LATITUDE.getStringValue(), Double.toString(address.getLatitude()));
        data.putString(GcmMessageField.DATA_LONGITUDE.getStringValue(), Double.toString(address.getLongitude()));

        sendGcmMessage(data, defaultGcmTtl);
    }

    private void sendGcmMessage(Bundle data, final Long timeToLive){
        if(!this.isGcmRegistrationCompleted()){
            // TODO: handle this error condition
            return;
        }

        new AsyncTask<Bundle, Void, Integer>(){
            @Override
            protected Integer doInBackground(Bundle... params){
                if(params.length != 1){
                    throw new IllegalArgumentException("No (or wrong) bundle specified while sending GCM message to backend");
                }

                Bundle data = params[0];

                if(!data.containsKey(GcmMessageField.DATA_NEEDS_ACK.getStringValue())){
                    data.putBoolean(GcmMessageField.DATA_NEEDS_ACK.getStringValue(), false);
                }

                String id = Integer.toString(messageId.incrementAndGet());

                try{
                    gcm = GoogleCloudMessaging.getInstance(context);
                    Long ttl = timeToLive;
                    if(ttl == null){
                        ttl = defaultGcmTtl;
                    }

                    gcm.send(gcmProjectRecipient, id, ttl, data);
                }catch(IOException ex){
                    Log.e(GcmMessagingServiceImpl.class.getName(), "Error :" + ex.getMessage());
                    return -1;
                }

                return Integer.parseInt(id);
            }
        }.execute(data);
    }
}
