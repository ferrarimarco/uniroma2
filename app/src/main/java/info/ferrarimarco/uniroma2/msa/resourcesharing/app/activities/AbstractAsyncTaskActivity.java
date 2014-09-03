package info.ferrarimarco.uniroma2.msa.resourcesharing.app.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.squareup.otto.Bus;

import javax.inject.Inject;

import butterknife.ButterKnife;
import dagger.ObjectGraph;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.services.gcm.GcmMessagingServiceImpl;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.services.gms.GooglePlayServiceUtils;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.services.persistence.ResourceService;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.services.persistence.UserService;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.util.ObjectGraphUtils;

public abstract class AbstractAsyncTaskActivity extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private View progressBarView;
    private View mainView;

    protected ObjectGraph objectGraph;

    @Inject
    GcmMessagingServiceImpl gcmMessagingService;

    @Inject
    UserService userService;

    @Inject
    ResourceService resourceService;

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

    protected void defaultInitialization(View progressBarView, View mainView) {
        this.progressBarView = progressBarView;
        this.mainView = mainView;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ButterKnife.inject(this);

        objectGraph = ObjectGraphUtils.getObjectGraph(this.getApplicationContext());
        objectGraph.inject(this);

        bus.register(this);

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

        if (!googleApiClient.isConnected()) {
            googleApiClient.connect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        googlePlayServiceUtils.checkGooglePlayServicesInstallationStatus(this);
        checkRegisteredUser();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case GooglePlayServiceUtils.REQUEST_CODE_RECOVER_PLAY_SERVICES:
                if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "Google Play Services must be installed.", Toast.LENGTH_SHORT).show();
                    // TODO: write this message in strings.xml
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
        Toast.makeText(this, "User is connected!", Toast.LENGTH_LONG).show();

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

    /**
     * Shows the progress UI and hides the form.
     */
    public void showProgress(final boolean show) {

        if (mainView == null || progressBarView == null) {
            throw new IllegalStateException("The activity is not correctly initialized. Main view or progress bar view is null");
        }

        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mainView.setVisibility(show ? View.GONE : View.VISIBLE);
        mainView.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mainView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        progressBarView.setVisibility(show ? View.VISIBLE : View.GONE);
        progressBarView.animate().setDuration(shortAnimTime).alpha(show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                progressBarView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    private void checkRegisteredUser() {
        showProgress(true);

        Boolean userRegistrationCompleted = userService.isRegistrationCompleted();

        if (userRegistrationCompleted) {
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
}
