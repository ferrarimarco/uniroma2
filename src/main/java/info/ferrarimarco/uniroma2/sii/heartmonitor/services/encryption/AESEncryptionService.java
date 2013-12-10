package info.ferrarimarco.uniroma2.sii.heartmonitor.services.encryption;

import info.ferrarimarco.uniroma2.sii.heartmonitor.CryptographyConstant;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AESEncryptionService implements RawEncryptionService {

	private Logger logger = LoggerFactory.getLogger(AESEncryptionService.class);

	private Key key;
	private Cipher in;
	private Cipher out;

	public AESEncryptionService() throws NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException{
		Security.addProvider(new BouncyCastleProvider());

		in = Cipher.getInstance("AES/ECB/ZeroBytePadding", CryptographyConstant.CRYPTO_PROVIDER.getStringValue());
		out = Cipher.getInstance("AES/ECB/ZeroBytePadding", CryptographyConstant.CRYPTO_PROVIDER.getStringValue());
	}

	public void setKey(byte[] keyBytes){
		key = new SecretKeySpec(keyBytes, CryptographyConstant.CRYPTO_ALGORITHM.getStringValue());

		try {
			out.init(Cipher.ENCRYPT_MODE, key);
		} catch (InvalidKeyException e) {
			logger.error("AES failed initialisation - {}: {}", e.toString(), ExceptionUtils.getStackTrace(e));
		}

		try {
			in.init(Cipher.DECRYPT_MODE, key);
		} catch (InvalidKeyException e) {
			logger.error("AES failed initialisation - {}: {}", e.toString(), ExceptionUtils.getStackTrace(e));
		}
	}

	private void checkKey() throws InvalidKeyException{
		if(key == null){
			throw new InvalidKeyException("Key is not initialized");
		}
	}
	
	public byte[] encrypt(byte[] input) throws InvalidKeyException{
		
		checkKey();
		
		ByteArrayOutputStream bOut = new ByteArrayOutputStream();
		CipherOutputStream cOut = new CipherOutputStream(bOut, out);

		try {
			for (int i = 0; i != input.length / 2; i++) {
				cOut.write(input[i]);
			}
			cOut.write(input, input.length / 2, input.length - input.length / 2);
			cOut.close();
		} catch (IOException e) {
			logger.error("AES failed encryption - {}", e.toString());
		}

		return bOut.toByteArray();
	}

	public byte[] decrypt(byte[] input) throws InvalidKeyException{
		
		checkKey();
		
		ByteArrayInputStream bIn = new ByteArrayInputStream(input);
		CipherInputStream cIn = new CipherInputStream(bIn, in);

		byte[] bytes = null;

		try {
			DataInputStream dIn = new DataInputStream(cIn);

			bytes = new byte[input.length];

			for (int i = 0; i != input.length / 2; i++) {
				bytes[i] = (byte) dIn.read();
			}

			dIn.readFully(bytes, input.length / 2, bytes.length - input.length / 2);

			dIn.close();
		} catch (Exception e) {
			logger.error("AES failed encryption - {}: {}", e.toString(), ExceptionUtils.getStackTrace(e));
		}

		return bytes;
	}
}
