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
	
	private static final String INPUT_SEPARATOR = ";";
	
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
		
		persistenceService.storeHeartbeatSession(session);
		
		String response = encryptionService.encryptHeartbeatSessionId(session.getId());
		
		return response;
	}
	
	public void storeHeartbeatValue(String input){
		
		String[] inputElements = input.split(INPUT_SEPARATOR);
		
		String encryptedSessionId = inputElements[0];
		String encryptedValue = inputElements[1];
		String sequenceNumber = inputElements[2];
		String checksum = inputElements[3];
	}
	
	public void endSession(String sessionId){
		
	}
}
