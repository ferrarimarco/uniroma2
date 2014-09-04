package info.ferrarimarco.uniroma2.msa.resourcesharing.app.services.persistence;

import android.content.Context;

import javax.inject.Inject;

import info.ferrarimarco.uniroma2.msa.resourcesharing.app.services.impl.SharedPreferencesServiceImpl;

public class UserService extends AbstractPersistenceService {

    @Inject
    SharedPreferencesServiceImpl sharedPreferencesService;

    @Inject
    public UserService(Context context) {
        super(context);
    }

    public String readRegisteredUserId() {
        return sharedPreferencesService.getGoogleAccountName();
    }

    public Boolean isRegistrationCompleted() {
        return sharedPreferencesService.isGoogleSignInCompleted();
    }

    public void registerNewUser(String userId) {
        sharedPreferencesService.storeGoogleAccountName(userId);
    }
}
