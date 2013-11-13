package info.ferrarimarco.uniroma2.sii.heartmonitor.test.dao.mongodb;

import java.util.List;

import org.joda.time.DateTime;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import info.ferrarimarco.uniroma2.sii.heartmonitor.controllers.HeartbeatSessionPersistenceController;
import info.ferrarimarco.uniroma2.sii.heartmonitor.model.HeartbeatSession;

@Controller
@RequestMapping("/mongoDbTest")
public class MongoDbTest {
	
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public String writeAndReadTestValues() {

		HeartbeatSessionPersistenceController controller = new HeartbeatSessionPersistenceController();
		
		controller.open(true);
		
		HeartbeatSession session = new HeartbeatSession(new DateTime());
		
		session.addValue(10);
		session.addValue(20);
		session.addValue(54);
		
		controller.storeHeartbeatSession(session);
		
		List<HeartbeatSession> sessions = controller.readAllHeartbeatSessions();
		
		StringBuilder response = new StringBuilder();
		
		for(HeartbeatSession s : sessions){
			response.append(s.toString() + ": ");
			for(Integer i : s.getValues()){
				response.append(i + " ");
			}
			
			response.append(System.lineSeparator());
		}
		
		return response.toString();
	}

}
