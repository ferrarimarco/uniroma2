package info.ferrarimarco.uniroma2.sii.heartmonitor.controllers.io.arduino;

import java.security.InvalidKeyException;

import info.ferrarimarco.uniroma2.sii.heartmonitor.HeartMonitorConstant;
import info.ferrarimarco.uniroma2.sii.heartmonitor.exceptions.InvalidSequenceNumberException;
import info.ferrarimarco.uniroma2.sii.heartmonitor.exceptions.SessionNotAvailableException;
import info.ferrarimarco.uniroma2.sii.heartmonitor.model.ArduinoResponseCodes;
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

	public ArduinoIOController() {
		super();
	}

	@ResponseBody
	@RequestMapping(value="/current_user", method = RequestMethod.GET)
	public String getCurrentUser() {

		logger.info("Received GET username request");

		CurrentHeartbeatSession currentHeartbeatSession = currentHeartbeatSessionPerisistenceService.readCurrentHeartbeatSession();
		
		if(currentHeartbeatSession == null) {
			String userName = HeartMonitorConstant.GENERIC_USERNAME.getStringValue();

			logger.error("No session found. Creating a session with a generic username ({})", userName);

			HeartbeatSession session = new HeartbeatSession(userName);
			session = heartbeatSessionPersistenceService.storeHeartbeatSession(session);

			currentHeartbeatSession = new CurrentHeartbeatSession(userName, session.getId());
		}
		
		User currentUser = userPersistenceService.readUser(currentHeartbeatSession.getUserName());

		String response = currentUser.getUserName();

		logger.info("GET current_user response: {}", response);

		return response;
	}

	@ResponseBody
	@RequestMapping(value="/session_id/user/{userId}", method = RequestMethod.GET)
	public String getUniqueSessionId(@PathVariable String userId) {

		logger.info("Received GET uniqueSessionId request");

		CurrentHeartbeatSession currentHeartbeatSession = currentHeartbeatSessionPerisistenceService.readCurrentHeartbeatSession();

		String response = encryptionService.encryptHeartbeatSessionId(currentHeartbeatSession.getCurrentSessionId());

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

		int response = ArduinoResponseCodes.OK.ordinal();

		if(session == null) {
			logger.error("The session " + sessionId + "has not been initialized");

			response = ArduinoResponseCodes.SESSION_NOT_INITIALIZED.ordinal();
		}

		if(session != null) {
			if (session.isClosed()) {
				response = ArduinoResponseCodes.SESSION_CLOSED.ordinal();
			}else {
				int expectedSequenceNumber = session.getExpectedSequenceNumber();

				if (receivedSequenceNumber != expectedSequenceNumber) {
					response = ArduinoResponseCodes.INVALID_SEQ_NUMBER.ordinal();
				}

				if(response != ArduinoResponseCodes.INVALID_SEQ_NUMBER.ordinal()){
					HeartbeatSessionValue latestValue = session.addValue(bpm, ibi);
					logger.info("Storing {} in session {}", latestValue.toString(), sessionId);
					heartbeatSessionPersistenceService.storeHeartbeatSession(session);
					
					heartbeatSessionValuePersistenceService.storeHeartbeatSessionValue(latestValue);

					response = ArduinoResponseCodes.OK.ordinal();
				}
			}
		}

		return Integer.toString(response);
	}

	public void endSession(String sessionId){

	}
}
