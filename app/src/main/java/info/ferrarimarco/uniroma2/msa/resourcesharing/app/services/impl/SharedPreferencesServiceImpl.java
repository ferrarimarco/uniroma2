package info.ferrarimarco.uniroma2.msa.resourcesharing.app.services.impl;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import javax.inject.Inject;

/**
 * Created by Marco on 11/08/2014.
 */
public class SharedPreferencesServiceImpl {

    private Context applicationContext;

    @Inject
    public SharedPreferencesServiceImpl(Context applicationContext) {
        this.applicationContext = applicationContext;
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
    private String getRegistrationId() {
        final SharedPreferences prefs = getGcmPreferences();
        String registrationId = prefs.getString(Globals.PREFS_PROPERTY_REG_ID, "");
        if (registrationId == null || registrationId.equals("")) {
            Log.i(Globals.TAG, "Registration not found.");
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
        final SharedPreferences prefs = getGcmPreferences(context);
        Log.i(Globals.TAG, "Saving GCM Registration ID in Shared Preferencies. ID: " + regId);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Globals.PREFS_PROPERTY_REG_ID, regId);
        editor.commit();
    }

    /**
     * Removes the registration ID from the application's
     * {@code SharedPreferences}.
     */
    public void removeGcmRegistrationId() {
        final SharedPreferences prefs = getGcmPreferences();
        Log.i(Globals.TAG, "Removig regId from Shared Preferencies");
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(Globals.PREFS_PROPERTY_REG_ID);
        editor.commit();
        gcmRegId = null;
    }

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGcmPreferences() {
        // This sample app persists the registration ID in shared preferences,
        // but how you store the regID in your app is up to you.
        return applicationContext.getSharedPreferences(Globals.PREFS_NAME, Context.MODE_PRIVATE);
    }
}
