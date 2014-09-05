package info.ferrarimarco.uniroma2.msa.resourcesharing.app.services.impl;

import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import javax.inject.Inject;

import info.ferrarimarco.uniroma2.msa.resourcesharing.app.BaseTezt;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.services.config.SharedPreferencesServiceImpl;

@Config(emulateSdk = 18)
@RunWith(RobolectricTestRunner.class)
public class SharedPreferencesServiceImplTest extends BaseTezt {

    @Inject
    SharedPreferencesServiceImpl sharedPreferencesService;

    public SharedPreferencesServiceImplTest() {
        super();
    }

    @Override
    public void dependencyInjectionTest() {
        this.singleDependencyCheck(sharedPreferencesService);
    }
}
