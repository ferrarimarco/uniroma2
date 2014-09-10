package info.ferrarimarco.uniroma2.msa.resourcesharing.app.services.intent;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.squareup.otto.Bus;

import javax.inject.Inject;

import dagger.ObjectGraph;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.broadcastreceiver.GcmBroadcastReceiver;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.util.ObjectGraphUtils;

public class GcmIntentServiceImpl extends IntentService{

    @Inject
    Bus bus;

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
                case GoogleCloudMessaging.MESSAGE_TYPE_DELETED:
                    //sendNotification("Deleted messages on server: " + extras.toString(), null);
                    break;
                case GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE:
                    break;
            }

            // Release the wake lock provided by the WakefulBroadcastReceiver.
            GcmBroadcastReceiver.completeWakefulIntent(intent);
        }
    }
}
