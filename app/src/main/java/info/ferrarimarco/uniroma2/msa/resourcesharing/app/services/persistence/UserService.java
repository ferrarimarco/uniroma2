package info.ferrarimarco.uniroma2.msa.resourcesharing.app.services.persistence;

import javax.inject.Inject;

import info.ferrarimarco.uniroma2.msa.resourcesharing.app.services.config.SharedPreferencesServiceImpl;

public class UserService {

    @Inject
    SharedPreferencesServiceImpl sharedPreferencesService;

    @Inject
    public UserService() {
        super();
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
