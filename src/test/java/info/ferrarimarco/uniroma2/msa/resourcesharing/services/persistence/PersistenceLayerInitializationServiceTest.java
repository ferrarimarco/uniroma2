package info.ferrarimarco.uniroma2.msa.resourcesharing.services.persistence;


import info.ferrarimarco.uniroma2.msa.resourcesharing.model.ResourceSharingMobileNodeData;
import info.ferrarimarco.uniroma2.msa.resourcesharing.model.ResourceSharingResource;
import info.ferrarimarco.uniroma2.msa.resourcesharing.model.ResourceSharingUser;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@ContextConfiguration("classpath:spring-context.xml")
public class PersistenceLayerInitializationServiceTest extends AbstractTestNGSpringContextTests{

	private static Logger logger = LoggerFactory.getLogger(PersistenceLayerInitializationServiceTest.class);
	
	@Autowired
    private ApplicationContext applicationContext;
	
	@Autowired
	private PersistenceLayerInitializationService persistenceLayerInitializationService;
	
	@Autowired
	private UserPersistenceService userPersistenceService;
	
	@Autowired
	private ResourcePersistenceService resourcePersistenceService;
	
	@Autowired
	private MobileNodeDataPersistenceService mobileNodeDataPersistenceService;
	
    @BeforeClass
    protected void setup() throws Exception {
    	Assert.assertNotNull(applicationContext);
    	Assert.assertNotNull(persistenceLayerInitializationService);
    	Assert.assertNotNull(userPersistenceService);
    	Assert.assertNotNull(resourcePersistenceService);
    	Assert.assertNotNull(mobileNodeDataPersistenceService);
    }

	@AfterMethod(alwaysRun = true)
	protected void cleanupMethod() {
		persistenceLayerInitializationService.cleanupUserRepository();
		persistenceLayerInitializationService.cleanupResourceRepository();
		persistenceLayerInitializationService.cleanupMobileNodeDataRepository();
	}
    
    @Test(groups = "springServicesTestGroup", dependsOnGroups = "userPersistenceServiceTestGroup")
    public void userPersistenceRepositoryInitializationTest() {
    	
    	int userCount = 10;
    	
    	persistenceLayerInitializationService.initializeUserRepository(userCount);
    	
    	List<ResourceSharingUser> users = userPersistenceService.findAll();
    	
    	Assert.assertNotNull(users);
    	Assert.assertEquals(userCount, users.size());
    	
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
    	
    	Assert.assertNotNull(resources);
    	Assert.assertEquals(resourceCount, resources.size());
    	
		logger.info("Stored resources list:");
		
		for(ResourceSharingResource r : resources) {
			logger.info(r.toString());
		}
		
		resourcePersistenceService.close();
    }
    
    @Test(groups = "springServicesTestGroup", dependsOnGroups = {"userPersistenceServiceTestGroup", "resourcePersistenceServiceTestGroup", "mobileNodeDataPersistenceServiceTestGroup"})
    public void mobileNodeDataRepositoryInitializationTest() {
    	
    	int resourceCount = 10;
    	
    	persistenceLayerInitializationService.initializeMobileNodeDataRepository(resourceCount);
    	
    	List<ResourceSharingMobileNodeData> resources = mobileNodeDataPersistenceService.findAll();
    	
    	mobileNodeDataPersistenceService.close();
    	
    	Assert.assertNotNull(resources);
    	Assert.assertEquals(resourceCount, resources.size());
    	
		logger.info("Stored Mobile nodes data list:");
		
		for(ResourceSharingMobileNodeData r : resources) {
			logger.info(r.toString());
		}
    }
}
