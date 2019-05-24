package info.ferrarimarco.uniroma2.msa.resourcesharing.app.services.config;

import android.content.Context;
import android.content.SharedPreferences;

import javax.inject.Inject;

import info.ferrarimarco.uniroma2.msa.resourcesharing.app.R;

public class SharedPreferencesServiceImpl{

    private SharedPreferences applicationSharedPreferences;
    private String gcmRegistrationIdKey;
    private String googleAccountNameKey;

    @Inject
    public SharedPreferencesServiceImpl(Context applicationContext){
        gcmRegistrationIdKey = applicationContext.getResources().getString(R.string.gcm_registration_id_key);
        googleAccountNameKey = applicationContext.getResources().getString(R.string.google_account_name_key);
        String sharedPreferencesId = applicationContext.getResources().getString(R.string.shared_preferences_id);
        applicationSharedPreferences = applicationContext.getSharedPreferences(sharedPreferencesId, Context.MODE_PRIVATE);
    }

    /**
     * Gets the current registration ID for application on GCM service, if there
     * is one.
     * <p/>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     * registration ID.
     */
    public String getGcmRegistrationId(){
        return applicationSharedPreferences.getString(gcmRegistrationIdKey, "");
    }

    /**
     * Stores the registration ID in the application's
     * {@code SharedPreferences}.
     *
     * @param regId registration ID
     */
    public void storeGcmRegistrationId(String regId){
        SharedPreferences.Editor editor = applicationSharedPreferences.edit();
        editor.putString(gcmRegistrationIdKey, regId);
        editor.apply();
    }

    public Boolean isGoogleSignInCompleted(){
        return applicationSharedPreferences.contains(googleAccountNameKey);
    }

    public String getGoogleAccountName(){
        return applicationSharedPreferences.getString(googleAccountNameKey, "");
    }

    public void storeGoogleAccountName(String googleAccountName){
        SharedPreferences.Editor editor = applicationSharedPreferences.edit();
        editor.putString(googleAccountNameKey, googleAccountName);
        editor.apply();
    }

    public boolean isGcmRegistrationCompleted(){
        return applicationSharedPreferences.contains(gcmRegistrationIdKey);
    }

    public String readRegisteredUserId(){
        return getGoogleAccountName();
    }

    public Boolean isRegistrationCompleted(){
        return this.isGoogleSignInCompleted();
    }

    public void registerNewUser(String userId){
        this.storeGoogleAccountName(userId);
    }
}
