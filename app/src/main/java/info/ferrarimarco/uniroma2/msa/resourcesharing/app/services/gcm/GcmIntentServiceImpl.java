package info.ferrarimarco.uniroma2.msa.resourcesharing.app.services.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.squareup.otto.Bus;

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
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();

        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        if (extras != null && !extras.isEmpty()) {
            GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
            String messageType = gcm.getMessageType(intent);
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                //sendNotification("Send error: " + extras.toString(), null);
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                //sendNotification("Deleted messages on server: " + extras.toString(), null);
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                if (GcmMessagingServiceImpl.GcmMessage.USER_ID_CHECK.equals(intent.getExtras().getString("action"))) {
                    bus.post(new AckAvailableEvent(TaskResultType.valueOf(extras.getString("result"))));
                }
            }

            // Release the wake lock provided by the WakefulBroadcastReceiver.
            GcmBroadcastReceiver.completeWakefulIntent(intent);
        }
    }
}
