package info.ferrarimarco.uniroma2.msa.resourcesharing.app.services.gcm;

import android.content.Context;
import android.location.Address;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.squareup.otto.Bus;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;

import dagger.ObjectGraph;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.R;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.Resource;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.event.GcmRegistrationCompletedEvent;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.services.config.SharedPreferencesServiceImpl;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.util.ObjectGraphUtils;

public class GcmMessagingServiceImpl{

    public enum GcmMessage{

        NEW_RESOURCE_FROM_ME("info.ferrarimarco.uniroma2.msa.resourcesharing.app.gcm.message.CREATE_NEW_RESOURCE"),
        DELETE_MY_RESOURCE("info.ferrarimarco.uniroma2.msa.resourcesharing.app.gcm.message.DELETE_RESOURCE"),
        UPDATE_USER_DETAILS("info.ferrarimarco.uniroma2.msa.resourcesharing.app.gcm.message.UPDATE_USER_DETAILS"),
        NEW_RESOURCE_FROM_OTHERS("info.ferrarimarco.uniroma2.msa.resourcesharing.app.gcm.message.NEW_RESOURCE_BY_OTHERS"),
        BOOK_RESOURCE("info.ferrarimarco.uniroma2.msa.resourcesharing.app.gcm.message.BOOK_RESOURCE"),
        RESOURCE_ALREADY_BOOKED("info.ferrarimarco.uniroma2.msa.resourcesharing.app.gcm.message.RESOURCE_ALREADY_BOOKED"),
        BOOKED_RESOURCE_DELETED("info.ferrarimarco.uniroma2.msa.resourcesharing.app.gcm.message.BOOKED_RESOURCE_DELETED");

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
        DATA_CREATION_TIME("creation_time"),
        DATA_ACQUISITION_MODE("acquisition_mode"),
        DATA_CREATOR_ID("creator_id"),
        DATA_TTL("ttl"),
        DATA_ADDRESS("address"),
        DATA_LOCALITY("locality"),
        DATA_COUNTRY("country"),
        DATA_LATITUDE("latitude"),
        DATA_LONGITUDE("longitude"),
        DATA_BOOKER_ID("booker_id"),
        DATA_MAX_DISTANCE("max_distance");

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
    Context context;

    @Inject
    Bus bus;

    private Long defaultGcmTtl;
    private Long maxGcmTtl;
    private static final String GCM_SUFFIX = "@gcm.googleapis.com";
    protected AtomicInteger messageId;
    private String gcmRegistrationId;
    private String gcmProjectRecipient;

    @Inject
    public GcmMessagingServiceImpl(){
        ObjectGraph objectGraph = ObjectGraphUtils.getObjectGraph(context);
        objectGraph.inject(this);
        defaultGcmTtl = Long.parseLong(String.valueOf(context.getResources().getString(R.string.gcm_ttl)));
        maxGcmTtl = Long.parseLong(String.valueOf(context.getResources().getString(R.string.gcm_max_ttl)));
        gcmProjectRecipient = context.getResources().getString(R.string.gcm_project_number) + GCM_SUFFIX;
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

                try{
                    gcm = GoogleCloudMessaging.getInstance(context);
                    gcmRegistrationId = gcm.register(context.getResources().getString(R.string.gcm_project_number));

                    Log.d(GcmMessagingServiceImpl.class.getName(), "GCM Registration ID: " + gcmRegistrationId);

                    // Persist the regID - no need to register again.
                    sharedPreferencesService.storeGcmRegistrationId(gcmRegistrationId);
                    bus.post(new GcmRegistrationCompletedEvent());
                }catch(IOException ex){
                    throw new RuntimeException(ex);
                }

                return null;
            }
        }.execute(sharedPreferencesService.readRegisteredUserId());
    }

    public boolean isGcmRegistrationCompleted(){
        return sharedPreferencesService.isGcmRegistrationCompleted();
    }

    public void sendNewResource(Resource resource){
        Bundle data = new Bundle();
        data.putString(GcmMessageField.DATA_ACTION.getStringValue(), GcmMessage.NEW_RESOURCE_FROM_ME.getStringValue());
        data.putString(GcmMessageField.DATA_TITLE.getStringValue(), resource.getTitle());
        data.putString(GcmMessageField.DATA_DESCRIPTION.getStringValue(), resource.getDescription());
        data.putString(GcmMessageField.DATA_LATITUDE.getStringValue(), resource.getLatitude().toString());
        data.putString(GcmMessageField.DATA_LONGITUDE.getStringValue(), resource.getLongitude().toString());
        data.putString(GcmMessageField.DATA_LOCALITY.getStringValue(), resource.getLocality());
        data.putString(GcmMessageField.DATA_COUNTRY.getStringValue(), resource.getCountry());
        data.putString(GcmMessageField.DATA_CREATION_TIME.getStringValue(), ((Long) resource.getCreationTime().getMillis()).toString());
        data.putString(GcmMessageField.DATA_ACQUISITION_MODE.getStringValue(), resource.getAcquisitionMode());
        data.putString(GcmMessageField.DATA_CREATOR_ID.getStringValue(), sharedPreferencesService.readRegisteredUserId());
        data.putString(GcmMessageField.DATA_TTL.getStringValue(), resource.getTimeToLive().toString());

        sendGcmMessage(data, resource.getTimeToLive());
    }

    public void bookResourceFromOthers(Resource resource){
        Bundle data = new Bundle();
        data.putString(GcmMessageField.DATA_ACTION.getStringValue(), GcmMessage.BOOK_RESOURCE.getStringValue());
        data.putLong(GcmMessageField.DATA_CREATION_TIME.getStringValue(), resource.getCreationTime().getMillis());
        data.putString(GcmMessageField.DATA_CREATOR_ID.getStringValue(), resource.getCreatorId());
        data.putString(GcmMessageField.DATA_BOOKER_ID.getStringValue(), resource.getBookerId());

        sendGcmMessage(data, maxGcmTtl);
    }

    public void deleteResourceFromMe(Resource resource){
        Bundle data = new Bundle();
        data.putString(GcmMessageField.DATA_ACTION.getStringValue(), GcmMessage.DELETE_MY_RESOURCE.getStringValue());
        data.putLong(GcmMessageField.DATA_CREATION_TIME.getStringValue(), resource.getCreationTime().getMillis());
        data.putString(GcmMessageField.DATA_CREATOR_ID.getStringValue(), resource.getCreatorId());

        sendGcmMessage(data, maxGcmTtl);
    }

    public void updateUserDetails(Address address){
        Bundle data = new Bundle();

        data.putString(GcmMessageField.DATA_ACTION.getStringValue(), GcmMessage.UPDATE_USER_DETAILS.getStringValue());
        data.putString(GcmMessageField.DATA_CREATOR_ID.getStringValue(), sharedPreferencesService.readRegisteredUserId());
        if(address.getMaxAddressLineIndex() > 0){
            data.putString(GcmMessageField.DATA_ADDRESS.getStringValue(), address.getAddressLine(0));
        }
        data.putString(GcmMessageField.DATA_LOCALITY.getStringValue(), address.getLocality());
        data.putString(GcmMessageField.DATA_COUNTRY.getStringValue(), address.getCountryName());
        data.putString(GcmMessageField.DATA_LATITUDE.getStringValue(), Double.toString(address.getLatitude()));
        data.putString(GcmMessageField.DATA_LONGITUDE.getStringValue(), Double.toString(address.getLongitude()));
        data.putString(GcmMessageField.DATA_MAX_DISTANCE.getStringValue(), Integer.toString(1000));

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
