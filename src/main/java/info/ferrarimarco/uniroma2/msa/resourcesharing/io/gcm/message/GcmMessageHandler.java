package info.ferrarimarco.uniroma2.msa.resourcesharing.io.gcm.message;

import info.ferrarimarco.uniroma2.msa.resourcesharing.io.gcm.connection.GcmConnectionManager;

import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class GcmMessageHandler {
	
	@Autowired
	private GcmMessageSender gcmMessageSender;
	
	@Autowired
	private GcmConnectionManager gcmConnectionManager;
	
	/**
	 * Handles an upstream data message from a device application.
	 * 
	 * <p>
	 * This sample echo server sends an echo message back to the device. Subclasses should override this method to process an upstream message.
	 */
	public void handleIncomingDataMessage(Map<String, Object> jsonObject) {
		@SuppressWarnings("unchecked")
		Map<String, String> payload = (Map<String, String>) jsonObject.get("data");

		String from = jsonObject.get("from").toString();
		
		// Send ACK to CCS
		String messageId = jsonObject.get("message_id").toString();
		gcmMessageSender.sendJsonAck(from, messageId);
		
		// PackageName of the application that sent this message.
		String category = jsonObject.get("category").toString();
		log.info("Application: " + category);

		String action = payload.get("action");
		if (action.equalsIgnoreCase("com.antoinecampbell.gcmdemo.REGISTER")) {
			String name = payload.get("name").toString();
			// TODO: Store username and registrationID in DB

			// Send an REGISTER response back
			payload.put("message", "Registration successful");
			gcmMessageSender.sendJsonMessage(from, payload, null, null, false);
			log.info("Adding new user: " + name + ":" + from);
		} else if (action.equalsIgnoreCase("com.antoinecampbell.gcmdemo.ECHO")) {
			// Send an ECHO response back
			gcmMessageSender.sendJsonMessage(from, payload, null, null, false);
		}else {
			log.info("Unkown action sent: " + action);
		}
	}
	
	/**
	 * Handles an ACK.
	 * 
	 * <p>
	 * By default, it only logs a INFO message, but subclasses could override it to properly handle ACKS.
	 */
	public void handleAckReceipt(Map<String, Object> jsonObject) {
		String messageId = jsonObject.get("message_id").toString();
		String from = jsonObject.get("from").toString();
		log.info("handleAckReceipt() from: " + from + ", messageId: " + messageId);
	}

	/**
	 * Handles a NACK.
	 * 
	 * <p>
	 * By default, it only logs a INFO message, but subclasses could override it to properly handle NACKS.
	 */
	public void handleNackReceipt(Map<String, Object> jsonObject) {
		String messageId = jsonObject.get("message_id").toString();
		String from = jsonObject.get("from").toString();
		log.info("handleNackReceipt() from: " + from + ", messageId: " + messageId);
	}
}
