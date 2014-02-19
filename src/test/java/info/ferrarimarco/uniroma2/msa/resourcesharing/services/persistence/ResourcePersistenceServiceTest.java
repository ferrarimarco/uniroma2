package info.ferrarimarco.uniroma2.msa.resourcesharing.services.persistence;

import java.util.List;

import info.ferrarimarco.uniroma2.msa.resourcesharing.model.ResourceSharingResource;
import info.ferrarimarco.uniroma2.msa.resourcesharing.model.ResourceSharingUser;
import info.ferrarimarco.uniroma2.msa.resourcesharing.services.DatatypeConversionService;
import info.ferrarimarco.uniroma2.msa.resourcesharing.services.hashing.HashingService;

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
public class ResourcePersistenceServiceTest extends AbstractTestNGSpringContextTests{
	
	private static Logger logger = LoggerFactory.getLogger(ResourcePersistenceServiceTest.class);
	
	@Autowired
    private ApplicationContext applicationContext;
	
	@Autowired
	private ResourcePersistenceService resourcePersistenceService;
	
	@Autowired
	private HashingService hashingService;
	
	@Autowired
	private UserPersistenceService userPersistenceService;
	
	@Autowired
	private DatatypeConversionService datatypeConversionService;
	
    @BeforeClass
    protected void setup() throws Exception {
    	Assert.assertNotNull(applicationContext);
    	Assert.assertNotNull(resourcePersistenceService);
    	Assert.assertNotNull(hashingService);
    	Assert.assertNotNull(userPersistenceService);
    	Assert.assertNotNull(datatypeConversionService);
    	
    	logger.info("Cleaning Repositories...");
    	userPersistenceService.dropCollection();
    	resourcePersistenceService.dropCollection();
    	logger.info("Repositories cleaned");
    }
    
    @AfterClass
    protected void teardown() throws Exception {
    	logger.info("Cleaning repositories...");
    	userPersistenceService.dropCollection();
    	resourcePersistenceService.dropCollection();
    	logger.info("Repositories cleaned");
    }
    
    @Test(groups = {"springServicesTestGroup", "resourcePersistenceServiceTestGroup"}, dependsOnGroups = {"userPersistenceServiceTestGroup"})
	public void registerNewResourceTest() {
    	
    	// Register a new user first
    	String username = "Marco Ferrari";
    	String password = "password";
    	String email = "ferrari.marco@gmail.com";
    	byte[] hashedPasswordBytes = hashingService.hash(password);
    	String hashedPassword = datatypeConversionService.bytesToHexString(hashedPasswordBytes);
		ResourceSharingUser user = userPersistenceService.storeUser(new ResourceSharingUser(username, email, hashedPassword, new DateTime()));
		
		userPersistenceService.close();
		
		ResourceSharingResource resource = new ResourceSharingResource("Test Resource Title", "Test Resource Description", "Test Resource Location", "Test Resource Acquisition Mode", user.getId());
		
		resource = resourcePersistenceService.storeResource(resource);
		
		logger.info("Stored Resource: {}", resource.toString());
		
		List<ResourceSharingResource> resources = resourcePersistenceService.readResourcesByCreatorId(resource.getCreatorId());
		
		resourcePersistenceService.close();
		
		Assert.assertNotNull(resources);
		Assert.assertEquals(resources.size(), 1);
		Assert.assertTrue(resource.equals(resources.get(0)));
		
		logger.info("Stored resources list:");
		
		for(ResourceSharingResource r : resources) {
			logger.info(r.toString());
		}
	}
}
