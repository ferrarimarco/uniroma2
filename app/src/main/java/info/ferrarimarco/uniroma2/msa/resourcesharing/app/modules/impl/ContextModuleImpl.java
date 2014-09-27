package info.ferrarimarco.uniroma2.msa.resourcesharing.app.modules.impl;

import android.content.Context;

import com.squareup.otto.Bus;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.activities.AbstractActivity;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.dao.GenericDao;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.dao.helper.DatabaseHelperManager;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.Resource;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.modules.DaoModule;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.services.gcm.GcmMessagingServiceImpl;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.services.persistence.ResourceService;

@Module(injects = {GenericDao.class, GcmMessagingServiceImpl.class, AbstractActivity.class, ResourceService.class})
public class ContextModuleImpl implements DaoModule{

    private final Context applicationContext;

    public ContextModuleImpl(Context context){
        this.applicationContext = context.getApplicationContext();
    }

    @Provides
    @Singleton
    public Context provideApplicationContext() {
        return applicationContext;
    }

    @Provides
    @Singleton
    public Bus provideBus() {
        return new Bus();
    }

    @Override
    @Provides
    @Singleton
    public GenericDao<Resource> provideResourceDao(DatabaseHelperManager databaseHelperManager, Context context){
        return new GenericDao<>(databaseHelperManager, context);
    }
}
