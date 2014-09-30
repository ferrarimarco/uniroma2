package info.ferrarimarco.uniroma2.msa.resourcesharing.io.gcm;

import lombok.extern.slf4j.Slf4j;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.XMPPConnection;

@Slf4j
public class GcmConnectionListener implements ConnectionListener{
	@Override
	public void reconnectionSuccessful() {
		log.info("Reconnecting..");
	}

	@Override
	public void reconnectionFailed(Exception e) {
		log.info("Reconnection failed.. ", e);
	}

	@Override
	public void reconnectingIn(int seconds) {
		log.info("Reconnecting in {} secs", seconds);
	}

	@Override
	public void connectionClosedOnError(Exception e) {
		log.info("Connection closed on error.");
	}

	@Override
	public void connectionClosed() {
		log.info("Connection closed.");
	}

	@Override
	public void authenticated(XMPPConnection connection) {
		log.info("Authenticated.");
	}

	@Override
	public void connected(XMPPConnection connection) {
		log.info("Connected.");
	}
}
