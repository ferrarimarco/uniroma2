package info.ferrarimarco.uniroma2.msa.resourcesharing.io.gcm.connection;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.net.ssl.SSLSocketFactory;

import lombok.extern.slf4j.Slf4j;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class GcmConnectionManager {

    @Value("${info.ferrarimarco.msa.resourcesharing.gcm.server}")
    private String gcmServer;

    @Value("${info.ferrarimarco.msa.resourcesharing.gcm.port}")
    private String gcmServerPort;

    @Value("${info.ferrarimarco.msa.resourcesharing.gcm.username}")
    private String username;

    @Value("${info.ferrarimarco.msa.resourcesharing.gcm.serverkey}")
    private String password;

    private static XMPPConnection connection;

    @PostConstruct
    public void initConnection() {
        // Add GcmPacketExtension
        ProviderManager.addExtensionProvider(GcmPacketExtension.GCM_ELEMENT_NAME, GcmPacketExtension.GCM_NAMESPACE, new GcmPacketExtensionProvider());

        // Init connection only one time
        if (connection == null) {
            buildNewXmppConnection();
        }
    }

    public void buildNewXmppConnection() {
        ConnectionConfiguration config = new ConnectionConfiguration(gcmServer, Integer.parseInt(gcmServerPort));
        config.setSecurityMode(SecurityMode.enabled);
        config.setReconnectionAllowed(true);
        config.setRosterLoadedAtLogin(false);
        config.setSendPresence(false);
        config.setSocketFactory(SSLSocketFactory.getDefault());
        config.setDebuggerEnabled(true);

        XMPPConnection connection = new XMPPTCPConnection(config);
        connection.addConnectionListener(new GcmConnectionListener());
        connection.addPacketListener(new GcmPacketListener(), new PacketTypeFilter(Message.class));
        connection.addPacketInterceptor(new GcmPacketInterceptor(), new PacketTypeFilter(Message.class));

        GcmConnectionManager.connection = connection;
        connect();
        login();
    }

    public XMPPConnection getExistingXmppConnection() {
        return connection;
    }

    private void connect() {
        try {
            connection.connect();
        } catch (SmackException | IOException | XMPPException e) {
            throw new IllegalStateException(e);
        }
    }

    public void disconnect() {
        if (connection.isConnected()) {
            try {
                connection.disconnect();
            } catch (NotConnectedException e) {
                log.warn("Trying to disconnect a not connected connection.");
            }
        }
    }

    private void login() {
        try {
            connection.login(username, password);
        } catch (XMPPException | SmackException | IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Sends a downstream GCM message.
     * 
     * @throws XMPPException
     * @throws IOException
     * @throws SmackException
     */
    public void send(String jsonRequest) throws SmackException, IOException, XMPPException {
        Packet request = new GcmPacketExtension(jsonRequest).toPacket();

        if (connection == null) {
            buildNewXmppConnection();
        }

        connection.sendPacket(request);
    }
}
