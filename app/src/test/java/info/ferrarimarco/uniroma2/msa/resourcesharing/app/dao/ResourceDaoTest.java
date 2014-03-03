package info.ferrarimarco.uniroma2.msa.resourcesharing.app.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import javax.inject.Inject;

import info.ferrarimarco.uniroma2.msa.resourcesharing.app.BaseDaoTezt;

import static org.junit.Assert.assertNotNull;

/**
 * Created by Marco on 03/03/14.
 */
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
}
