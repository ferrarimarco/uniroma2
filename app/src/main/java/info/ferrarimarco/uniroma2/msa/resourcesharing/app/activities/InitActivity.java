package info.ferrarimarco.uniroma2.msa.resourcesharing.app.activities;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;

import javax.inject.Inject;

import butterknife.InjectView;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.R;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.services.gms.GooglePlayServiceUtils;

public class InitActivity extends AbstractAsyncTaskActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int RC_SIGN_IN = 0;

    @InjectView(R.id.init_progress)
    View mProgressView;

    @InjectView(R.id.init_content)
    View mInitContentView;

    @Inject
    GooglePlayServiceUtils googlePlayServiceUtils;

    private GoogleApiClient googleApiClient;

    /**
     * A flag indicating that a PendingIntent is in progress and prevents us
     * from starting further intents.
     */
    private boolean intentInProgress;

    private ConnectionResult connectionResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        objectGraph.inject(this);

        // Initializing google plus api client
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(Plus.API, null)
                .addScope(Plus.SCOPE_PLUS_LOGIN).build();

        if (this.getActionBar() != null) {
            this.getActionBar().hide();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);

        this.defaultInitialization(mProgressView, mInitContentView);

        // check for Google Play Services
        googlePlayServiceUtils.checkGooglePlayServicesInstallationStatus(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        googleApiClient.connect();

        // Create an async task to check for reg user
        checkRegisteredUser();
    }

    protected void onStop() {
        super.onStop();
        if (googleApiClient.isConnected()) {
            googleApiClient.disconnect();
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

    @Override
    protected void onActivityResult(int requestCode, int responseCode,
                                    Intent intent) {
        if (requestCode == RC_SIGN_IN) {
            if (responseCode != RESULT_OK) {
                // TODO: handle this error condition
            }

            intentInProgress = false;

            if (!googleApiClient.isConnecting()) {
                googleApiClient.connect();
            }
        }
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
    public void onConnectionSuspended(int arg0) {
        googleApiClient.connect();
    }

    /**
     * Sign-in into google
     */
    private void signInWithGplus() {
        if (!googleApiClient.isConnecting()) {
            resolveSignInError();
        }
    }

    /**
     * Method to resolve any sign in errors
     */
    private void resolveSignInError() {
        if (connectionResult.hasResolution()) {
            try {
                intentInProgress = true;
                connectionResult.startResolutionForResult(this, RC_SIGN_IN);
            } catch (IntentSender.SendIntentException e) {
                intentInProgress = false;
                googleApiClient.connect();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.init, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }

    private void checkRegisteredUser() {
        showProgress(true);

        Boolean userRegistrationCompleted = userService.isRegistrationCompleted();

        if (userRegistrationCompleted) {
            Intent intent = new Intent(this, ShowResourcesActivity.class);
            startActivity(intent);
            finish();
        } else {
            signInWithGplus();
        }
    }
}
