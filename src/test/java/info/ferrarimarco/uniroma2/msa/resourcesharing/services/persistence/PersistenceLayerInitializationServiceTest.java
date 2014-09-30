package info.ferrarimarco.uniroma2.msa.resourcesharing.services.persistence;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import info.ferrarimarco.uniroma2.msa.resourcesharing.BaseSpringTest;
import info.ferrarimarco.uniroma2.msa.resourcesharing.model.ResourceSharingResource;
import info.ferrarimarco.uniroma2.msa.resourcesharing.model.ResourceSharingUser;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@ContextConfiguration("classpath:spring-context.xml")
public class PersistenceLayerInitializationServiceTest extends BaseSpringTest{

	private static Logger logger = LoggerFactory.getLogger(PersistenceLayerInitializationServiceTest.class);
	
	@Autowired
	private PersistenceLayerInitializationService persistenceLayerInitializationService;
	
	@Autowired
	private UserPersistenceService userPersistenceService;
	
	@Autowired
	private ResourcePersistenceService resourcePersistenceService;
	
    @BeforeClass
    protected void setup() throws Exception {
    	super.setup();
    	assertThat(persistenceLayerInitializationService, notNullValue());
    	assertThat(userPersistenceService, notNullValue());
    	assertThat(resourcePersistenceService, notNullValue());
    }

	@AfterMethod(alwaysRun = true)
	protected void cleanupMethod() {
		persistenceLayerInitializationService.cleanupUserRepository();
		persistenceLayerInitializationService.cleanupResourceRepository();
	}
    
    @Test(groups = "springServicesTestGroup", dependsOnGroups = "userPersistenceServiceTestGroup")
    public void userPersistenceRepositoryInitializationTest() {
    	
    	int userCount = 10;
    	
    	persistenceLayerInitializationService.initializeUserRepository(userCount);
    	
    	List<ResourceSharingUser> users = userPersistenceService.findAll();
    	
    	assertThat(users, notNullValue());
    	assertThat(userCount, equalTo(users.size()));
    	
		logger.info("Stored users list:");
		
		for(ResourceSharingUser u : users) {
			logger.info(u.toString());
		}
		
		userPersistenceService.close();
    }
    
    @Test(groups = "springServicesTestGroup", dependsOnGroups = {"userPersistenceServiceTestGroup", "resourcePersistenceServiceTestGroup"})
    public void resourcePersistenceRepositoryInitializationTest() {
    	
    	int resourceCount = 10;
    	
    	persistenceLayerInitializationService.initializeResourceRepository(resourceCount);
    	
    	List<ResourceSharingResource> resources = resourcePersistenceService.findAll();
    	
    	assertThat(resources, notNullValue());
    	assertThat(resourceCount, equalTo(resources.size()));
    	
		logger.info("Stored resources list:");
		
		for(ResourceSharingResource r : resources) {
			logger.info(r.toString());
		}
		
		resourcePersistenceService.close();
    }
}
