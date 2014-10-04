package info.ferrarimarco.uniroma2.msa.resourcesharing.io.gcm;
import java.io.IOException;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class GcmApplicationListenerBean implements ApplicationListener<ContextRefreshedEvent> {
	
	@Autowired
	private GcmMessageSender gcmMessageSender;
	
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
		
		try {
			gcmMessageSender.connect();
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			gcmMessageSender.login();
		} catch (XMPPException | SmackException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}