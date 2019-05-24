package info.ferrarimarco.uniroma2.msa.resourcesharing.services.persistence;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import info.ferrarimarco.uniroma2.msa.resourcesharing.BaseSpringTest;
import info.ferrarimarco.uniroma2.msa.resourcesharing.model.ResourceSharingResource;
import info.ferrarimarco.uniroma2.msa.resourcesharing.services.DatatypeConversionService;
import info.ferrarimarco.uniroma2.msa.resourcesharing.services.hashing.HashingService;

import java.util.List;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class ResourcePersistenceServiceTest extends BaseSpringTest {

    private static Logger logger = LoggerFactory.getLogger(ResourcePersistenceServiceTest.class);

    @Autowired
    private ResourcePersistenceService resourcePersistenceService;

    @Autowired
    private HashingService hashingService;

    @Autowired
    private DatatypeConversionService datatypeConversionService;

    @BeforeClass
    protected void setup() throws Exception {
        super.setup();
        assertThat(resourcePersistenceService, notNullValue());
        assertThat(hashingService, notNullValue());
        assertThat(datatypeConversionService, notNullValue());
    }

    @Test(groups = { "springServicesTestGroup", "resourcePersistenceServiceTestGroup" }, dependsOnGroups = { "userPersistenceServiceTestGroup" })
    public void registerNewResourceTest() {
        resourcePersistenceService.open();

        ResourceSharingResource resource = new ResourceSharingResource("Test Resource ID", "Test Resource Title", "Test Resource Description", 0.0, 0.0, "Test Resource locality",
                "Test Resource Country", new DateTime(), "Test Resource Acquisition Mode", "Test Creator ID", false, "Test Booker ID", null);
        resource = resourcePersistenceService.storeResource(resource);
        logger.info("Stored Resource: {}", resource.toString());

        List<ResourceSharingResource> resources = resourcePersistenceService.readResourcesByCreatorId(resource.getCreatorId());
        resourcePersistenceService.close();

        assertThat(resources, notNullValue());
        assertThat(resources.size(), equalTo(1));
        assertThat(resource, equalTo(resources.get(0)));

        logger.info("Stored resources list:");

        for (ResourceSharingResource r : resources) {
            logger.info(r.toString());
        }
    }
}
