package info.ferrarimarco.uniroma2.msa.resourcesharing.io.gcm.connection;

import lombok.extern.slf4j.Slf4j;

import org.jivesoftware.smack.PacketInterceptor;
import org.jivesoftware.smack.packet.Packet;

@Slf4j
public class GcmPacketInterceptor implements PacketInterceptor {
	@Override
	public void interceptPacket(Packet packet) {
		log.info("Sent: {0}", packet.toXML());
	}
}
