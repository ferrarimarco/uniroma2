package info.ferrarimarco.uniroma2.msa.resourcesharing.app.services.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.squareup.otto.Bus;

import javax.inject.Inject;

import info.ferrarimarco.uniroma2.msa.resourcesharing.app.gcm.GcmBroadcastReceiver;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.event.ack.UserIdCheckAckAvailableEvent;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.event.ack.UserSavedAckAvailableEvent;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.task.TaskResultType;

public class GcmIntentServiceImpl extends IntentService {

    @Inject
    Bus bus;

    public GcmIntentServiceImpl() {
        super("GcmIntentService");
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
                String receivedAction = intent.getExtras().getString("action");
                if (GcmMessagingServiceImpl.GcmMessage.USER_ID_CHECK.getStringValue().equals(receivedAction)) {
                    bus.post(new UserIdCheckAckAvailableEvent(TaskResultType.valueOf(extras.getString("result"))));
                } else if (GcmMessagingServiceImpl.GcmMessage.REGISTRATION.getStringValue().equals(receivedAction)) {
                    bus.post(new UserSavedAckAvailableEvent(TaskResultType.valueOf(extras.getString("result"))));
                } else if (GcmMessagingServiceImpl.GcmMessage.NEW_RESOURCE_FROM_ME.getStringValue().equals(intent.getExtras().getString("action"))) {
                    //bus.post(new ResourceSavedAckAvailableEvent(TaskResultType.valueOf(extras.getString("result"),)));
                    // TODO: get the resource (perhaps by local id?)
                }
            }

            // Release the wake lock provided by the WakefulBroadcastReceiver.
            GcmBroadcastReceiver.completeWakefulIntent(intent);
        }
    }
}
