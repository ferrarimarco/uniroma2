package info.ferrarimarco.uniroma2.msa.resourcesharing.config;

import javax.net.ssl.SSLSocketFactory;

import info.ferrarimarco.uniroma2.msa.resourcesharing.io.gcm.GcmConnectionListener;
import info.ferrarimarco.uniroma2.msa.resourcesharing.io.gcm.GcmPacketExtension;
import info.ferrarimarco.uniroma2.msa.resourcesharing.io.gcm.GcmPacketExtensionProvider;
import info.ferrarimarco.uniroma2.msa.resourcesharing.io.gcm.GcmPacketInterceptor;
import info.ferrarimarco.uniroma2.msa.resourcesharing.io.gcm.GcmPacketListener;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.provider.ProviderManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:MsaResourceSharingCommon.properties")
public class GcmConfig {
	
	@Value("${info.ferrarimarco.msa.resourcesharing.gcm.username}")
	private String username;
	
	@Value("${info.ferrarimarco.msa.resourcesharing.gcm.serverkey}")
	private String password;
	
	@Value("info.ferrarimarco.msa.resourcesharing.gcm.server")
	private String gcmServer;
	
	@Value("info.ferrarimarco.msa.resourcesharing.gcm.port")
	private String gcmServerPort;
	
	@Bean
	public Connection getConnection() throws XMPPException {
		// Add GcmPacketExtension
		ProviderManager.getInstance().addExtensionProvider(GcmPacketExtension.GCM_ELEMENT_NAME, GcmPacketExtension.GCM_NAMESPACE, new GcmPacketExtensionProvider());
		
		ConnectionConfiguration config = new ConnectionConfiguration(gcmServer, Integer.parseInt(gcmServerPort));
		config.setSecurityMode(SecurityMode.enabled);
		config.setReconnectionAllowed(true);
		config.setRosterLoadedAtLogin(false);
		config.setSendPresence(false);
		config.setSocketFactory(SSLSocketFactory.getDefault());

		// NOTE: Set to true to launch a window with information about packets
		// sent and received
		config.setDebuggerEnabled(true);

		// -Dsmack.debugEnabled=true
		XMPPConnection.DEBUG_ENABLED = true;
		XMPPConnection connection = new XMPPConnection(config);
		connection.addConnectionListener(new GcmConnectionListener());
		connection.addPacketListener(new GcmPacketListener(), new PacketTypeFilter(Message.class));
		connection.addPacketInterceptor(new GcmPacketInterceptor(), new PacketTypeFilter(Message.class));
		connection.connect();
		connection.login(username, password);
		
		return connection;
	}
}
