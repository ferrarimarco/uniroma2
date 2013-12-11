package info.ferrarimarco.uniroma2.sii.heartmonitor.controllers.io.arduino;

import java.security.InvalidKeyException;

import info.ferrarimarco.uniroma2.sii.heartmonitor.model.HeartbeatSession;
import info.ferrarimarco.uniroma2.sii.heartmonitor.services.DatatypeConversionService;
import info.ferrarimarco.uniroma2.sii.heartmonitor.services.encryption.ArduinoIOEncryptionService;
import info.ferrarimarco.uniroma2.sii.heartmonitor.services.encryption.HeartbeatSessionEncryptionService;
import info.ferrarimarco.uniroma2.sii.heartmonitor.services.persistence.HeartbeatSessionPersistenceService;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value="/arduino")
public class ArduinoIOController {
	
	private Logger logger = LoggerFactory.getLogger(ArduinoIOController.class);
	
	private static final String SLASH_CHAR_PLACEHOLDER = "-"; 
	
	@Autowired
	private HeartbeatSessionPersistenceService persistenceService;
	
	@Autowired
	private HeartbeatSessionEncryptionService encryptionService;
	
	@Autowired
	private ArduinoIOEncryptionService arduinoIOEncryptionService;
	
	@Autowired
	private DatatypeConversionService datatypeConversionService;
	
	public ArduinoIOController() {
		super();
	}

	@ResponseBody
	@RequestMapping(value="/session_id/user/{userId}", method = RequestMethod.GET)
	public String getUniqueSessionId(@PathVariable String userId) {
		
		logger.info("Received GET uniqueSessionId request");
		
		HeartbeatSession session = new HeartbeatSession(userId);
		
		persistenceService.open(true);
		persistenceService.storeHeartbeatSession(session);
		persistenceService.close();
		
		String response = encryptionService.encryptHeartbeatSessionId(session.getId());
		
		logger.info("GET uniqueSessionId response: {}", response);
		
		return response;
	}
	
	@ResponseBody
	@RequestMapping(value="/session/store/{input}", method = RequestMethod.PUT)
	public void storeHeartbeatValue(@PathVariable String input){
		
		if(input.contains(SLASH_CHAR_PLACEHOLDER)){
			input = input.replace(SLASH_CHAR_PLACEHOLDER, "/");
		}
		
		byte[] encodedInputBytes = datatypeConversionService.explicitCastStringToByteArrayConversion(input);
		
		logger.info("Received storeHeartbeatValue PUT request with input: ASCII: {} , HEX: {}", input, datatypeConversionService.bytesToHex(encodedInputBytes, true));
		
		byte[] decodedInputBytes = Base64.decodeBase64(encodedInputBytes);
		
		logger.info("Decoded storeHeartbeatValue PUT request with input: HEX: {}", datatypeConversionService.bytesToHex(decodedInputBytes, true));

		String decryptedInput = null;
		
		try {
			decryptedInput = arduinoIOEncryptionService.decrypt(decodedInputBytes);
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.info("Error {} ({}) while decrypting input {}: {}", e.getMessage(), e.getClass().toString(), input, ExceptionUtils.getStackTrace(e));
		}
		
		logger.info("Input decrypted as: {}", decryptedInput);
		
	}
	
	public void endSession(String sessionId){
		
	}
}
