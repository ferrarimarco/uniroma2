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

	private String key = "SECRET_KEY_OF_32SECRET_KEY_OF_32";
	private String iv = "MY_IV_VECTOR_CBC";
	private byte[] keyBytes;
	private byte[] ivBytes;
	
	@PostConstruct
	private void init(){
		keyBytes = datatypeConversionService.explicitCastStringToByteArrayConversion(key);
		ivBytes = datatypeConversionService.explicitCastStringToByteArrayConversion(iv);
		
		encryptionService.setKeyAndIV(keyBytes, ivBytes);
		
		logger.info("Encryption key for Arduino IO: ASCII: {} , HEX: {}", key, datatypeConversionService.bytesToHex(keyBytes, true));
		logger.info("IV for Arduino IO: ASCII: {} , HEX: {}", iv, datatypeConversionService.bytesToHex(ivBytes, true));
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
		
		logger.info("Decryption of ASCII: {} , HEX: {}", input, datatypeConversionService.bytesToHex(stringBytes, true));
		logger.info("Decryption result ASCII: {} , HEX: {}", result, datatypeConversionService.bytesToHex(output, true));
		
		result = result.trim();
		
		return result;
	}
	
	public String decrypt(byte[] input) throws InvalidKeyException{

		byte[] output = encryptionService.decrypt(input);
		String result = datatypeConversionService.explicitCastByteArrayToStringConversion(output);
		
		logger.info("Decryption of ASCII: {} , HEX: {}", input, datatypeConversionService.bytesToHex(input, true));
		logger.info("Decryption result ASCII: {} , HEX: {}", result, datatypeConversionService.bytesToHex(output, true));
		
		return result;
	}
	
	public byte[] decryptToBytes(byte[] input) throws InvalidKeyException{

		byte[] output = encryptionService.decrypt(input);
		String result = datatypeConversionService.explicitCastByteArrayToStringConversion(output);
		
		logger.info("Decryption of ASCII: {} , HEX: {}", input, datatypeConversionService.bytesToHex(input, true));
		logger.info("Decryption result ASCII: {} , HEX: {}", result, datatypeConversionService.bytesToHex(output, true));
		
		return output;
	}
}
