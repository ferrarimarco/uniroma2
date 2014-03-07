package info.ferrarimarco.uniroma2.msa.resourcesharing.app.modules;

import info.ferrarimarco.uniroma2.msa.resourcesharing.app.dao.ResourceDao;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.dao.helper.DatabaseHelperManager;


public interface DaoModule {
	DatabaseHelperManager provideDatabaseHelperManager();

	ResourceDao provideResourceDao(DatabaseHelperManager databaseHelperManager);
}
