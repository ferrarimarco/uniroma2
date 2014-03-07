package info.ferrarimarco.uniroma2.msa.resourcesharing.app.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.sql.SQLException;
import java.util.List;

import javax.inject.Inject;

import info.ferrarimarco.uniroma2.msa.resourcesharing.app.BaseDaoTezt;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.Resource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


@Config(emulateSdk = 18)
@RunWith(RobolectricTestRunner.class)
public class ResourceDaoTest extends BaseDaoTezt {

	@Inject
	ResourceDao resourceDao;

	public ResourceDaoTest() {
		super();
	}

	@Test
	public void daoInjectionTest() {
		assertNotNull(resourceDao);
	}

	@Test
	public void resourceDaoTest() throws SQLException {

		resourceDao.open(context);

		Resource resource = new Resource();
		resource.setCreatorId("testCreatorId");
		resource.setDescription("testDescription");

		int result = resourceDao.saveResource(resource);

		assertEquals(result, 1);

		List<Resource> retrievedResources = resourceDao.readResource(resource);

		assertEquals(retrievedResources.size(), 1);

		// To write less assertions
		resource.setId(retrievedResources.get(0).getId());

		assertEquals(resource, retrievedResources.get(0));

		result = resourceDao.deleteResource(resource);

		assertEquals(result, 1);

		resourceDao.close();
	}
}
