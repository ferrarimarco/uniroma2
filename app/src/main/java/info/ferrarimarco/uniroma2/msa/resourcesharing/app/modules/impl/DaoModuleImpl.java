package info.ferrarimarco.uniroma2.msa.resourcesharing.app.modules.impl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.dao.ResourceDao;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.dao.helper.DatabaseHelperManager;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.modules.DaoModule;

/**
 * Created by Marco on 03/03/14.
 */
@Module(injects = {ResourceDao.class})
public class DaoModuleImpl implements DaoModule {

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
