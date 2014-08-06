package info.ferrarimarco.uniroma2.msa.resourcesharing.app.modules.impl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.activities.CreateNewResourceActivity;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.services.rest.BackendRestService;
import retrofit.RestAdapter;

@Module(injects = {CreateNewResourceActivity.class})
public class RestServiceModuleImpl {

    private RestAdapter restAdapter;

    public RestServiceModuleImpl(String endpoint){
        restAdapter = new RestAdapter.Builder().setEndpoint(endpoint).build();
    }

    @Provides
    @Singleton
    public BackendRestService provideBackendRestService(){
        return restAdapter.create(BackendRestService.class);
    }
}
