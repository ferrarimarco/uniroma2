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
public class GenericDao<T extends GenericEntity>{

    private DatabaseHelperManager databaseHelperManager;
    private DatabaseHelper databaseHelper;

    private Dao<T, ?> dao;

    private Context context;

    private boolean initialized;

    @Inject
    public GenericDao(DatabaseHelperManager databaseHelperManager, Context context){
        this.databaseHelperManager = databaseHelperManager;
        this.context = context;
        initialized = false;
    }

    protected void checkInitialization(){
        if(!initialized){
            throw new DaoException("This DAO has not been initialized");
        }
    }

    public void open(final Class<T> entityClass) throws SQLException{
        databaseHelper = databaseHelperManager.getHelper(context);
        dao = databaseHelper.getEntityDao(entityClass);
        initialized = true;
    }

    public void close(){
        if(initialized){
            databaseHelper.close();
            databaseHelperManager.releaseHelper();
        }
    }

    public int save(T resource) throws SQLException{
        checkInitialization();
        return dao.create(resource);
    }

    public List<T> read(T resource) throws SQLException{
        checkInitialization();
        return dao.queryForMatching(resource);
    }

    public int update(T resource) throws SQLException{
        checkInitialization();
        return dao.update(resource);
    }

    public int delete(T resource) throws SQLException{
        checkInitialization();
        return dao.delete(resource);
    }
}
