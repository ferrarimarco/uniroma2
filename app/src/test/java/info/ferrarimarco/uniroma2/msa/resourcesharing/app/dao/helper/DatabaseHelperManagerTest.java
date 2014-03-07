package info.ferrarimarco.uniroma2.msa.resourcesharing.app.dao.helper;

import android.util.Log;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import javax.inject.Inject;

import info.ferrarimarco.uniroma2.msa.resourcesharing.app.BaseDaoTezt;

import static org.junit.Assert.assertNotNull;


@Config(emulateSdk = 18)
@RunWith(RobolectricTestRunner.class)
public class DatabaseHelperManagerTest extends BaseDaoTezt {

	@Inject
	DatabaseHelperManager databaseHelperManager;

	public DatabaseHelperManagerTest() {
		super();
	}

	@Override
	@Test
	public void daoInjectionTest() {
		assertNotNull(databaseHelperManager);
	}

	@Test
	public void getDatabaseHelperTest() {
		DatabaseHelperManager databaseHelperManager = new DatabaseHelperManager();
		DatabaseHelper databaseHelper = databaseHelperManager.getHelper(context);
		assertNotNull(databaseHelper);

		Log.d(DatabaseHelperManagerTest.class.getName(), "Database Path:" + context.getDatabasePath(DatabaseHelper.DATABASE_NAME));
	}

	@Test
	public void releaseDatabaseHelperTest() {
		DatabaseHelperManager databaseHelperManager = new DatabaseHelperManager();
		DatabaseHelper databaseHelper = databaseHelperManager.getHelper(context);
		assertNotNull(databaseHelper);

		databaseHelper.close();

		databaseHelperManager.releaseHelper();
	}
}
