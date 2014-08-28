package info.ferrarimarco.uniroma2.msa.resourcesharing.app.modules.impl;

import android.content.Context;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import dagger.Module;
import dagger.Provides;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.services.gcm.GcmMessagingServiceImpl;

@Module(injects = {GcmMessagingServiceImpl.class}, complete = false, library = true)
public class GcmModuleImpl {

    @Provides
    public GoogleCloudMessaging provideGoogleCloudMessaging(Context context) {
        return GoogleCloudMessaging.getInstance(context);
    }
}
