package info.ferrarimarco.uniroma2.msa.resourcesharing.app.services.gms;

import android.app.Activity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import javax.inject.Inject;

public class GooglePlayServiceUtils {

    private static final int REQUEST_CODE_RECOVER_PLAY_SERVICES = 1001;

    @Inject
    public GooglePlayServiceUtils() {
    }

    public void checkGooglePlayServicesInstallationStatus(Activity activity) {
        // Check status of Google Play Services
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);

        try {
            if (status != ConnectionResult.SUCCESS) {
                GooglePlayServicesUtil.getErrorDialog(status, activity, REQUEST_CODE_RECOVER_PLAY_SERVICES).show();
            }
        } catch (Exception e) {
            Log.e("Error: GooglePlayServiceUtil: ", "" + e);
        }
    }
}
