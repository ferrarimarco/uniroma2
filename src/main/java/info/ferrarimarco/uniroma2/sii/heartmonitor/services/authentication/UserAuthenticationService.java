package info.ferrarimarco.uniroma2.sii.heartmonitor.services.authentication;

import info.ferrarimarco.uniroma2.sii.heartmonitor.model.User;
import info.ferrarimarco.uniroma2.sii.heartmonitor.services.hashing.HashingService;
import info.ferrarimarco.uniroma2.sii.heartmonitor.services.persistence.UserPersistenceService;

import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserAuthenticationService {
	
	private Logger logger = LoggerFactory.getLogger(UserAuthenticationService.class);
	
	@Autowired
	private UserPersistenceService userPersistenceService;
	
	@Autowired
	private HashingService hashingService;
	
	public boolean authenticate(String userName, String password) {
		
		userPersistenceService.open(false);
		
		User user = userPersistenceService.readUser(userName);
		
		userPersistenceService.close();
		
		boolean result = false;
		
		logger.info("Authenticating user {}", userName);
		
		if(user != null) {
			byte[] hashedPassword = hashingService.hash(password);
			char[] hexEncodedHash = Hex.encodeHex(hashedPassword);
			
			String hexEncodedHashedPassword = new String(hexEncodedHash);
			
			result = user.getHashedPassword().equals(hexEncodedHashedPassword);
			
			if(result) {
				logger.info("Wrong password for user {}", userName);
			}else {
				logger.info("Correct password for user {}", userName);
			}
			
		}else {
			logger.info("User {} does not exist", userName);
		}
		
		return result;
	}
}
