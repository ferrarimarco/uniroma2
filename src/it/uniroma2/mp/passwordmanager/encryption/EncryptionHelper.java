package it.uniroma2.mp.passwordmanager.encryption;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/***
 * Classe che si occupa di criptare e decriptare le password
 * **/

public class EncryptionHelper {
	
	/***
	 * Cripta il testo specificato utilizzando il seme e l'agoritmo specificato
	 * @param seed seme per la generazione della chiave
	 * @param cleartext testo in chiaro
	 * @param algorithm tipo algoritmo per criptare
	 * @return testo criptato
	 * **/
	public String encrypt(String seed, String cleartext, EncryptionAlgorithm algorithm) throws Exception {
        byte[] rawKey = getRawKey(seed.getBytes(), algorithm);
        byte[] result = encrypt(rawKey, cleartext.getBytes(), algorithm);
        return EncodingHelper.toHex(result);
    }

	/***
	 * Decripta il testo specificato utilizzando il seme e l'agoritmo specificato
	 * @param seed seme per la generazione della chiave
	 * @param encrypted testo criptato
	 * @param algorithm tipo algoritmo per criptare
	 * @return testo in chiaro
	 * **/
    public String decrypt(String seed, String encrypted, EncryptionAlgorithm algorithm) throws Exception {
        byte[] rawKey = getRawKey(seed.getBytes(), algorithm);
        byte[] enc = EncodingHelper.toByte(encrypted);
        byte[] result = decrypt(rawKey, enc, algorithm);
        return new String(result);
    }

    private byte[] getRawKey(byte[] seed, EncryptionAlgorithm algorithm) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance(algorithm.toString());
        SecureRandom sr = SecureRandom.getInstance( "SHA1PRNG", "Crypto" );
        sr.setSeed(seed);
        kgen.init(128, sr); // 192 and 256 bits may not be available
        SecretKey skey = kgen.generateKey();
        byte[] raw = skey.getEncoded();
        return raw;
    }


    private byte[] encrypt(byte[] raw, byte[] clear, EncryptionAlgorithm algorithm) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, algorithm.toString());
        Cipher cipher = Cipher.getInstance(algorithm.toString());
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] encrypted = cipher.doFinal(clear);
        return encrypted;
    }

    private byte[] decrypt(byte[] raw, byte[] encrypted, EncryptionAlgorithm algorithm) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, algorithm.toString());
        Cipher cipher = Cipher.getInstance(algorithm.toString());
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        byte[] decrypted = cipher.doFinal(encrypted);
        return decrypted;
    }
	
}
