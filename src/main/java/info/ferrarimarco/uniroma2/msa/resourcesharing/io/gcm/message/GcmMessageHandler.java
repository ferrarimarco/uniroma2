package info.ferrarimarco.uniroma2.msa.resourcesharing.io.gcm.message;

import info.ferrarimarco.uniroma2.msa.resourcesharing.io.gcm.connection.GcmConnectionManager;
import info.ferrarimarco.uniroma2.msa.resourcesharing.model.ResourceSharingResource;
import info.ferrarimarco.uniroma2.msa.resourcesharing.model.ResourceSharingUser;
import info.ferrarimarco.uniroma2.msa.resourcesharing.services.DatatypeConversionService;
import info.ferrarimarco.uniroma2.msa.resourcesharing.services.persistence.ResourcePersistenceService;
import info.ferrarimarco.uniroma2.msa.resourcesharing.services.persistence.UserPersistenceService;
import info.ferrarimarco.uniroma2.msa.resourcesharing.services.hashing.HashingService;

import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.joda.time.DateTime;
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
    
    @Autowired
    private DatatypeConversionService datatypeConversionService;
    
    private ResourceSharingResource createResource(String creationTimeMs, String creatorId) {
        byte[] hashedPasswordBytes = hashingService.hash(creationTimeMs + creatorId);
        String hashedId = datatypeConversionService.bytesToHexString(hashedPasswordBytes);
        
        return new ResourceSharingResource(hashedId);
    }
    
    /**
     * Handles an upstream data message from a device application.
     */
    public void handleIncomingDataMessage(Map<String, Object> jsonObject) {
        @SuppressWarnings("unchecked")
        Map<String, String> payload = (Map<String, String>) jsonObject.get(GcmMessageField.MESSAGE_DATA.getStringValue());

        String senderGcmId = jsonObject.get(GcmMessageField.MESSAGE_FROM.getStringValue()).toString();
        String messageId = jsonObject.get(GcmMessageField.MESSAGE_ID.getStringValue()).toString();
        String category = jsonObject.get(GcmMessageField.MESSAGE_CATEGORY.getStringValue()).toString();
        GcmMessageAction action = GcmMessageAction.getGcmMessageAction(payload.get(GcmMessageField.MESSAGE_ACTION.getStringValue()));

        // Send ACK to CCS
        gcmMessageSender.sendJsonAck(senderGcmId, messageId);
        
        log.trace("PackageName of the Application that sent this message: {}", category);
        
        switch(action) {
        case BOOK_RESOURCE:
            String bookerId = payload.get(GcmMessageField.DATA_BOOKER_ID.getStringValue());
            String creatorId = payload.get(GcmMessageField.DATA_CREATOR_ID.getStringValue());
            Long creationTime = -1L;
            try {
                creationTime = Long.parseLong(payload.get(GcmMessageField.DATA_CREATION_TIME.getStringValue()));
            } catch (Exception e) {
                log.error("Unable to parse creation time {}, from {} ({})", payload.get(GcmMessageField.DATA_CREATION_TIME.getStringValue()), creatorId, senderGcmId);
                creationTime = -1L;
            }
            
            if(creationTime != -1L) {
                ResourceSharingResource resourceToBook = resourcePersistenceService.readResourceById(createResource(creationTime.toString(), creatorId).getId());
                resourcePersistenceService.bookResource(resourceToBook);
                
                ResourceSharingUser resourceCreator = userPersistenceService.readUsersByUserId(creatorId);
                
                // Resource booked message to resource creator
                gcmMessageSender.sendJsonMessage(resourceCreator.getGcmId(), payload, null, timeToLive, true);
            }
            break;
        case DELETE_MY_RESOURCE:
            String creatorIdDeleteResource = payload.get(GcmMessageField.DATA_CREATOR_ID.getStringValue());
            Long creationTimeDeleteResource = -1L;
            try {
                creationTimeDeleteResource = Long.parseLong(payload.get(GcmMessageField.DATA_CREATION_TIME.getStringValue()));
            } catch (Exception e) {
                log.error("Unable to parse creation time {}, from {} ({})", payload.get(GcmMessageField.DATA_CREATION_TIME.getStringValue()), creatorIdDeleteResource, senderGcmId);
                creationTimeDeleteResource = -1L;
            }
            
            if(creationTime != -1L) {
                resourcePersistenceService.deleteResource(new ResourceSharingResource(creationTimeDeleteResource, creatorIdDeleteResource));
            }
            break;
        case NEW_RESOURCE_FROM_ME:
            // NEW RESOURCE CREATED from user
            // also update user info
            break;
        case NEW_RESOURCE_FROM_OTHERS:
            // This should not be sent by devices, but only from server to devices
            log.warn("Received {} action from {}. A client is misbehaving. Message ignored.", action.getStringValue(), senderGcmId);
            break;
        case UPDATE_USER_DETAILS:
            String senderUserId = payload.get(GcmMessageField.DATA_CREATOR_ID.getStringValue());
            String address = payload.get(GcmMessageField.DATA_ADDRESS.getStringValue());
            String locality = payload.get(GcmMessageField.DATA_LOCALITY.getStringValue());
            String country = payload.get(GcmMessageField.DATA_COUNTRY.getStringValue());
            Double latitude = 0.0;
            try {
                latitude = Double.parseDouble(payload.get(GcmMessageField.DATA_LATITUDE.getStringValue()));
            } catch (Exception e) {
                latitude = 0.0;
                log.error("Unable to parse latitude. Using {}.", latitude);
            }

            Double longitude = 0.0;
            try {
                longitude = Double.parseDouble(payload.get(GcmMessageField.DATA_LONGITUDE.getStringValue()));
            } catch (Exception e) {
                longitude = 0.0;
                log.error("Unable to parse longitude. Using {}.", longitude);
            }

            ResourceSharingUser user = new ResourceSharingUser(senderUserId, senderGcmId, new DateTime(), address, locality, country, latitude, longitude);
            userPersistenceService.storeUser(user);
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
