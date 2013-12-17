package info.ferrarimarco.uniroma2.sii.heartmonitor.services.authentication;

import info.ferrarimarco.uniroma2.sii.heartmonitor.model.User;
import info.ferrarimarco.uniroma2.sii.heartmonitor.services.SessionManagerService;
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
	
	@Autowired
	private SessionManagerService sessionManagerService;
	
	
	public User login(String userName, String password, boolean storeUserInSession) {
		User result = authenticate(userName, password);
		
		if(result != null && storeUserInSession) {
			sessionManagerService.saveCurrentAuthenticatedUser(result);
		}
		
		return result;
	}
	
	public void logout() {
		sessionManagerService.removeCurrentAuthenticatedUser();
	}
	
	public User authenticate(String userName, String password) {
		
		User result = null;
		
		if(userName != null && !userName.trim().isEmpty() && password != null && !password.trim().isEmpty()) {
			userPersistenceService.open();
			
			User user = userPersistenceService.readUser(userName);
			
			userPersistenceService.close();
			
			logger.info("Authenticating user {}", userName);
			
			if(user != null) {
				byte[] hashedPassword = hashingService.hash(password);
				char[] hexEncodedHash = Hex.encodeHex(hashedPassword);
				
				String hexEncodedHashedPassword = new String(hexEncodedHash);
				
				boolean isMatchingPassword = user.getHashedPassword().equals(hexEncodedHashedPassword);
				
				if(isMatchingPassword) {
					result = user;
					logger.info("Correct password for user {}", userName);
				}else {
					logger.info("Wrong password for user {}", userName);
				}
				
			}else {
				logger.info("User {} does not exist", userName);
			}		
		}else {
			logger.info("Wrong auth parameters. Username: {}, Password:{}", userName, password);
		}
		
		return result;
	}
}
