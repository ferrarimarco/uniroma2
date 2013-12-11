package info.ferrarimarco.uniroma2.sii.heartmonitor.services.encryption;

import java.security.InvalidKeyException;

public interface RawEncryptionService {
	void setKeyAndIV(byte[] keyBytes, byte[] ivBytes);
	byte[] encrypt(byte[] input) throws InvalidKeyException;
	byte[] decrypt(byte[] input) throws InvalidKeyException;
}
