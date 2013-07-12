package it.uniroma2.mp.passwordmanager.encryption;

public interface EncryptionHelper {
	
	String encrypt(String seed, String cleartext) throws Exception;
	String decrypt(String seed, String encrypted) throws Exception;
}
