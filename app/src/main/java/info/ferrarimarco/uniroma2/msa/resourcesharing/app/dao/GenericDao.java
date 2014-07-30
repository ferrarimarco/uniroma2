package info.ferrarimarco.uniroma2.msa.resourcesharing.app.dao;

import android.content.Context;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;

import javax.inject.Inject;

import info.ferrarimarco.uniroma2.msa.resourcesharing.app.dao.helper.DatabaseHelper;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.dao.helper.DatabaseHelperManager;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.exceptions.DaoException;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.GenericEntity;

/**
 * Created by Marco on 12/07/2014.
 */
public class GenericDao<T extends GenericEntity> {

    private Context context;
    private DatabaseHelperManager databaseHelperManager;
    private DatabaseHelper databaseHelper;

    private Dao<T, ?> dao;

    @Inject
    public GenericDao(DatabaseHelperManager databaseHelperManager, Context context) {
        this.databaseHelperManager = databaseHelperManager;
        this.context = context;
    }

    protected void checkInitialization() throws SQLException {
        if (dao == null) {
            throw new DaoException("This dao is not initialized. Call open() on the GenericDao object before any use.");
        }
    }

    public void open(final Class<T> entityClass) throws SQLException {
        databaseHelper = this.databaseHelperManager.getHelper(context);
        dao = databaseHelper.getEntityDao(entityClass);
    }

    public void close() {
        databaseHelperManager.releaseHelper();
    }

    public int save(T resource) throws SQLException {
        checkInitialization();
        return dao.create(resource);
    }

    public List<T> read(T resource) throws SQLException {
        checkInitialization();
        return dao.queryForMatching(resource);
    }

    public List<T> readAll() throws SQLException {
        checkInitialization();
        return dao.queryForAll();
    }

    public int update(T resource) throws SQLException {
        checkInitialization();
        return dao.update(resource);
    }

    public int delete(T resource) throws SQLException {
        checkInitialization();
        return dao.delete(resource);
    }
}
