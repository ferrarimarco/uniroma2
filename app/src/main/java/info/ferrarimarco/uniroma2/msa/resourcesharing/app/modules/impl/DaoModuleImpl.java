package info.ferrarimarco.uniroma2.msa.resourcesharing.app.modules.impl;

import android.content.Context;

import java.sql.SQLException;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.dao.GenericDao;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.dao.helper.DatabaseHelperManager;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.Resource;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.modules.DaoModule;


@Module(injects = {GenericDao.class}, complete = false, library = true)
public class DaoModuleImpl implements DaoModule{

    @Provides
    @Singleton
    public DatabaseHelperManager provideDatabaseHelperManager(){
        return new DatabaseHelperManager();
    }

    @Provides
    @Singleton
    public GenericDao<Resource> provideResourceDao(DatabaseHelperManager databaseHelperManager, Context context){

        GenericDao<Resource> dao = null;
        try{
            dao = new GenericDao<>(databaseHelperManager, context);
            dao.open(Resource.class);
        }catch(SQLException e){
            // TODO: handle this exception
            e.printStackTrace();
        }

        return dao;
    }
}
