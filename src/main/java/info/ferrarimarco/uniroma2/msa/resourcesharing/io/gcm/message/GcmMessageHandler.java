package info.ferrarimarco.uniroma2.msa.resourcesharing.io.gcm.message;

import info.ferrarimarco.uniroma2.msa.resourcesharing.io.gcm.connection.GcmConnectionManager;
import info.ferrarimarco.uniroma2.msa.resourcesharing.services.persistence.ResourcePersistenceService;
import info.ferrarimarco.uniroma2.msa.resourcesharing.services.persistence.UserPersistenceService;
import info.ferrarimarco.uniroma2.msa.resourcesharing.services.hashing.HashingService;

import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GcmMessageHandler {

    private enum GcmMessageAction{
        NEW_RESOURCE_FROM_ME("info.ferrarimarco.uniroma2.msa.resourcesharing.app.gcm.message.CREATE_NEW_RESOURCE"),
        DELETE_MY_RESOURCE("info.ferrarimarco.uniroma2.msa.resourcesharing.app.gcm.message.DELETE_RESOURCE"),
        UPDATE_USER_DETAILS("info.ferrarimarco.uniroma2.msa.resourcesharing.app.gcm.message.UPDATE_USER_DETAILS"),
        NEW_RESOURCE_FROM_OTHERS("info.ferrarimarco.uniroma2.msa.resourcesharing.app.gcm.message.NEW_RESOURCE_BY_OTHERS"),
        BOOK_RESOURCE("info.ferrarimarco.uniroma2.msa.resourcesharing.app.gcm.message.BOOK_RESOURCE");

        private String stringValue;

        private GcmMessageAction(String stringValue){
            this.stringValue = stringValue;
        }

        public String getStringValue(){
            return stringValue;
        }

        public static GcmMessageAction getGcmMessageAction(String stringValue) {
            GcmMessageAction[] elements = GcmMessageAction.values();

            for(GcmMessageAction action : elements) {
                if(action.getStringValue().equals(stringValue)) {
                    return action;
                }
            }

            return null;
        }
    }

    private enum GcmMessageField{
        MESSAGE_ID("message_id"),
        MESSAGE_FROM("from"),
        MESSAGE_DATA("data"),
        MESSAGE_CATEGORY("category"),
        MESSAGE_ACTION("action"),
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
        DATA_BOOKER_ID("booker_id");

        private String stringValue;

        private GcmMessageField(String stringValue){
            this.stringValue = stringValue;
        }

        public String getStringValue(){
            return stringValue;
        }
    }

    @Autowired
    private GcmMessageSender gcmMessageSender;

    @Autowired
    private GcmConnectionManager gcmConnectionManager;

    @Autowired
    private UserPersistenceService userPersistenceService;

    @Autowired
    private ResourcePersistenceService resourcePersistenceService;

    @Autowired
    private HashingService hashingService;

    /**
     * Handles an upstream data message from a device application.
     */
    public void handleIncomingDataMessage(Map<String, Object> jsonObject) {
        @SuppressWarnings("unchecked")
        Map<String, String> payload = (Map<String, String>) jsonObject.get(GcmMessageField.MESSAGE_DATA.getStringValue());

        String from = jsonObject.get(GcmMessageField.MESSAGE_FROM.getStringValue()).toString();
        String messageId = jsonObject.get(GcmMessageField.MESSAGE_ID.getStringValue()).toString();
        String category = jsonObject.get(GcmMessageField.MESSAGE_CATEGORY.getStringValue()).toString();
        GcmMessageAction action = GcmMessageAction.getGcmMessageAction(payload.get(GcmMessageField.MESSAGE_ACTION.getStringValue()));

        // PackageName of the application that sent this message.
        log.info("Application: {}", category);

        // Send ACK to CCS
        gcmMessageSender.sendJsonAck(from, messageId);

        switch(action) {
        case BOOK_RESOURCE:
            break;
        case DELETE_MY_RESOURCE:
            break;
        case NEW_RESOURCE_FROM_ME:
            // NEW RESOURCE CREATED from user
            break;
        case NEW_RESOURCE_FROM_OTHERS:
            
            break;
        case UPDATE_USER_DETAILS:
            //            String name = payload.get("name").toString();
            //            // TODO: Store username and registrationID in DB
            //
            //            // Send an REGISTER response back
            //            payload.put("message", "Registration successful");
            //            gcmMessageSender.sendJsonMessage(from, payload, null, null, false);
            break;
        default:
            log.info("Action {} not supported. Ignored.", action);
            break;
        }
    }

    /**
     * Handles an ACK.
     * 
     * <p>
     * By default, it only logs a INFO message, but subclasses could override it to properly handle ACKS.
     */
    public void handleAckReceipt(Map<String, Object> jsonObject) {
        String messageId = jsonObject.get(GcmMessageField.MESSAGE_ID.getStringValue()).toString();
        String from = jsonObject.get(GcmMessageField.MESSAGE_FROM.getStringValue()).toString();
        log.info("handleAckReceipt() from: " + from + ", messageId: " + messageId);
    }

    /**
     * Handles a NACK.
     * 
     * <p>
     * By default, it only logs a INFO message, but subclasses could override it to properly handle NACKS.
     */
    public void handleNackReceipt(Map<String, Object> jsonObject) {
        String messageId = jsonObject.get(GcmMessageField.MESSAGE_ID.getStringValue()).toString();
        String from = jsonObject.get(GcmMessageField.MESSAGE_FROM.getStringValue()).toString();
        log.info("handleNackReceipt() from: " + from + ", messageId: " + messageId);
    }
}
