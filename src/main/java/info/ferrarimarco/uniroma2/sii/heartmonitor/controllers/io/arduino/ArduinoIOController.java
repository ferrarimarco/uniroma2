package info.ferrarimarco.uniroma2.sii.heartmonitor.controllers.io.arduino;

import info.ferrarimarco.uniroma2.sii.heartmonitor.model.HeartbeatSession;
import info.ferrarimarco.uniroma2.sii.heartmonitor.services.encryption.HeartbeatSessionEncryptionService;
import info.ferrarimarco.uniroma2.sii.heartmonitor.services.persistence.HeartbeatSessionPersistenceService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value="/arduino")
public class ArduinoIOController {
	
	@Autowired
	private HeartbeatSessionPersistenceService persistenceService;
	
	@Autowired
	private HeartbeatSessionEncryptionService encryptionService;
	
	public ArduinoIOController() {
		super();
	}

	@ResponseBody
	@RequestMapping(value="/session_id/user/{userId}", method = RequestMethod.GET)
	public String getUniqueSessionId(@PathVariable String userId) {
		
		HeartbeatSession session = new HeartbeatSession(userId);
		
		persistenceService.open(true);
		persistenceService.storeHeartbeatSession(session);
		persistenceService.close();
		
		String response = encryptionService.encryptHeartbeatSessionId(session.getShortId());
		
		return response;
	}
	
	@ResponseBody
	@RequestMapping(value="/session/store/{input}", method = RequestMethod.PUT)
	public void storeHeartbeatValue(@PathVariable String input){
		
	}
	
	public void endSession(String sessionId){
		
	}
}
