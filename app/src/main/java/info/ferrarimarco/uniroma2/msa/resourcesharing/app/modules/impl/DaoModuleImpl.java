package info.ferrarimarco.uniroma2.msa.resourcesharing.app.modules.impl;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.activities.CreateNewResourceActivity;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.activities.InitActivity;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.activities.RegisterNewUserActivity;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.activities.ShowResourcesActivity;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.dao.GenericDao;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.dao.helper.DatabaseHelperManager;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.Resource;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.User;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.modules.DaoModule;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.tasks.resource.ReadAllResourcesAsyncTask;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.tasks.user.RegisterNewUserAsyncTask;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.tasks.user.RegisteredUserCheckAsyncTask;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.tasks.user.UserLoginAsyncTask;


@Module(injects = {GenericDao.class, InitActivity.class, RegisterNewUserActivity.class, ShowResourcesActivity.class, CreateNewResourceActivity.class, ReadAllResourcesAsyncTask.class, RegisterNewUserAsyncTask.class, UserLoginAsyncTask.class, RegisteredUserCheckAsyncTask.class}, complete = false, library = true)
public class DaoModuleImpl implements DaoModule {

    @Override
    @Provides
    @Singleton
    public GenericDao<Resource> provideResourceDao(DatabaseHelperManager databaseHelperManager, Context context) {
        return new GenericDao<>(databaseHelperManager, context);
    }

    @Override
    @Provides
    @Singleton
    public GenericDao<User> provideUserDao(DatabaseHelperManager databaseHelperManager, Context context) {
        return new GenericDao<>(databaseHelperManager, context);
    }
}
