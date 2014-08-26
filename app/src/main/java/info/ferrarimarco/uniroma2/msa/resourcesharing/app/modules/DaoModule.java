package info.ferrarimarco.uniroma2.msa.resourcesharing.app.modules;

import android.content.Context;

import info.ferrarimarco.uniroma2.msa.resourcesharing.app.dao.GenericDao;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.dao.helper.DatabaseHelperManager;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.Resource;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.User;

public interface DaoModule {
    GenericDao<Resource> provideResourceDao(DatabaseHelperManager databaseHelperManager, Context context);

    GenericDao<User> provideUserDao(DatabaseHelperManager databaseHelperManager, Context context);
}
