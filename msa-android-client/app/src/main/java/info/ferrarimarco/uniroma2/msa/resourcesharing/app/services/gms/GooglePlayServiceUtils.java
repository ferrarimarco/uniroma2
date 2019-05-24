package info.ferrarimarco.uniroma2.msa.resourcesharing.app.services.gms;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import javax.inject.Inject;

public class GooglePlayServiceUtils {

    public static final int REQUEST_CODE_RECOVER_PLAY_SERVICES = 1001;

    @Inject
    public GooglePlayServiceUtils() {
    }

    public void checkGooglePlayServicesInstallationStatus(Activity activity) {
        // Check status of Google Play Services
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);

        try {
            if (status != ConnectionResult.SUCCESS) {
                if (GooglePlayServicesUtil.isUserRecoverableError(status)) {
                    showErrorDialog(status, activity);
                } else {
                    Toast.makeText(activity, "This device is not supported.", Toast.LENGTH_LONG).show();
                    activity.finish();
                }
            }
        } catch (Exception e) {
            Log.e("Error: GooglePlayServiceUtil: ", "" + e);
        }
    }

    private void showErrorDialog(int code, Activity activity) {
        GooglePlayServicesUtil.getErrorDialog(code, activity, REQUEST_CODE_RECOVER_PLAY_SERVICES).show();
    }
}
