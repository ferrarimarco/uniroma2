package info.ferrarimarco.uniroma2.msa.resourcesharing.services.persistence;

import info.ferrarimarco.uniroma2.msa.resourcesharing.model.ResourceSharingMobileNodeData;

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
public class MobileNodeDataPersistenceServiceTest extends AbstractTestNGSpringContextTests {

private static Logger logger = LoggerFactory.getLogger(ResourcePersistenceServiceTest.class);
	
	@Autowired
    private ApplicationContext applicationContext;
	
	@Autowired
	private MobileNodeDataPersistenceService mobileNodeDataPersistenceService;
	
    @BeforeClass
    protected void setup() throws Exception {
    	Assert.assertNotNull(applicationContext);
    	Assert.assertNotNull(mobileNodeDataPersistenceService);
    	
    	logger.info("Cleaning Repositories...");
    	mobileNodeDataPersistenceService.dropCollection();
    	logger.info("Repositories cleaned");
    }
    
    @AfterClass
    protected void teardown() throws Exception {
    	logger.info("Cleaning repositories...");
    	mobileNodeDataPersistenceService.dropCollection();
    	logger.info("Repositories cleaned");
    }
    
    @Test(groups = {"springServicesTestGroup", "mobileNodeDataPersistenceServiceTestGroup"})
	public void createNewMobileNodeDataTest() {
    	
    	ResourceSharingMobileNodeData mobileNodeData = new ResourceSharingMobileNodeData("ID", "Location", new DateTime());
    	
    	mobileNodeData = mobileNodeDataPersistenceService.storeMobileNodeData(mobileNodeData);
    	
		ResourceSharingMobileNodeData mobileNodeDataFromService = mobileNodeDataPersistenceService.findById(mobileNodeData.getMobileNodeId());
		
		mobileNodeDataPersistenceService.close();
		
		Assert.assertNotNull(mobileNodeDataFromService);
		Assert.assertTrue(mobileNodeData.equals(mobileNodeDataFromService));
		
		logger.info("Stored resource: {}", mobileNodeData.toString());
	}
}
