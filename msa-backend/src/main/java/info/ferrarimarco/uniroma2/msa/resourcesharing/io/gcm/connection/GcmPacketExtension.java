package info.ferrarimarco.uniroma2.msa.resourcesharing.io.gcm.connection;

import org.jivesoftware.smack.packet.DefaultPacketExtension;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smack.util.XmlStringBuilder;

/**
 * XMPP Packet Extension for GCM Cloud Connection Server.
 */
public class GcmPacketExtension extends DefaultPacketExtension {

    public static final String GCM_ELEMENT_NAME = "gcm";
    public static final String GCM_NAMESPACE = "google:mobile:data";

    private String json;

    public GcmPacketExtension(String json) {
        super(GCM_ELEMENT_NAME, GCM_NAMESPACE);
        this.json = json;
    }

    public String getJson() {
        return json;
    }

    @Override
    public String toXML() {
        return String.format("<%s xmlns=\"%s\">%s</%s>", GCM_ELEMENT_NAME, GCM_NAMESPACE, json, GCM_ELEMENT_NAME);
    }

    public Packet toPacket() {
        return new Message() {

            // Must override toXML() because it includes a <body>
            @Override
            public XmlStringBuilder toXML() {

                XmlStringBuilder buf = new XmlStringBuilder();
                buf.append("<message");
                if (getXmlns() != null) {
                    buf.append(" xmlns=\"").append(getXmlns()).append("\"");
                }
                if (getLanguage() != null) {
                    buf.append(" xml:lang=\"").append(getLanguage()).append("\"");
                }
                if (getPacketID() != null) {
                    buf.append(" id=\"").append(getPacketID()).append("\"");
                }
                if (getTo() != null) {
                    buf.append(" to=\"").append(StringUtils.escapeForXML(getTo())).append("\"");
                }
                if (getFrom() != null) {
                    buf.append(" from=\"").append(StringUtils.escapeForXML(getFrom())).append("\"");
                }
                buf.append(">");
                buf.append(GcmPacketExtension.this.toXML());
                buf.append("</message>");
                return buf;
            }
        };
    }
}