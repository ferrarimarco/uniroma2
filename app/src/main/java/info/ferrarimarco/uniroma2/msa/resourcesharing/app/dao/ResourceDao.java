package info.ferrarimarco.uniroma2.msa.resourcesharing.app.dao;

import android.content.Context;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

import javax.inject.Inject;

import info.ferrarimarco.uniroma2.msa.resourcesharing.app.dao.helper.DatabaseHelper;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.dao.helper.DatabaseHelperManager;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.exceptions.DaoException;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.Resource;

/**
 * Created by Marco on 01/03/14.
 */
public class ResourceDao {

	private DatabaseHelperManager databaseHelperManager;

	private DatabaseHelper databaseHelper;
	private Dao<Resource, String> resourceDao;

	private boolean initialized;

	@Inject
	public ResourceDao(DatabaseHelperManager databaseHelperManager) {
		this.databaseHelperManager = databaseHelperManager;
		initialized = false;
	}

	private void checkInitialization() {
		if (!initialized) {
			throw new DaoException("This DAO has not been initialized");
		}
	}

	public int saveResource(Resource resource) throws SQLException {

		checkInitialization();

		return resourceDao.create(resource);
	}

	public int updateResource(Resource resource) throws SQLException {
		checkInitialization();
		return resourceDao.update(resource);
	}

	public int deleteResource(Resource resource) throws SQLException {
		checkInitialization();
		return resourceDao.delete(resource);
	}

	public void open(Context context) throws SQLException {
		databaseHelper = databaseHelperManager.getHelper(context);
		resourceDao = databaseHelper.getResourceDao();

		initialized = true;
	}

	public void close() {
		if (initialized) {
			databaseHelper.close();
			databaseHelperManager.releaseHelper();
		}
	}
}
