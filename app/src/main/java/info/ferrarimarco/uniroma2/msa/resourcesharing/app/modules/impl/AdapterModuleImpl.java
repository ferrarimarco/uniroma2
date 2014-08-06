package info.ferrarimarco.uniroma2.msa.resourcesharing.app.modules.impl;

import android.content.Context;

import dagger.Module;
import dagger.Provides;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.activities.ShowResourcesActivity;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.adapters.ResourceArrayAdapter;

@Module(injects = {ShowResourcesActivity.class})
public class AdapterModuleImpl {

    private Context context;

    public AdapterModuleImpl(Context context){
        this.context = context;
    }

    @Provides
    public ResourceArrayAdapter provideResourceArrayAdapter(){
        return new ResourceArrayAdapter(context);
    }
}
