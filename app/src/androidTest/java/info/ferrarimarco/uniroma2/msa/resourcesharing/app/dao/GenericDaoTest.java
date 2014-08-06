package info.ferrarimarco.uniroma2.msa.resourcesharing.app.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.sql.SQLException;
import java.util.List;

import javax.inject.Inject;

import info.ferrarimarco.uniroma2.msa.resourcesharing.app.BaseDaoTezt;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.dao.helper.DatabaseHelperManager;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.Resource;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.User;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


@Config(emulateSdk = 18)
@RunWith(RobolectricTestRunner.class)
public class GenericDaoTest extends BaseDaoTezt{

    @Inject
    DatabaseHelperManager databaseHelperManager;

    @Inject
    GenericDao<Resource> resourceDao;

    @Inject
    GenericDao<User> userDao;

    public GenericDaoTest(){
        super();
    }

    @Test
    public void daoInjectionTest(){
        assertNotNull(databaseHelperManager);
        assertNotNull(resourceDao);
        assertNotNull(userDao);
    }

    @Test
    public void resourceDaoTest() throws SQLException{
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

    @Test
    public void userDaoTest() throws SQLException{
        userDao.open(User.class);

        User user = new User();
        user.setEmail("testEmail");
        user.setName("testName");

        int result = userDao.save(user);
        assertEquals(result, 1);

        List<User> retrievedEntities = userDao.read(user);
        assertEquals(retrievedEntities.size(), 1);

        // To write less assertions
        user.setId(retrievedEntities.get(0).getId());
        assertEquals(user, retrievedEntities.get(0));

        result = userDao.delete(user);
        assertEquals(result, 1);

        userDao.close();
    }
}
