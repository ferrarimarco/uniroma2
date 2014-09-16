package info.ferrarimarco.uniroma2.msa.resourcesharing.app.services.intent;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.joda.time.DateTime;

import dagger.ObjectGraph;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.broadcastreceiver.GcmBroadcastReceiver;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.Resource;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.services.gcm.GcmMessagingServiceImpl.GcmMessage;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.services.gcm.GcmMessagingServiceImpl.GcmMessageField;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.util.ObjectGraphUtils;

public class GcmIntentServiceImpl extends IntentService{

    public GcmIntentServiceImpl(){
        super("GcmIntentService");
    }

    @Override
    public void onCreate(){
        ObjectGraph objectGraph = ObjectGraphUtils.getObjectGraph(this);
        objectGraph.inject(this);
    }

    @Override
    protected void onHandleIntent(Intent intent){
        Bundle extras = intent.getExtras();

        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        if(extras != null && !extras.isEmpty()){
            GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
            String messageType = gcm.getMessageType(intent);
            switch(messageType){
                case GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR:
                    //sendNotification("Send error: " + extras.toString(), null);
                    break;
                case GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE:
                    String action = intent.getStringExtra(GcmMessageField.DATA_ACTION.getStringValue());
                    GcmMessage gcmMessage = GcmMessage.valueOf(action);
                    switch (gcmMessage) {
                        case NEW_RESOURCE_FROM_OTHERS:
                            Long ttl = intent.getLongExtra(GcmMessageField.DATA_TTL.getStringValue(), 0L);

                            if (0L != ttl) {
                                String title = intent.getStringExtra(GcmMessageField.DATA_TITLE.getStringValue());
                                String description = intent.getStringExtra(GcmMessageField.DATA_DESCRIPTION.getStringValue());
                                Location location = intent.getParcelableExtra(GcmMessageField.DATA_LOCATION.getStringValue());
                                Long creationTimeMs = intent.getLongExtra(GcmMessageField.DATA_CREATION_TIME.getStringValue(), DateTime.now().getMillis());
                                String acquisitionMode = intent.getStringExtra(GcmMessageField.DATA_ACQUISITION_MODE.getStringValue());
                                String creatorId = intent.getStringExtra(GcmMessageField.DATA_CREATOR_ID.getStringValue());

                                Resource resource = new Resource(title, description, location, new DateTime(creationTimeMs), acquisitionMode, creatorId, Resource.ResourceType.NEW, Boolean.FALSE, ttl);
                                ResourceIntentService.startActionReceiveResourceFromOthers(this, resource);
                            }

                            break;
                    }
                    break;
            }

            // Release the wake lock provided by the WakefulBroadcastReceiver.
            GcmBroadcastReceiver.completeWakefulIntent(intent);
        }
    }
}
