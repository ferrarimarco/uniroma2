package info.ferrarimarco.uniroma2.msa.resourcesharing.services.persistence;

import java.util.List;

import info.ferrarimarco.uniroma2.msa.resourcesharing.model.ResourceSharingUser;
import info.ferrarimarco.uniroma2.msa.resourcesharing.services.DatatypeConversionService;
import info.ferrarimarco.uniroma2.msa.resourcesharing.services.hashing.HashingService;
import info.ferrarimarco.uniroma2.msa.resourcesharing.services.persistence.UserPersistenceService;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@ContextConfiguration("classpath:spring-context.xml")
public class UserPersistenceServiceTest extends AbstractTestNGSpringContextTests{
	
	private static Logger logger = LoggerFactory.getLogger(UserPersistenceServiceTest.class);
	
	@Autowired
    private ApplicationContext applicationContext;
	
	@Autowired
	private HashingService hashingService;
	
	@Autowired
	private UserPersistenceService userPersistenceService;
	
	@Autowired
	private DatatypeConversionService datatypeConversionService;
	
    @BeforeClass
    protected void setup() throws Exception {
    	Assert.assertNotNull(applicationContext);
    	Assert.assertNotNull(hashingService);
    	Assert.assertNotNull(userPersistenceService);
    	Assert.assertNotNull(datatypeConversionService);
    	
    	logger.info("Cleaning User repository...");
    	userPersistenceService.dropCollection();
    	logger.info("User repository cleaned");
    }
    
    @Test(groups = {"userPersistenceServiceTestGroup","springServicesTestGroup"})
	public void registerNewUserTest() {
    	
    	String username = "Marco Ferrari";
    	String password = "password";
    	String email = "ferrari.marco@gmail.com";
    	
    	byte[] hashedPasswordBytes = hashingService.hash(password);
    	String hashedPassword = datatypeConversionService.bytesToHexString(hashedPasswordBytes);
    	
		ResourceSharingUser user = new ResourceSharingUser(username, email, hashedPassword, new DateTime());
		
		logger.info("User to store: {}", user.toString());
		
		user = userPersistenceService.storeUser(user);
		
		logger.info("Stored User: {}", user.toString());
		
		List<ResourceSharingUser> users = userPersistenceService.readUsersByName(username);
		
		userPersistenceService.close();
		
		Assert.assertNotNull(users);
		Assert.assertEquals(users.size(), 1);
		Assert.assertTrue(user.equals(users.get(0)));
		
		logger.info("Stored users list:");
		
		for(ResourceSharingUser u : users) {
			logger.info(u.toString());
		}
	}
    
    @AfterClass
    protected void teardown() throws Exception {
    	logger.info("Cleaning User repository...");
    	userPersistenceService.dropCollection();
    	logger.info("User repository cleaned");
    }
}
