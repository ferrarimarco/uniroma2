package info.ferrarimarco.uniroma2.msa.resourcesharing.app.modules;

import android.content.Context;

import dagger.Provides;

public interface ContextModule {
    @Provides
    Context provideContext();
}
