package info.ferrarimarco.uniroma2.msa.resourcesharing.app.modules.impl;

import dagger.Module;
import dagger.Provides;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.activities.RegisterNewUserActivity;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.tasks.RegisterNewUserAsyncTask;

/**
 * Created by Marco on 17/07/2014.
 */
@Module(injects = {RegisterNewUserActivity.class}, complete = false)
public class TaskModuleImpl{

    @Provides
    public RegisterNewUserAsyncTask provideRegisterNewUserAsyncTask(){
        return new RegisterNewUserAsyncTask();
    }
}
