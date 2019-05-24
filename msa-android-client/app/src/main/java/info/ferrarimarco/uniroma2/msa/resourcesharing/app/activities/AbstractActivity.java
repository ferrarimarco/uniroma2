package info.ferrarimarco.uniroma2.msa.resourcesharing.app.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.plus.Plus;
import com.squareup.otto.Bus;

import javax.inject.Inject;

import butterknife.ButterKnife;
import dagger.ObjectGraph;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.R;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.services.config.SharedPreferencesServiceImpl;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.services.gcm.GcmMessagingServiceImpl;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.services.gms.GooglePlayServiceUtils;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.util.ObjectGraphUtils;

public abstract class AbstractActivity extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    protected ObjectGraph objectGraph;

    @Inject
    GcmMessagingServiceImpl gcmMessagingService;

    @Inject
    SharedPreferencesServiceImpl sharedPreferencesService;

    @Inject
    Bus bus;

    @Inject
    GooglePlayServiceUtils googlePlayServiceUtils;

    GoogleApiClient googleApiClient;

    private static final int CONNECTION_FAILURE_ERROR_RESOLUTION = 1002;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        ButterKnife.inject(this);

        objectGraph = ObjectGraphUtils.getObjectGraph(getApplicationContext());
        objectGraph.inject(this);

        googlePlayServiceUtils.checkGooglePlayServicesInstallationStatus(this);

        // Initializing google plus api client
        googleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(Plus.API).addApi(LocationServices.API).addScope(Plus.SCOPE_PLUS_LOGIN).build();
    }

    @Override
    protected void onStart(){
        super.onStart();
        googlePlayServiceUtils.checkGooglePlayServicesInstallationStatus(this);
    }

    @Override
    protected void onResume(){
        super.onResume();
        googlePlayServiceUtils.checkGooglePlayServicesInstallationStatus(this);
        bus.register(this);

        if(!googleApiClient.isConnecting() && !googleApiClient.isConnected()){
            googleApiClient.connect();
        }
    }

    @Override
    protected void onPause(){
        super.onPause();

        // Always unregister when an object no longer should be on the bus.
        bus.unregister(this);

        if(googleApiClient.isConnected()){
            googleApiClient.disconnect();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        switch(requestCode){
            case GooglePlayServiceUtils.REQUEST_CODE_RECOVER_PLAY_SERVICES:
                if(resultCode == RESULT_CANCELED){
                    Toast.makeText(this, getResources().getString(R.string.google_play_services_missing_text), Toast.LENGTH_SHORT).show();
                    finish();
                }
                return;
            case CONNECTION_FAILURE_ERROR_RESOLUTION:
                if(resultCode == RESULT_OK){
                    // Try again to connect
                    googleApiClient.connect();
                }
                return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onConnectionSuspended(int arg0){
        // Reconnecting as we need the google api client for location updates
        if(!googleApiClient.isConnecting()){
            googleApiClient.connect();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result){
        // Google API client encountered an error while connecting

        if(!result.hasResolution()){
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this, 0).show();
        }else{
            try{
                result.startResolutionForResult(this, CONNECTION_FAILURE_ERROR_RESOLUTION);
            }catch(IntentSender.SendIntentException e){
                // Try again
                googleApiClient.connect();
            }
        }
    }
}
