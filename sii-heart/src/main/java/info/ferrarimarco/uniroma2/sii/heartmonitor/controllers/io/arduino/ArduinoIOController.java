package info.ferrarimarco.uniroma2.sii.heartmonitor.controllers.io.arduino;

import java.security.InvalidKeyException;

import javax.annotation.PostConstruct;

import info.ferrarimarco.uniroma2.sii.heartmonitor.exceptions.InvalidSequenceNumberException;
import info.ferrarimarco.uniroma2.sii.heartmonitor.exceptions.SessionNotAvailableException;
import info.ferrarimarco.uniroma2.sii.heartmonitor.model.ArduinoResponseCode;
import info.ferrarimarco.uniroma2.sii.heartmonitor.model.CurrentHeartbeatSession;
import info.ferrarimarco.uniroma2.sii.heartmonitor.model.HeartbeatSession;
import info.ferrarimarco.uniroma2.sii.heartmonitor.model.HeartbeatSessionValue;
import info.ferrarimarco.uniroma2.sii.heartmonitor.model.User;
import info.ferrarimarco.uniroma2.sii.heartmonitor.services.DatatypeConversionService;
import info.ferrarimarco.uniroma2.sii.heartmonitor.services.SessionManagerService;
import info.ferrarimarco.uniroma2.sii.heartmonitor.services.encryption.ArduinoIOEncryptionService;
import info.ferrarimarco.uniroma2.sii.heartmonitor.services.encryption.HeartbeatSessionEncryptionService;
import info.ferrarimarco.uniroma2.sii.heartmonitor.services.persistence.CurrentHeartbeatSessionPerisistenceService;
import info.ferrarimarco.uniroma2.sii.heartmonitor.services.persistence.HeartbeatSessionPersistenceService;
import info.ferrarimarco.uniroma2.sii.heartmonitor.services.persistence.HeartbeatSessionValuePersistenceService;
import info.ferrarimarco.uniroma2.sii.heartmonitor.services.persistence.UserPersistenceService;

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
	private HeartbeatSessionPersistenceService heartbeatSessionPersistenceService;
	
	@Autowired
	private HeartbeatSessionValuePersistenceService heartbeatSessionValuePersistenceService;

	@Autowired
	private CurrentHeartbeatSessionPerisistenceService currentHeartbeatSessionPerisistenceService;

	@Autowired
	private HeartbeatSessionEncryptionService encryptionService;

	@Autowired
	private ArduinoIOEncryptionService arduinoIOEncryptionService;

	@Autowired
	private DatatypeConversionService datatypeConversionService;

	@Autowired
	private UserPersistenceService userPersistenceService;

	@Autowired
	private SessionManagerService sessionManagerService;
	
	@PostConstruct
	public void init() {
		currentHeartbeatSessionPerisistenceService.deleteAll();
		heartbeatSessionValuePersistenceService.deleteAll();
	}

	@ResponseBody
	@RequestMapping(value="/current_user", method = RequestMethod.GET)
	public String getCurrentUser() {

		logger.info("Received GET username request");

		CurrentHeartbeatSession currentHeartbeatSession = currentHeartbeatSessionPerisistenceService.readCurrentHeartbeatSession();
		
		String response = null;
		
		if(currentHeartbeatSession != null) {
			User currentUser = userPersistenceService.readUser(currentHeartbeatSession.getUserName());
			response = currentUser.getUserName();
			
			logger.info("Found a session waiting for values. User: {}", response);
			
		}else { // no session has been initialized
			ArduinoResponseCode arduinoResponseCode = ArduinoResponseCode.USER_NOT_AVAILABLE;
			logger.info("Cannot find a session waiting for values. Response: {} ({})", arduinoResponseCode.ordinal(), arduinoResponseCode.toString());
			response = Integer.toString(arduinoResponseCode.ordinal());
		}

		logger.info("GET current_user response: {}", response);

		return response;
	}

	@ResponseBody
	@RequestMapping(value="/session_id/user/{userId}", method = RequestMethod.GET)
	public String getUniqueSessionId(@PathVariable String userId) {

		logger.info("Received GET uniqueSessionId request");

		CurrentHeartbeatSession currentHeartbeatSession = currentHeartbeatSessionPerisistenceService.readCurrentHeartbeatSession();

		String response = null;
		
		if(currentHeartbeatSession != null && !currentHeartbeatSession.isRequested()) {
			response = encryptionService.encryptHeartbeatSessionId(currentHeartbeatSession.getCurrentSessionId());
			
			currentHeartbeatSession.setRequested(true);
			
			logger.info("Found a session waiting for values. User: {}, Session:{}", currentHeartbeatSession.getUserName(), response);
			
		}else { // no session has been initialized
			ArduinoResponseCode arduinoResponseCode = ArduinoResponseCode.SESSION_NOT_INITIALIZED;
			logger.info("Cannot find a session waiting for values. Response: {} ({})", arduinoResponseCode.ordinal(), arduinoResponseCode.toString());
			response = Integer.toString(arduinoResponseCode.ordinal());
		}
		
		logger.info("GET uniqueSessionId response: {}", response);

		return response;
	}

	@ResponseBody
	@RequestMapping(value="/session/store/{input}", method = RequestMethod.PUT)
	public String storeHeartbeatValue(@PathVariable String input) throws SessionNotAvailableException, InvalidSequenceNumberException{

		if(input.contains(SLASH_CHAR_PLACEHOLDER)){
			input = input.replace(SLASH_CHAR_PLACEHOLDER, "/");
		}

		byte[] encodedInputBytes = datatypeConversionService.explicitCastStringToByteArrayConversion(input);

		logger.info("Received storeHeartbeatValue PUT request with input: ASCII: {} , HEX: {}", input, datatypeConversionService.bytesToHex(encodedInputBytes, true));

		byte[] decodedInputBytes = Base64.decodeBase64(encodedInputBytes);

		logger.info("Decoded storeHeartbeatValue PUT request with input: HEX: {}", datatypeConversionService.bytesToHex(decodedInputBytes, true));

		byte[] decryptedInput = null;

		try {
			decryptedInput = arduinoIOEncryptionService.decryptToBytes(decodedInputBytes);
		} catch (InvalidKeyException e) {
			logger.info("Error {} ({}) while decrypting input {}: {}", e.getMessage(), e.getClass().toString(), input, ExceptionUtils.getStackTrace(e));
		}

		logger.info("Input decrypted as: {}", datatypeConversionService.bytesToHex(decryptedInput, true));

		byte[] sessionIdBytes = new byte[24];
		byte[] bpmBytes = new byte[2];
		byte[] ibiBytes = new byte[2];
		byte[] seqNumberBytes = new byte[2];

		int currentPosition = 0;

		for(int i = 0; i < sessionIdBytes.length; i++) {
			sessionIdBytes[i] = decryptedInput[i];
		}

		currentPosition = sessionIdBytes.length;

		for(int i = 0; i < bpmBytes.length; i++) {
			bpmBytes[i] = decryptedInput[i + currentPosition];
		}

		currentPosition += bpmBytes.length;

		for(int i = 0; i < ibiBytes.length; i++) {
			ibiBytes[i] = decryptedInput[i + currentPosition];
		}

		currentPosition += ibiBytes.length;

		for(int i = 0; i < seqNumberBytes.length; i++) {
			seqNumberBytes[i] = decryptedInput[i + currentPosition];
		}

		String sessionId = datatypeConversionService.explicitCastByteArrayToStringConversion(sessionIdBytes);
		int bpm = datatypeConversionService.bytesToInt(bpmBytes[0], bpmBytes[1]);
		int ibi = datatypeConversionService.bytesToInt(ibiBytes[0], ibiBytes[1]);
		int receivedSequenceNumber = datatypeConversionService.bytesToInt(seqNumberBytes[0], seqNumberBytes[1]);

		logger.info("Session ID: " + sessionId);
		logger.info("BPM: {}, IBI: {}", bpm, ibi);
		logger.info("Seq number: {}", receivedSequenceNumber);

		HeartbeatSession session = heartbeatSessionPersistenceService.readHeartbeatSession(sessionId);

		int response = ArduinoResponseCode.OK.ordinal();

		if(session == null) {
			logger.error("Session " + sessionId + " has not been initialized");

			response = ArduinoResponseCode.SESSION_NOT_INITIALIZED.ordinal();
		} else {
			if (session.isClosed()) {
				logger.error("Session " + sessionId + " is closed");
				response = ArduinoResponseCode.SESSION_CLOSED.ordinal();
			} else {
				if (receivedSequenceNumber != session.getExpectedSequenceNumber()) {
					logger.error("Expected seq_number for Session " + sessionId + " is {}, received {}", session.getExpectedSequenceNumber(), receivedSequenceNumber);
					if (receivedSequenceNumber >= session.getExpectedSequenceNumber()) { // we lost some packets
						session.incrementSequenceNumber(receivedSequenceNumber);
						
						logger.info("Updated expected seq_number to {}", receivedSequenceNumber);
					} else { // someone is trying a replay attack!
						response = ArduinoResponseCode.INVALID_SEQ_NUMBER.ordinal();
					}
				}
				
				if(response == ArduinoResponseCode.OK.ordinal()){ // everything went good so far
					HeartbeatSessionValue latestValue = session.addValue(bpm, ibi);
					logger.info("Storing {} in session {}", latestValue.toString(), sessionId);
					heartbeatSessionPersistenceService.storeHeartbeatSession(session);
					heartbeatSessionValuePersistenceService.storeHeartbeatSessionValue(latestValue);
				}
			}
		}

		return Integer.toString(response);
	}
}
