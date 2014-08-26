package info.ferrarimarco.uniroma2.msa.resourcesharing.app.services.impl;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import javax.inject.Inject;

import info.ferrarimarco.uniroma2.msa.resourcesharing.app.R;

public class SharedPreferencesServiceImpl {

    private SharedPreferences gcmSharedPreferences;
    private String gcmRegistrationIdKey;

    @Inject
    public SharedPreferencesServiceImpl(Context applicationContext) {
        gcmRegistrationIdKey = applicationContext.getResources().getString(R.string.gcm_registration_id_key);
        String gcmSharedPreferencesId = applicationContext.getResources().getString(R.string.gcm_shared_preferences_id);
        gcmSharedPreferences = applicationContext.getSharedPreferences(gcmSharedPreferencesId, Context.MODE_PRIVATE);
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
    public String getRegistrationId() {
        String registrationId = gcmSharedPreferences.getString(gcmRegistrationIdKey, "");
        if (registrationId == null || registrationId.equals("")) {
            Log.i(SharedPreferencesServiceImpl.class.getName(), "Registration not found.");
            return "";
        }

        return registrationId;
    }

    /**
     * Stores the registration ID in the application's
     * {@code SharedPreferences}.
     *
     * @param regId registration ID
     */
    public void storeGcmRegistrationId(String regId) {
        Log.i(SharedPreferencesServiceImpl.class.getName(), "Saving GCM Registration ID in Shared Preferencies. ID: " + regId);
        SharedPreferences.Editor editor = gcmSharedPreferences.edit();
        editor.putString(gcmRegistrationIdKey, regId);
        editor.commit();
    }

    /**
     * Removes the registration ID from the application's
     * {@code SharedPreferences}.
     */
    public void removeGcmRegistrationId() {
        Log.i(SharedPreferencesServiceImpl.class.getName(), "Removig regId from Shared Preferencies");
        SharedPreferences.Editor editor = gcmSharedPreferences.edit();
        editor.remove(gcmRegistrationIdKey);
        editor.commit();
    }
}
