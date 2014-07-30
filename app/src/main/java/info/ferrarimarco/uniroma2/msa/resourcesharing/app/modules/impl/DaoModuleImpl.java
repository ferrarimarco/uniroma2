package info.ferrarimarco.uniroma2.msa.resourcesharing.app.modules.impl;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.activities.InitActivity;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.activities.RegisterNewUserActivity;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.dao.GenericDao;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.dao.helper.DatabaseHelperManager;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.Resource;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.User;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.modules.DaoModule;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.tasks.RegisterNewUserAsyncTask;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.tasks.RegisteredUserCheckTask;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.tasks.UserAsyncTask;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.tasks.UserLoginAsyncTask;


@Module(injects = {GenericDao.class, InitActivity.class, RegisterNewUserActivity.class, UserAsyncTask.class, RegisterNewUserAsyncTask.class, UserLoginAsyncTask.class, RegisteredUserCheckTask.class}, complete = false, library = true)
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
