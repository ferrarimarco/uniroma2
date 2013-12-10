package info.ferrarimarco.uniroma2.sii.heartmonitor.services.encryption;

import info.ferrarimarco.uniroma2.sii.heartmonitor.services.DatatypeConversionService;

import java.security.InvalidKeyException;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ArduinoIOEncryptionService {
	
	private Logger logger = LoggerFactory.getLogger(ArduinoIOEncryptionService.class);
	
	@Autowired
	private DatatypeConversionService datatypeConversionService;

	@Autowired
	private RawEncryptionService encryptionService;

	@PostConstruct
	private void init(){
		
		String key = "SECRET_KEY_OF_32SECRET_KEY_OF_32";
		
		byte[] keyBytes = datatypeConversionService.explicitCastStringToByteArrayConversion(key);
		
		encryptionService.setKey(keyBytes);
		
		logger.info("Encryption key for Arduino IO: ASCII: {} , HEX: {}", key, datatypeConversionService.bytesToHex(keyBytes, true));
	}

	public String encrypt(String input) throws InvalidKeyException{

		byte[] stringBytes = datatypeConversionService.explicitCastStringToByteArrayConversion(input);

		byte[] output = encryptionService.encrypt(stringBytes);
		String result = datatypeConversionService.explicitCastByteArrayToStringConversion(output);
		
		
		
		return result;
	}

	public String decrypt(String input) throws InvalidKeyException{

		byte[] stringBytes = datatypeConversionService.explicitCastStringToByteArrayConversion(input);
		
		byte[] output = encryptionService.decrypt(stringBytes);
		String result = datatypeConversionService.explicitCastByteArrayToStringConversion(output);
		
		logger.info("Decryption. ASCII: {} , HEX: {}", result, datatypeConversionService.bytesToHex(output, true));
		
		return result;
	}
}
