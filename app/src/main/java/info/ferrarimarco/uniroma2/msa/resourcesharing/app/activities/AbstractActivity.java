package info.ferrarimarco.uniroma2.msa.resourcesharing.app.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.squareup.otto.Bus;

import javax.inject.Inject;

import butterknife.ButterKnife;
import dagger.ObjectGraph;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.R;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.services.gcm.GcmMessagingServiceImpl;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.services.gms.GooglePlayServiceUtils;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.services.persistence.UserService;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.util.ObjectGraphUtils;

public abstract class AbstractActivity extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    protected ObjectGraph objectGraph;

    @Inject
    GcmMessagingServiceImpl gcmMessagingService;

    @Inject
    UserService userService;

    @Inject
    Bus bus;

    @Inject
    GooglePlayServiceUtils googlePlayServiceUtils;

    private GoogleApiClient googleApiClient;

    private static final int SIGN_IN_ERROR_RESOLUTION = 1002;

    /**
     * A flag indicating that a PendingIntent is in progress and prevents us
     * from starting further intents.
     */
    private boolean intentInProgress;

    private ConnectionResult connectionResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ButterKnife.inject(this);

        objectGraph = ObjectGraphUtils.getObjectGraph(this.getApplicationContext());
        objectGraph.inject(this);

        googlePlayServiceUtils.checkGooglePlayServicesInstallationStatus(this);

        // Initializing google plus api client
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(Plus.API, null)
                .addScope(Plus.SCOPE_PLUS_LOGIN).build();
    }

    @Override
    protected void onStart() {
        super.onStart();

        googlePlayServiceUtils.checkGooglePlayServicesInstallationStatus(this);
        checkRegisteredUser();
    }

    @Override
    protected void onResume() {
        super.onResume();
        googlePlayServiceUtils.checkGooglePlayServicesInstallationStatus(this);
        checkGcmRegistration();
        checkRegisteredUser();
        bus.register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Always unregister when an object no longer should be on the bus.
        bus.unregister(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case GooglePlayServiceUtils.REQUEST_CODE_RECOVER_PLAY_SERVICES:
                if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, getResources().getString(R.string.google_play_services_missing_text), Toast.LENGTH_SHORT).show();
                    finish();
                }
                return;
            case SIGN_IN_ERROR_RESOLUTION:
                intentInProgress = false;
                if (resultCode != RESULT_OK) {
                    // TODO: handle this error condition
                }

                if (!googleApiClient.isConnecting()) {
                    googleApiClient.connect();
                }
                return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        googleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle arg0) {
        // Get user's information
        try {
            if (Plus.PeopleApi.getCurrentPerson(googleApiClient) != null) {
                String accountName = Plus.AccountApi.getAccountName(googleApiClient);
                userService.registerNewUser(accountName);
            } else {
                // TODO: handle this error condition
            }
        } catch (Exception e) {
            e.printStackTrace();
            // TODO: handle this error condition
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (!result.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this, 0).show();
            return;
        }

        if (!intentInProgress) {
            // Store the ConnectionResult for later usage
            connectionResult = result;
            resolveSignInError();
        }
    }

    private void checkRegisteredUser() {
        if (userService.isRegistrationCompleted()) {
            Intent intent = new Intent(this, ShowResourcesActivity.class);
            startActivity(intent);
            finish();
        } else {
            // Sign-in into google
            if (!googleApiClient.isConnecting()) {
                intentInProgress = true;
                resolveSignInError();
            }
        }
    }

    private void resolveSignInError() {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(this, SIGN_IN_ERROR_RESOLUTION);
            } catch (IntentSender.SendIntentException e) {
                intentInProgress = false;
                googleApiClient.connect();
            }
        }
    }

    private void checkGcmRegistration() {
        if (!gcmMessagingService.isGcmRegistrationCompleted()) {
            gcmMessagingService.registerWithGcm();
        }
    }
}
