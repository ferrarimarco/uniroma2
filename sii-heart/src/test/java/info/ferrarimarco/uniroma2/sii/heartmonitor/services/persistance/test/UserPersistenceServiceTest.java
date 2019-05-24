package info.ferrarimarco.uniroma2.sii.heartmonitor.services.persistance.test;

import info.ferrarimarco.uniroma2.sii.heartmonitor.model.User;
import info.ferrarimarco.uniroma2.sii.heartmonitor.services.DatatypeConversionService;
import info.ferrarimarco.uniroma2.sii.heartmonitor.services.hashing.ShaHashingService;
import info.ferrarimarco.uniroma2.sii.heartmonitor.services.persistence.UserPersistenceService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@ContextConfiguration("classpath:spring-context.xml")
public class UserPersistenceServiceTest extends AbstractTestNGSpringContextTests{
	
	@Autowired
    private ApplicationContext applicationContext;
	
	@Autowired
	private ShaHashingService hashingService;
	
	@Autowired
	private DatatypeConversionService datatypeConversionService;
	
	@Autowired
	private UserPersistenceService userPersistenceService;

    @BeforeClass
    protected void setup() throws Exception {
    	Assert.assertNotNull(applicationContext);
    }
    
    @Test
	public void registerNewUserWithUserClassTest() {
    	
    	String username = "marco";
    	String password = "marco";
    	
    	byte[] hashedPasswordBytes = hashingService.hash(password);
    	String hashedPassword = datatypeConversionService.bytesToHexString(hashedPasswordBytes);
    	
		User user = new User(username, hashedPassword);
		
		userPersistenceService.storeUser(user);
		userPersistenceService.close();
	}
}
