package info.ferrarimarco.uniroma2.msa.resourcesharing.app.services.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.squareup.otto.Bus;
import com.squareup.otto.Produce;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import info.ferrarimarco.uniroma2.msa.resourcesharing.app.callers.AsyncCaller;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.gcm.GcmBroadcastReceiver;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.event.AckAvailableEvent;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.task.TaskResultType;

public class GcmIntentServiceImpl extends IntentService {

    private Map<AsyncCaller, Integer> callerToMessageIdMap;

    @Inject
    Bus bus;

    public GcmIntentServiceImpl() {
        super("GcmIntentService");
        callerToMessageIdMap = new HashMap<>();
        bus.register(this);
    }

    @Produce
    private AckAvailableEvent produceAck(String messageIdToAck, TaskResultType result) {
        return new AckAvailableEvent(messageIdToAck, result);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Bundle extras = intent.getExtras();

        if ("info.ferrarimarco.uniroma2.msa.resourcesharing.ACK_REQUEST".equals(intent.getAction())) {
            // A caller is requesting an ACK
            // TODO: extract params
            bus.post(produceAck());

        } else {
            GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
            // The getMessageType() intent parameter must be the intent you received
            // in your BroadcastReceiver.
            if (extras != null && !extras.isEmpty()) {
                String messageType = gcm.getMessageType(intent);
                if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                    //sendNotification("Send error: " + extras.toString(), null);
                } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                    //sendNotification("Deleted messages on server: " + extras.toString(), null);
                } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                    if (GcmMessagingServiceImpl.GcmMessage.USER_ID_CHECK.equals(intent.getExtras().getString("action"))) {

                    } else {
                        //sendNotification("Received: " + message, extras);
                    }
                }

                // Release the wake lock provided by the WakefulBroadcastReceiver.
                GcmBroadcastReceiver.completeWakefulIntent(intent);
            }
        }
    }
}
