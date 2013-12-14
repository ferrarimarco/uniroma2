package info.ferrarimarco.uniroma2.sii.heartmonitor.services.hashing;

import info.ferrarimarco.uniroma2.sii.heartmonitor.CryptographyConstant;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ShaHashingService implements HashingService {
	
	private Logger logger = LoggerFactory.getLogger(ShaHashingService.class);
	
	private MessageDigest mda;
	
	public ShaHashingService() {
		Security.addProvider(new BouncyCastleProvider());
		
		try {
			mda = MessageDigest.getInstance(CryptographyConstant.SECURE_HASH_ALGORITHM.toString(), CryptographyConstant.CRYPTO_PROVIDER.toString());
		} catch (NoSuchAlgorithmException | NoSuchProviderException e) {
			logger.error("ShaHashingService failed initialisation - {}: {}", e.toString(), ExceptionUtils.getStackTrace(e));
		}
	}
	
	@Override
	public byte[] hash(String input) {
		return mda.digest(input.getBytes());
	}
	
}
