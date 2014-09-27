package info.ferrarimarco.uniroma2.msa.resourcesharing.app.modules.impl;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.dao.GenericDao;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.dao.GenericDaoTest;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.dao.helper.DatabaseHelperManager;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.dao.helper.DatabaseHelperManagerTest;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.Resource;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.modules.DaoModule;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.services.impl.SharedPreferencesServiceImplTest;

@Module(injects = {GenericDaoTest.class, DatabaseHelperManagerTest.class, SharedPreferencesServiceImplTest.class}, complete = false, overrides = true)
public class TestContextModuleImpl implements DaoModule{

    @Override
    @Provides
    @Singleton
    public GenericDao<Resource> provideResourceDao(DatabaseHelperManager databaseHelperManager, Context context) {
        return new GenericDao<>(databaseHelperManager, context);
    }
}
