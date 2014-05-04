package info.ferrarimarco.uniroma2.msa.resourcesharing.app.modules.impl;

import android.content.Context;

import dagger.Module;
import dagger.Provides;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.modules.ContextModule;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.services.impl.HashingServiceImpl;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.services.impl.ResourceSharingServiceImpl;

@Module(injects = {ResourceSharingServiceImpl.class, HashingServiceImpl.class})
public class ContextModuleImpl implements ContextModule {

    private final Context mContext;

    public ContextModuleImpl(Context context) {
        mContext = context;
    }

    @Provides
    public Context provideContext() {
        return mContext;
    }

}
