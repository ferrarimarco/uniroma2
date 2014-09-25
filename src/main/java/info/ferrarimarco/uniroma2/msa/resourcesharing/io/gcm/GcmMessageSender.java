package info.ferrarimarco.uniroma2.msa.resourcesharing.io.gcm;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Packet;
import org.json.simple.JSONValue;
import org.springframework.beans.factory.annotation.Autowired;


public class GcmMessageSender {
	
	static Random random = new Random();
	
	@Autowired
	private Connection connection;
	
	/**
	 * Returns a random message id to uniquely identify a message.
	 * 
	 * <p>
	 * Note: This is generated by a pseudo random number generator for illustration purpose, and is not guaranteed to be unique.
	 * 
	 */
	public String getRandomMessageId() {
		return "m-" + Long.toString(random.nextLong());
	}

	/**
	 * Sends a downstream GCM message.
	 */
	public void send(String jsonRequest) {
		Packet request = new GcmPacketExtension(jsonRequest).toPacket();
		connection.sendPacket(request);
	}

	private String createJsonMessage(String to, String messageId, Map<String, String> payload, String collapseKey, Long timeToLive, Boolean delayWhileIdle) {
		Map<String, Object> message = new HashMap<String, Object>();
		message.put("to", to);
		if (collapseKey != null) {
			message.put("collapse_key", collapseKey);
		}
		if (timeToLive != null) {
			message.put("time_to_live", timeToLive);
		}
		if (delayWhileIdle != null && delayWhileIdle) {
			message.put("delay_while_idle", true);
		}
		message.put("message_id", messageId);
		message.put("data", payload);
		return JSONValue.toJSONString(message);
	}
	
	/**
	 * Sends a JSON encoded GCM message.
	 * 
	 * @param to
	 *            RegistrationId of the target device (Required).
	 * @param messageId
	 *            Unique messageId for which CCS will send an "ack/nack" (Required).
	 * @param payload
	 *            Message content intended for the application. (Optional).
	 * @param collapseKey
	 *            GCM collapse_key parameter (Optional).
	 * @param timeToLive
	 *            GCM time_to_live parameter (Optional).
	 * @param delayWhileIdle
	 *            GCM delay_while_idle parameter (Optional).
	 */
	public void sendJsonMessage(String to, Map<String, String> payload, String collapseKey, Long timeToLive, Boolean delayWhileIdle) {
		String message = createJsonMessage(to, getRandomMessageId(), payload, collapseKey, timeToLive, delayWhileIdle);
		send(message);
	}


	private String createJsonAck(String to, String messageId) {
		Map<String, Object> message = new HashMap<String, Object>();
		message.put("message_type", "ack");
		message.put("to", to);
		message.put("message_id", messageId);
		return JSONValue.toJSONString(message);
	}
	
	/**
	 * Sends a JSON encoded ACK message for an upstream message received from an application.
	 * 
	 * @param to
	 *            RegistrationId of the device who sent the upstream message.
	 * @param messageId
	 *            messageId of the upstream message to be acknowledged to CCS.
	 */
	public void sendJsonAck(String to, String messageId) {
		String ack = createJsonAck(to, messageId);
		send(ack);
	}
	
	/**
	 * Send GCM message to all registered devices
	 * 
	 * @param payload
	 *            message data
	 */
	public void broadcastMessage(Map<String, String> payload) {

	}
}
