package info.ferrarimarco.uniroma2.msa.resourcesharing.app.services.impl;

import org.junit.AfterClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import javax.inject.Inject;

import info.ferrarimarco.uniroma2.msa.resourcesharing.app.BaseTezt;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

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

    @Test
    public void storeGcmRegistrationIdTest() {
        String registrationId = "test_gcm_id";
        sharedPreferencesService.storeGcmRegistrationId(registrationId);
        String registrationIdFromPrefs = sharedPreferencesService.getRegistrationId();
        assertThat(registrationId, equalTo(registrationIdFromPrefs));
    }

    @Test
    public void removeGcmRegistrationIdTest() {
        sharedPreferencesService.removeGcmRegistrationId();
        String registrationIdFromPrefs = sharedPreferencesService.getRegistrationId();
        // TODO: use hamcrest matcher in the Hamcrest library to check if this is null or empty
        //assertThat(registrationIdFromPrefs, );
    }

    @AfterClass
    public void teardown() {
        sharedPreferencesService.removeGcmRegistrationId();
    }
}
