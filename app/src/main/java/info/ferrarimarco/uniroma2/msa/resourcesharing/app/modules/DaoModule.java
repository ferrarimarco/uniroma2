package info.ferrarimarco.uniroma2.msa.resourcesharing.app.modules;

import info.ferrarimarco.uniroma2.msa.resourcesharing.app.dao.ResourceDao;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.dao.helper.DatabaseHelperManager;

/**
 * Created by Marco on 03/03/14.
 */
public interface DaoModule {
	DatabaseHelperManager provideDatabaseHelperManager();

	ResourceDao provideResourceDao(DatabaseHelperManager databaseHelperManager);
}
