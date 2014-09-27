package info.ferrarimarco.uniroma2.msa.resourcesharing.app.services.intent;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.joda.time.DateTime;

import java.io.IOException;

import info.ferrarimarco.uniroma2.msa.resourcesharing.app.broadcast.GcmReceiver;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.Resource;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.services.gcm.GcmMessagingServiceImpl.GcmMessage;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.services.gcm.GcmMessagingServiceImpl.GcmMessageField;

public class GcmIntentServiceImpl extends IntentService{

    public GcmIntentServiceImpl(){
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent){
        Bundle extras = intent.getExtras();

        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        if(extras != null && !extras.isEmpty()){
            GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
            String messageType = gcm.getMessageType(intent);
            if(messageType != null){
                switch(messageType){
                    case GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR:
                        try{
                            throw new IOException("Unable to send GCM message");
                        }catch(IOException e){
                            e.printStackTrace();
                            Log.e(GcmIntentServiceImpl.class.getName(), e.getMessage());
                        }
                        break;
                    case GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE:
                        String action = intent.getStringExtra(GcmMessageField.DATA_ACTION.getStringValue());
                        GcmMessage gcmMessage = GcmMessage.valueOf(action);
                        Long time;

                        switch(gcmMessage){
                            case NEW_RESOURCE_FROM_OTHERS:
                                time = intent.getLongExtra(GcmMessageField.DATA_TTL.getStringValue(), 0L);

                                if(time != 0L){
                                    String title = intent.getStringExtra(GcmMessageField.DATA_TITLE.getStringValue());
                                    String description = intent.getStringExtra(GcmMessageField.DATA_DESCRIPTION.getStringValue());
                                    Double latitude = Double.parseDouble(intent.getStringExtra(GcmMessageField.DATA_LATITUDE.getStringValue()));
                                    Double longitude = Double.parseDouble(intent.getStringExtra(GcmMessageField.DATA_LONGITUDE.getStringValue()));
                                    Long creationTimeMs = intent.getLongExtra(GcmMessageField.DATA_CREATION_TIME.getStringValue(), DateTime.now().getMillis());
                                    String acquisitionMode = intent.getStringExtra(GcmMessageField.DATA_ACQUISITION_MODE.getStringValue());
                                    String creatorId = intent.getStringExtra(GcmMessageField.DATA_CREATOR_ID.getStringValue());

                                    Resource resource = new Resource(title, description, latitude, longitude, new DateTime(creationTimeMs), acquisitionMode, creatorId, Resource.ResourceType.NEW, Boolean.FALSE, time, null);
                                    ResourceIntentService.startActionReceiveResourceFromOthers(this, resource);
                                }

                                break;
                            case BOOK_RESOURCE:
                                time = intent.getLongExtra(GcmMessageField.DATA_CREATION_TIME.getStringValue(), 0L);

                                if(time == 0L){
                                    throw new IllegalArgumentException("Unable to read resource information");
                                }

                                String bookerId = intent.getStringExtra(GcmMessageField.DATA_BOOKER_ID.getStringValue());
                                String creatorId = intent.getStringExtra(GcmMessageField.DATA_CREATOR_ID.getStringValue());

                                Resource resource = new Resource(null, null, null, null, new DateTime(time), null, creatorId, Resource.ResourceType.CREATED_BY_ME, Boolean.FALSE, time, bookerId);
                                ResourceIntentService.startActionBookResourceFromMe(this, resource);

                                break;

                        }
                        break;
                }

            }

            // Release the wake lock provided by the WakefulBroadcastReceiver.
            GcmReceiver.completeWakefulIntent(intent);
        }
    }
}
