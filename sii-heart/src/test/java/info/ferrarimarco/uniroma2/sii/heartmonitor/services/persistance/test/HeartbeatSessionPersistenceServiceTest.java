package info.ferrarimarco.uniroma2.sii.heartmonitor.services.persistance.test;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import info.ferrarimarco.uniroma2.sii.heartmonitor.model.HeartbeatSession;
import info.ferrarimarco.uniroma2.sii.heartmonitor.services.persistence.HeartbeatSessionPersistenceService;

@ContextConfiguration("classpath:spring-context.xml")
public class HeartbeatSessionPersistenceServiceTest extends AbstractTestNGSpringContextTests {
	
	private static Logger logger = LoggerFactory.getLogger(HeartbeatSessionPersistenceServiceTest.class);
	
	@Autowired
    private ApplicationContext applicationContext;
	
	@Autowired
	private HeartbeatSessionPersistenceService persistenceService;
	
	@BeforeClass
	protected void setup() {
		Assert.assertNotNull(applicationContext);
		
		persistenceService.deleteAll();
		writeAndReadTestValues();
	}
	
	private void writeAndReadTestValues() {

		HeartbeatSession session = new HeartbeatSession("marco");
		
		session.addValue(10, 29);
		session.addValue(20, 45);
		session.addValue(54, 56);
		
		persistenceService.storeHeartbeatSession(session);
	}
	
	@Test
	public void readAllSessionsTest() {
		logger.info("List of all recorded sessions");
		
		List<HeartbeatSession> sessions = persistenceService.readAllHeartbeatSessions();
		
		logger.info("Sessions count: {}", sessions.size());
		
		for(HeartbeatSession session : sessions) {
			logger.info(session.toString());
		}
	}
}
