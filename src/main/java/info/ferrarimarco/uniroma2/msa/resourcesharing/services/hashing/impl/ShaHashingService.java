package info.ferrarimarco.uniroma2.msa.resourcesharing.services.hashing.impl;

import info.ferrarimarco.uniroma2.msa.resourcesharing.services.hashing.HashingService;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ShaHashingService implements HashingService {
	
	private Logger logger = LoggerFactory.getLogger(ShaHashingService.class);
	
	@Value("${info.ferrarimarco.msa.resourcesharing.security.hashing.algorithm}")
	private String hashingAlgorithm;
	
	@Value("${info.ferrarimarco.msa.resourcesharing.security.provider}")
	private String cryptographicProvider;
	
	private MessageDigest mda;
	
	public ShaHashingService() {
		Security.addProvider(new BouncyCastleProvider());
	}
	
	@PostConstruct
	private void init() {
		try {
			mda = MessageDigest.getInstance(hashingAlgorithm, cryptographicProvider);
		} catch (NoSuchAlgorithmException | NoSuchProviderException e) {
			logger.error("ShaHashingService failed initialisation - {}: {}", e.toString(), ExceptionUtils.getStackTrace(e));
		}
	}
	
	@Override
	public byte[] hash(String input) {
		return mda.digest(input.getBytes());
	}
}
