package info.ferrarimarco.uniroma2.msa.resourcesharing.app.dao.helper;

import android.util.Log;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import info.ferrarimarco.uniroma2.msa.resourcesharing.app.BaseTest;

import static org.junit.Assert.assertNotNull;

/**
 * Created by Marco on 28/02/14.
 */
@Config(emulateSdk = 18)
@RunWith(RobolectricTestRunner.class)
public class DatabaseHelperManagerTest extends BaseTest {

    @Test
    public void getDatabaseHelperTest(){
        initContext();

        DatabaseHelperManager databaseHelperManager = new DatabaseHelperManager();
        DatabaseHelper databaseHelper = databaseHelperManager.getHelper(context);
        assertNotNull(databaseHelper);

        Log.d(DatabaseHelperManagerTest.class.getName(), "Database Path:" + context.getDatabasePath(DatabaseHelper.DATABASE_NAME));
    }
}
