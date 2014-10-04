package info.ferrarimarco.uniroma2.msa.resourcesharing.io.gcm.connection;


import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.provider.PacketExtensionProvider;
import org.xmlpull.v1.XmlPullParser;

public class GcmPacketExtensionProvider implements PacketExtensionProvider{
	@Override
	public PacketExtension parseExtension(XmlPullParser parser) throws Exception {
		String json = parser.nextText();
		GcmPacketExtension packet = new GcmPacketExtension(json);
		return packet;
	}
}
