package info.ferrarimarco.uniroma2.msa.resourcesharing.io.gcm.connection;

import info.ferrarimarco.uniroma2.msa.resourcesharing.io.gcm.message.GcmMessageHandler;

import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class GcmPacketListener implements PacketListener {

    @Autowired
    private GcmMessageHandler gcmMessageHandler;

    @Override
    public void processPacket(Packet packet) {
        log.info("Received: " + packet.toXML());
        Message incomingMessage = (Message) packet;
        GcmPacketExtension gcmPacket = (GcmPacketExtension) incomingMessage.getExtension(GcmPacketExtension.GCM_NAMESPACE);
        String json = gcmPacket.getJson();
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> jsonObject = (Map<String, Object>) JSONValue.parseWithException(json);

            // present for "ack"/"nack", null otherwise
            Object messageType = jsonObject.get("message_type");

            if (messageType == null) {
                // Normal upstream data message
                gcmMessageHandler.handleIncomingDataMessage(jsonObject);
            } else if ("ack".equals(messageType.toString())) {
                // Process Ack
                gcmMessageHandler.handleAckReceipt(jsonObject);
            } else if ("nack".equals(messageType.toString())) {
                // Process Nack
                gcmMessageHandler.handleNackReceipt(jsonObject);
            } else {
                log.warn("Unrecognized message type (%s)", messageType.toString());
            }
        } catch (ParseException e) {
            log.error("Error parsing JSON " + json, e);
        } catch (Exception e) {
            log.error("Couldn't send echo.", e);
        }
    }
}
