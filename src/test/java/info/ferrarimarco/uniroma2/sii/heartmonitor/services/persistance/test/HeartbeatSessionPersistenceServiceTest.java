package info.ferrarimarco.uniroma2.sii.heartmonitor.services.persistance.test;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import info.ferrarimarco.uniroma2.sii.heartmonitor.model.HeartbeatSession;
import info.ferrarimarco.uniroma2.sii.heartmonitor.model.HeartbeatSessionValue;
import info.ferrarimarco.uniroma2.sii.heartmonitor.services.persistence.HeartbeatSessionPersistenceService;

@Controller
@RequestMapping("/mongo-db")
public class HeartbeatSessionPersistenceServiceTest {
	
	@Autowired
	private HeartbeatSessionPersistenceService persistenceService;
	
	@RequestMapping(value="write_read_test_values", method = RequestMethod.GET)
	@ResponseBody
	public String writeAndReadTestValues() {

		persistenceService.open();
		
		HeartbeatSession session = new HeartbeatSession("TestUser");
		
		session.addValue(10, 29);
		session.addValue(20, 45);
		session.addValue(54, 56);
		
		persistenceService.storeHeartbeatSession(session);
		
		List<HeartbeatSession> sessions = persistenceService.readAllHeartbeatSessions();
		
		StringBuilder response = new StringBuilder();
		
		for(HeartbeatSession s : sessions){
			response.append(s.toString() + ": ");
			for(HeartbeatSessionValue h : s.getValues()){
				response.append(h.toString() + " ");
			}
			
			response.append(System.lineSeparator());
		}
		
		return response.toString();
	}

}
