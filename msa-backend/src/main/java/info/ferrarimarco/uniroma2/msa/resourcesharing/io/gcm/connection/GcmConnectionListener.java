package info.ferrarimarco.uniroma2.msa.resourcesharing.io.gcm.connection;

import lombok.extern.slf4j.Slf4j;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.XMPPConnection;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GcmConnectionListener implements ConnectionListener {
    @Override
    public void reconnectionSuccessful() {
        log.info("Reconnecting..");
    }

    @Override
    public void reconnectionFailed(Exception e) {
        log.error("Reconnection failed: {} ", e.getMessage());
    }

    @Override
    public void reconnectingIn(int seconds) {
        log.info("Reconnecting in {} secs", seconds);
    }

    @Override
    public void connectionClosedOnError(Exception e) {
        log.info("Connection closed on error: {}", e.getMessage());
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
