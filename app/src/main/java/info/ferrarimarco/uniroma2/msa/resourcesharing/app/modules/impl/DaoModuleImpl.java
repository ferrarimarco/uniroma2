package info.ferrarimarco.uniroma2.msa.resourcesharing.app.modules.impl;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.activities.CreateNewResourceActivity;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.activities.InitActivity;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.activities.ShowResourcesActivity;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.dao.GenericDao;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.dao.helper.DatabaseHelperManager;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.Resource;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.modules.DaoModule;


@Module(injects = {GenericDao.class, InitActivity.class, ShowResourcesActivity.class, CreateNewResourceActivity.class}, complete = false, library = true)
public class DaoModuleImpl implements DaoModule {

    @Override
    @Provides
    @Singleton
    public GenericDao<Resource> provideResourceDao(DatabaseHelperManager databaseHelperManager, Context context) {
        return new GenericDao<>(databaseHelperManager, context);
    }
}
