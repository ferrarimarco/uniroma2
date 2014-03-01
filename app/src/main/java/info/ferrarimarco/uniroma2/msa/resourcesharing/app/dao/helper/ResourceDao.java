package info.ferrarimarco.uniroma2.msa.resourcesharing.app.dao.helper;

import android.content.Context;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.Resource;

/**
 * Created by Marco on 01/03/14.
 */
public class ResourceDao {

	// TODO: autowire here
	private DatabaseHelperManager databaseHelperManager;
	private DatabaseHelper databaseHelper;
	private Dao<Resource, String> resourceDao;

	public ResourceDao(Context context) throws SQLException {
		open(context);
	}

	public int saveResource(Resource resource) throws SQLException {
		return resourceDao.create(resource);
	}

	public int updateResource(Resource resource) throws SQLException {
		return resourceDao.update(resource);
	}

	public int deleteResource(Resource resource) throws SQLException {
		return resourceDao.delete(resource);
	}

	public void open(Context context) throws SQLException {
		databaseHelper = databaseHelperManager.getHelper(context);
		resourceDao = databaseHelper.getResourceDao();
	}

	public void close() {
		databaseHelper.close();
		databaseHelperManager.releaseHelper();
	}
}
