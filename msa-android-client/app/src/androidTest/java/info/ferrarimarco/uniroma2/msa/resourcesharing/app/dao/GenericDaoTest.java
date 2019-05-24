package info.ferrarimarco.uniroma2.msa.resourcesharing.app.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.sql.SQLException;
import java.util.List;

import javax.inject.Inject;

import info.ferrarimarco.uniroma2.msa.resourcesharing.app.BaseTezt;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.dao.helper.DatabaseHelperManager;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.Resource;

import static org.junit.Assert.assertEquals;


@Config(emulateSdk = 18)
@RunWith(RobolectricTestRunner.class)
public class GenericDaoTest extends BaseTezt {

    @Inject
    DatabaseHelperManager databaseHelperManager;

    @Inject
    GenericDao<Resource> resourceDao;

    public GenericDaoTest() {
        super();
    }

    @Override
    @Test
    public void dependencyInjectionTest() {
        this.singleDependencyCheck(databaseHelperManager);
        this.singleDependencyCheck(resourceDao);
    }

    @Test
    public void resourceDaoTest() throws SQLException {
        resourceDao.open(Resource.class);

        Resource resource = new Resource();
        resource.setCreatorId("testCreatorId");
        resource.setDescription("testDescription");

        int result = resourceDao.save(resource);
        assertEquals(result, 1);

        List<Resource> retrievedEntities = resourceDao.read(resource);
        assertEquals(retrievedEntities.size(), 1);

        // To write less assertions
        resource.setAndroidId(retrievedEntities.get(0).getAndroidId());
        assertEquals(resource, retrievedEntities.get(0));

        result = resourceDao.delete(resource);
        assertEquals(result, 1);

        resourceDao.close();
    }
}
