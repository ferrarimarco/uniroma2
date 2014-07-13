package info.ferrarimarco.uniroma2.msa.resourcesharing.app.modules;

import android.content.Context;

import info.ferrarimarco.uniroma2.msa.resourcesharing.app.dao.GenericDao;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.dao.helper.DatabaseHelperManager;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.Resource;

/**
 * Created by Marco on 13/07/2014.
 */
public interface DaoModule{

    DatabaseHelperManager provideDatabaseHelperManager();

    GenericDao<Resource> provideResourceDao(DatabaseHelperManager databaseHelperManager, Context context);
}
