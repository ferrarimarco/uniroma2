package info.ferrarimarco.uniroma2.msa.resourcesharing.app.dao;

import android.content.Context;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;

import javax.inject.Inject;

import info.ferrarimarco.uniroma2.msa.resourcesharing.app.dao.helper.DatabaseHelper;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.dao.helper.DatabaseHelperManager;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.GenericEntity;

public class GenericDao<T extends GenericEntity>{

    private Context context;
    private DatabaseHelperManager databaseHelperManager;

    private Dao<T, ?> dao;

    @Inject
    public GenericDao(DatabaseHelperManager databaseHelperManager, Context context){
        this.databaseHelperManager = databaseHelperManager;
        this.context = context;
    }

    protected void checkInitialization() throws SQLException{
        if(dao == null){
            throw new SQLException("This dao is not initialized. Call open() on the GenericDao object before any use.");
        }
    }

    public void open(final Class<T> entityClass) throws SQLException{
        DatabaseHelper databaseHelper = this.databaseHelperManager.getHelper(context);
        dao = databaseHelper.getEntityDao(entityClass);
    }

    public void close(){
        databaseHelperManager.releaseHelper();
    }

    public int save(T resource) throws SQLException{
        checkInitialization();
        return dao.create(resource);
    }

    public List<T> read(T resource) throws SQLException{
        checkInitialization();
        return dao.queryForMatching(resource);
    }

    public T readUniqueResult(T resource) throws SQLException{
        checkInitialization();

        List<T> resources = dao.queryForMatching(resource);
        T result = null;

        if(resources != null){
            if(resources.size() > 1){
                throw new SQLException("More than one entry found for given criterion");
            }else{
                result = resources.get(0);
            }
        }

        return result;
    }

    public List<T> readAll() throws SQLException{
        checkInitialization();
        return dao.queryForAll();
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
