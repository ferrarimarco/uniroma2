package info.ferrarimarco.uniroma2.sii.heartmonitor.services.authentication;

import info.ferrarimarco.uniroma2.sii.heartmonitor.model.SessionParameter;
import info.ferrarimarco.uniroma2.sii.heartmonitor.model.User;
import info.ferrarimarco.uniroma2.sii.heartmonitor.services.hashing.HashingService;
import info.ferrarimarco.uniroma2.sii.heartmonitor.services.persistence.UserPersistenceService;

import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zkoss.zk.ui.Sessions;

@Service
public class UserAuthenticationService {
	
	private Logger logger = LoggerFactory.getLogger(UserAuthenticationService.class);
	
	@Autowired
	private UserPersistenceService userPersistenceService;
	
	@Autowired
	private HashingService hashingService;
	
	public User getAuthenticatedUser() {
		return (User) Sessions.getCurrent().getAttribute(SessionParameter.CURRENT_USER.toString());
	}
	
	public User login(String userName, String password) {
		User result = authenticate(userName, password);
		
		if(result != null) {
			Sessions.getCurrent().setAttribute(SessionParameter.CURRENT_USER.toString(), result);		
		}
		
		return result;
	}
	
	public void logout() {
		Sessions.getCurrent().removeAttribute(SessionParameter.CURRENT_USER.toString());		
	}
	
	public User authenticate(String userName, String password) {
		
		User result = null;
		
		if(userName != null && !userName.trim().isEmpty() && password != null && !password.trim().isEmpty()) {
			userPersistenceService.open(false);
			
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
