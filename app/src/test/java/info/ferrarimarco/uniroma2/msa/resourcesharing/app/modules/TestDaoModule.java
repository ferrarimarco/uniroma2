package info.ferrarimarco.uniroma2.msa.resourcesharing.app.modules;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.dao.ResourceDao;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.dao.ResourceDaoTest;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.dao.helper.DatabaseHelperManager;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.dao.helper.DatabaseHelperManagerTest;


@Module(injects = {ResourceDao.class, ResourceDaoTest.class, DatabaseHelperManagerTest.class})
public class TestDaoModule implements DaoModule {
	@Provides
	@Singleton
	public DatabaseHelperManager provideDatabaseHelperManager() {
		return new DatabaseHelperManager();
	}

	@Provides
	@Singleton
	public ResourceDao provideResourceDao(DatabaseHelperManager databaseHelperManager) {
		return new ResourceDao(databaseHelperManager);
	}
}
