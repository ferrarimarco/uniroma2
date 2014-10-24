package info.ferrarimarco.uniroma2.msa.resourcesharing.io.gcm.message;

import info.ferrarimarco.uniroma2.msa.resourcesharing.io.gcm.connection.GcmConnectionManager;
import info.ferrarimarco.uniroma2.msa.resourcesharing.model.ResourceSharingResource;
import info.ferrarimarco.uniroma2.msa.resourcesharing.model.ResourceSharingUser;
import info.ferrarimarco.uniroma2.msa.resourcesharing.services.persistence.ResourcePersistenceService;
import info.ferrarimarco.uniroma2.msa.resourcesharing.services.persistence.UserPersistenceService;

import java.util.List;
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
		BOOK_RESOURCE("info.ferrarimarco.uniroma2.msa.resourcesharing.app.gcm.message.BOOK_RESOURCE"),
		RESOURCE_ALREADY_BOOKED("info.ferrarimarco.uniroma2.msa.resourcesharing.app.gcm.message.RESOURCE_ALREADY_BOOKED"),
		BOOKED_RESOURCE_DELETED("info.ferrarimarco.uniroma2.msa.resourcesharing.app.gcm.message.BOOKED_RESOURCE_DELETED");

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

	@Autowired
	private GcmMessageSender gcmMessageSender;

	@Autowired
	private GcmConnectionManager gcmConnectionManager;

	@Autowired
	private UserPersistenceService userPersistenceService;

	@Autowired
	private ResourcePersistenceService resourcePersistenceService;

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
			String creatorIdBookedResource = payload.get(GcmMessageField.DATA_CREATOR_ID.getStringValue());
			Long creationTimeBookedResource = -1L;
			try {
				creationTimeBookedResource = Long.parseLong(payload.get(GcmMessageField.DATA_CREATION_TIME.getStringValue()));
			} catch (Exception e) {
				log.error("Unable to parse creation time {}, from {} ({})", payload.get(GcmMessageField.DATA_CREATION_TIME.getStringValue()), creatorIdBookedResource, senderGcmId);
				return;
			}
			Long timeToLive;
			try {
				timeToLive = Long.parseLong(payload.get(GcmMessageField.DATA_TTL.getStringValue()));
			} catch (Exception e1) {
				log.error("Unable to parse TTL {}, from {} ({})", payload.get(GcmMessageField.DATA_TTL.getStringValue()), creatorIdBookedResource, senderGcmId);
				return;
			}

			ResourceSharingResource resourceToBook = resourcePersistenceService.readResourceById(Long.toString(creationTimeBookedResource), creatorIdBookedResource);

			if(resourceToBook.getBookerId() == null || resourceToBook.getBookerId().length() == 0) {
				resourceToBook.setBookerId(bookerId);
				resourcePersistenceService.storeResource(resourceToBook);

				ResourceSharingUser resourceCreator = userPersistenceService.readUsersByUserId(creatorIdBookedResource);

				// Resource booked message to resource creator
				gcmMessageSender.sendJsonMessage(resourceCreator.getGcmId(), payload, null, timeToLive, true);
			}else {
				// Resource already booked message to resource booker
				payload.put(GcmMessageField.MESSAGE_ACTION.getStringValue(), GcmMessageAction.RESOURCE_ALREADY_BOOKED.getStringValue());
				gcmMessageSender.sendJsonMessage(senderGcmId, payload, null, timeToLive, true);
			}
			break;
		case DELETE_MY_RESOURCE:
			String creatorIdDeleteResource = payload.get(GcmMessageField.DATA_CREATOR_ID.getStringValue());
			Long creationTimeDeleteResource = -1L;
			try {
				creationTimeDeleteResource = Long.parseLong(payload.get(GcmMessageField.DATA_CREATION_TIME.getStringValue()));
			} catch (Exception e) {
				log.error("Unable to parse creation time {}, from {} ({})", payload.get(GcmMessageField.DATA_CREATION_TIME.getStringValue()), creatorIdDeleteResource, senderGcmId);
				return;
			}

			ResourceSharingResource resourceToDelete = resourcePersistenceService.readResourceById(Long.toString(creationTimeDeleteResource), creatorIdDeleteResource);
			if(resourceToDelete.getBookerId() != null && resourceToDelete.getBookerId().length() > 0) {
				// Booked resource deleted message to resource booker
				payload.put(GcmMessageField.MESSAGE_ACTION.getStringValue(), GcmMessageAction.BOOKED_RESOURCE_DELETED.getStringValue());
				gcmMessageSender.sendJsonMessage(senderGcmId, payload, null, null, true);
			}

			resourcePersistenceService.deleteResource(resourceToDelete.getId());
			break;
		case NEW_RESOURCE_FROM_ME:
			// NEW RESOURCE CREATED from user
			String creatorIdNewResource = payload.get(GcmMessageField.DATA_CREATOR_ID.getStringValue());
			String resourceTitle = payload.get(GcmMessageField.DATA_TITLE.getStringValue());
			String resourceDescription = payload.get(GcmMessageField.DATA_DESCRIPTION.getStringValue());
			Double resourceLatitude;
			try {
				resourceLatitude = Double.parseDouble(payload.get(GcmMessageField.DATA_LATITUDE.getStringValue()));
			} catch (Exception e) {
				log.error("Unable to parse latitude {}, from {} ({})", payload.get(GcmMessageField.DATA_LATITUDE.getStringValue()), creatorIdNewResource, senderGcmId);
				return;
			}
			Double resourceLongitude;
			try {
				resourceLongitude = Double.parseDouble(payload.get(GcmMessageField.DATA_LONGITUDE.getStringValue()));
			} catch (NumberFormatException e) {
				log.error("Unable to parse longitude {}, from {} ({})", payload.get(GcmMessageField.DATA_LONGITUDE.getStringValue()), creatorIdNewResource, senderGcmId);
				return;
			}

			String resourceLocality = payload.get(GcmMessageField.DATA_LOCALITY.getStringValue());
			String resourceCountry = payload.get(GcmMessageField.DATA_COUNTRY.getStringValue());
			String resourceCreationTime = payload.get(GcmMessageField.DATA_CREATION_TIME.getStringValue());
			String resourceAcqisitionMode = payload.get(GcmMessageField.DATA_ACQUISITION_MODE.getStringValue());
			String resourceCreatorId = payload.get(GcmMessageField.DATA_CREATOR_ID.getStringValue());
			Long resourceTtl;
			try {
				resourceTtl = Long.parseLong(payload.get(GcmMessageField.DATA_TTL.getStringValue()));
			} catch (NumberFormatException e) {
				log.error("Unable to parse ttl {}, from {} ({})", payload.get(GcmMessageField.DATA_TTL.getStringValue()), creatorIdNewResource, senderGcmId);
				return;
			}
			
			Long creationTimeMs;
			try {
				creationTimeMs = Long.parseLong(resourceCreationTime);
			} catch (NumberFormatException e) {
				log.error("Unable to parse creation time {}, from {} ({})", payload.get(GcmMessageField.DATA_CREATION_TIME.getStringValue()), creatorIdNewResource, senderGcmId);
				return;
			}

			DateTime expirationDateTime = new DateTime(resourceTtl + creationTimeMs);
			
			if (expirationDateTime.isAfterNow()) {
				DateTime creationTime = new DateTime(creationTimeMs);
				ResourceSharingResource newResource = new ResourceSharingResource(null, resourceTitle, resourceDescription, resourceLatitude, resourceLongitude, resourceLocality, resourceCountry, creationTime, resourceAcqisitionMode, resourceCreatorId, Boolean.FALSE, null, resourceTtl);
				resourcePersistenceService.storeResource(newResource);
				ResourceSharingUser creator = userPersistenceService.readUsersByUserId(creatorIdNewResource);
				if (creator == null) {
					// Update user info
					creator = new ResourceSharingUser(creatorIdNewResource, senderGcmId, new DateTime(), null, resourceLocality, resourceCountry, resourceLatitude, resourceLongitude, null);
				}
				// Search for users in resource range
				List<ResourceSharingUser> usersInRange = userPersistenceService.findUsersInRange(resourceLatitude, resourceLongitude);
				payload.put(GcmMessageField.MESSAGE_ACTION.getStringValue(), GcmMessageAction.NEW_RESOURCE_FROM_OTHERS.getStringValue());
				for (ResourceSharingUser user : usersInRange) {
					gcmMessageSender.sendJsonMessage(user.getGcmId(), payload, null, null, true);
				}
			}else {
				log.warn("Resource {} is expired. Skipped.", resourceTitle);
			}
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
			Double latitude;
			try {
				latitude = Double.parseDouble(payload.get(GcmMessageField.DATA_LATITUDE.getStringValue()));
			} catch (Exception e) {
				log.error("Unable to parse latitude.");
				return;
			}

			Double longitude;
			try {
				longitude = Double.parseDouble(payload.get(GcmMessageField.DATA_LONGITUDE.getStringValue()));
			} catch (Exception e) {
				log.error("Unable to parse longitude.");
				return;
			}
			
			Integer maxDistance = null;
			try {
				maxDistance = Integer.parseInt(payload.get(GcmMessageField.DATA_LONGITUDE.getStringValue()));
			} catch (Exception e) {
				log.error("Unable to parse maxDistance.");
			}
			
			ResourceSharingUser user = userPersistenceService.readUsersByUserId(senderUserId);
			if(user != null) {
				user.setLastUpdate(new DateTime());
				user.setAddress(address);
				user.setLocality(locality);
				user.setCountry(country);
				user.setLatitude(latitude);
				user.setLongitude(longitude);
				if(maxDistance != null) {
					user.setMaxDistance(maxDistance);
				}
			}else {
				user = new ResourceSharingUser(senderUserId, senderGcmId, new DateTime(), address, locality, country, latitude, longitude, maxDistance);
			}
			
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
