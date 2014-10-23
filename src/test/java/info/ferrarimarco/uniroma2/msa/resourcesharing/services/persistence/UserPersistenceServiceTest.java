package info.ferrarimarco.uniroma2.msa.resourcesharing.services.persistence;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import info.ferrarimarco.uniroma2.msa.resourcesharing.BaseSpringTest;
import info.ferrarimarco.uniroma2.msa.resourcesharing.model.ResourceSharingUser;
import info.ferrarimarco.uniroma2.msa.resourcesharing.services.DatatypeConversionService;
import info.ferrarimarco.uniroma2.msa.resourcesharing.services.hashing.HashingService;
import lombok.extern.slf4j.Slf4j;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@ContextConfiguration("classpath:spring-context.xml")
@Slf4j
public class UserPersistenceServiceTest extends BaseSpringTest {
	
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
    }
    
    @Test(groups = {"userPersistenceServiceTestGroup","springServicesTestGroup"})
	public void registerNewUserTest() {
    	userPersistenceService.open();
    	
    	String userId = "test-user-id";
		ResourceSharingUser user = new ResourceSharingUser(userId, "test-user-gcm-id", new DateTime(), "test-user-address", "test-user-locality", "test-user-country", 0.0, 0.0);
		
		user = userPersistenceService.storeUser(user);
		ResourceSharingUser readUser = userPersistenceService.readUsersByUserId(userId);
		
		userPersistenceService.close();
		
		assertThat(readUser, notNullValue());
		assertThat(user, equalTo(readUser));
		
		log.info("User to store: {}", user.toString());
		log.info("Stored user: {}", readUser.toString());
	}
}
