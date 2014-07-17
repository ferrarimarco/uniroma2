package info.ferrarimarco.uniroma2.msa.resourcesharing.app.modules.impl;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.services.HashingService;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.services.ResourceSharingService;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.services.impl.HashingServiceImpl;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.services.impl.ResourceSharingServiceImpl;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.tasks.RegisterNewUserAsyncTask;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.tasks.UserLoginAsyncTask;

/**
 * Created by Marco on 17/07/2014.
 */
@Module(injects = {RegisterNewUserAsyncTask.class, UserLoginAsyncTask.class}, complete = false, library = true)
public class ServiceModuleImpl{

    @Provides
    @Singleton
    public ResourceSharingService provideResourceSharingService(Context context){
        return new ResourceSharingServiceImpl(context);
    }

    @Provides
    @Singleton
    public HashingService provideHashingService(Context context){
        return new HashingServiceImpl(context);
    }

}
