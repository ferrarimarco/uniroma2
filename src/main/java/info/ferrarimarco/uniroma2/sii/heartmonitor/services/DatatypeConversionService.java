package info.ferrarimarco.uniroma2.sii.heartmonitor.services;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class DatatypeConversionService {

	private Logger logger = LoggerFactory.getLogger(DatatypeConversionService.class);

	private final char[] hexArray = "0123456789ABCDEF".toCharArray();

	public String bytesToHex(byte[] bytes, boolean isSpacedHexValues) {

		char[] hexChars = new char[bytes.length * 2];

		int v;

		for ( int j = 0; j < bytes.length; j++ ) {
			v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}

		String result = new String(hexChars);

		if(isSpacedHexValues) {

			List<String> elements = new ArrayList<String>(result.length());

			StringBuffer spacedResult = new StringBuffer(result.length() * 2);

			for(int i = 0; i < result.length(); i += 2) {
				
				int start = i;
				int end = start + 2;
				
				if(end >= result.length()) {
					end = result.length();
				}
				
				String element = result.substring(start, end);

				elements.add(element);
			}

			for(String element : elements) {
				spacedResult.append(element + " ");
			}

			result = spacedResult.toString().trim();
		}

		return result;
	}

	public byte[] explicitCastStringToByteArrayConversion(String input) {
		byte[] bytes = new byte[input.length()];

		for(int i = 0; i < input.length(); i++) {
			bytes[i] = (byte) input.charAt(i);
		}

		logger.debug("Explicit cast to byte[]. Input: {}, result: {}", input, bytesToHex(bytes, true));

		return bytes;
	}

	public String explicitCastByteArrayToStringConversion(byte[] bytes) {

		StringBuffer result = new StringBuffer(bytes.length);

		for(int i = 0; i < bytes.length; i++) {
			result.append((char) bytes[i]);
		}

		logger.debug("Explicit cast to String. Input: {}, result: {}", bytesToHex(bytes, true), result.toString());

		return result.toString();
	}

	public int bytesToInt(byte high, byte low) {
	    ByteBuffer bb = ByteBuffer.wrap(new byte[] {high, low});
	    return bb.getShort(); // Implicitly widened to an int per JVM spec.
	}
	
}
