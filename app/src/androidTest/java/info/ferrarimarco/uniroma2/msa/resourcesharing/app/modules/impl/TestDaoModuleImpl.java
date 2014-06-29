package info.ferrarimarco.uniroma2.msa.resourcesharing.app.modules.impl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.dao.ResourceDao;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.dao.ResourceDaoTest;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.dao.helper.DatabaseHelperManager;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.dao.helper.DatabaseHelperManagerTest;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.modules.DaoModule;

@Module(injects = {ResourceDao.class, ResourceDaoTest.class, DatabaseHelperManagerTest.class})
public class TestDaoModuleImpl implements DaoModule {

    @Override
    @Provides
    @Singleton
    public DatabaseHelperManager provideDatabaseHelperManager() {
        return new DatabaseHelperManager();
    }

    @Override
    @Provides
    @Singleton
    public ResourceDao provideResourceDao(DatabaseHelperManager databaseHelperManager) {
        return new ResourceDao(databaseHelperManager);
    }
}
