package info.ferrarimarco.uniroma2.msa.resourcesharing.services.persistence;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import info.ferrarimarco.uniroma2.msa.resourcesharing.BaseSpringTest;
import info.ferrarimarco.uniroma2.msa.resourcesharing.model.ResourceSharingUser;
import info.ferrarimarco.uniroma2.msa.resourcesharing.services.DatatypeConversionService;
import info.ferrarimarco.uniroma2.msa.resourcesharing.services.hashing.HashingService;

import java.util.List;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@ContextConfiguration("classpath:spring-context.xml")
public class UserPersistenceServiceTest extends BaseSpringTest {
	
	private static Logger logger = LoggerFactory.getLogger(UserPersistenceServiceTest.class);
	
	@Autowired
	private HashingService hashingService;
	
	@Autowired
	private UserPersistenceService userPersistenceService;
	
	@Autowired
	private DatatypeConversionService datatypeConversionService;
	
    @BeforeClass
    protected void setup() throws Exception {
    	super.setup();
    	assertThat(hashingService, notNullValue());
    	assertThat(userPersistenceService, notNullValue());
    	assertThat(datatypeConversionService, notNullValue());
    	
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
		
		assertThat(users, notNullValue());
		assertThat(users.size(), equalTo(1));
		assertThat(user, equalTo(users.get(0)));
		
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
