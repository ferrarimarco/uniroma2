package info.ferrarimarco.uniroma2.sii.heartmonitor.services.encryption;

import java.security.InvalidKeyException;


public interface RawEncryptionService {
	void setKey(byte[] keyBytes);
	byte[] encrypt(byte[] input) throws InvalidKeyException;
	byte[] decrypt(byte[] input) throws InvalidKeyException;
}
