package info.ferrarimarco.uniroma2.sii.heartmonitor.services.encryption;

import info.ferrarimarco.uniroma2.sii.heartmonitor.CryptographyConstant;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AESEncryptionService implements RawEncryptionService {

	private Logger logger = LoggerFactory.getLogger(AESEncryptionService.class);

	private Boolean initCompleted;
	private Cipher in;
	private Cipher out;

	public AESEncryptionService(){
		Security.addProvider(new BouncyCastleProvider());

		try {
			in = Cipher.getInstance(CryptographyConstant.CIPHER_INSTANCE_TRANSFORMATION.getStringValue(), CryptographyConstant.CRYPTO_PROVIDER.getStringValue());
			out = Cipher.getInstance(CryptographyConstant.CIPHER_INSTANCE_TRANSFORMATION.getStringValue(), CryptographyConstant.CRYPTO_PROVIDER.getStringValue());
		} catch (NoSuchAlgorithmException | NoSuchProviderException | NoSuchPaddingException e) {
			logger.error("AES failed initialisation - {}: {}", e.toString(), ExceptionUtils.getStackTrace(e));
		}
	}

	public void setKeyAndIV(byte[] keyBytes, byte[] ivBytes){
		
		Key key = new SecretKeySpec(keyBytes, CryptographyConstant.CRYPTO_ALGORITHM.getStringValue());
		AlgorithmParameterSpec IVspec = new IvParameterSpec(ivBytes);
		
		try {
			in.init(Cipher.DECRYPT_MODE, key, IVspec);
			out.init(Cipher.ENCRYPT_MODE, key, IVspec);
		} catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
			logger.error("AES failed initialisation - {}: {}", e.toString(), ExceptionUtils.getStackTrace(e));
		}
		
		initCompleted = Boolean.TRUE;
		
	}

	private void checkKey() throws InvalidKeyException{
		if(!initCompleted){
			throw new InvalidKeyException("Key and IV are not initialized");
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

		byte[] bytes = new byte[input.length];;

		try {
			DataInputStream dIn = new DataInputStream(cIn);

			dIn.readFully(bytes, 0, bytes.length);

			dIn.close();
		} catch(EOFException e) {
			logger.error("{} while decrypting {}", e.toString(), e.toString());
		} catch (Exception e) {
			logger.error("AES failed encryption - {}: {}", e.toString(), ExceptionUtils.getStackTrace(e));
		}

		return bytes;
	}
}
