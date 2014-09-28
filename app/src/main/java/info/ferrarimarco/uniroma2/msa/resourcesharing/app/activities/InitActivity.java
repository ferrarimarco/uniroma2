package info.ferrarimarco.uniroma2.msa.resourcesharing.app.activities;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.plus.Plus;
import com.squareup.otto.Subscribe;

import info.ferrarimarco.uniroma2.msa.resourcesharing.app.R;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.event.GcmRegistrationCompletedEvent;

public class InitActivity extends AbstractActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState){
        setContentView(R.layout.activity_init);
        super.onCreate(savedInstanceState);
    }

    @Subscribe
    public void gcmRegistrationCompletedEvent(GcmRegistrationCompletedEvent gcmRegistrationCompletedEvent){
        afterInit();
    }

    private void afterInit(){
        Intent startDestinationActivity = new Intent(this, ShowResourcesActivity.class);
        startActivity(startDestinationActivity);
        finish();
    }

    @Override
    public void onConnected(Bundle arg0){
        // Google API client is connected

        // Check user registration
        if(!sharedPreferencesService.isRegistrationCompleted() || !gcmMessagingService.isGcmRegistrationCompleted()){
            if(!sharedPreferencesService.isRegistrationCompleted()){
                // Get user's information
                if(Plus.PeopleApi.getCurrentPerson(googleApiClient) != null){
                    String accountName = Plus.AccountApi.getAccountName(googleApiClient);
                    sharedPreferencesService.registerNewUser(accountName);
                }else{
                    throw new IllegalArgumentException("No Google account name defined");
                }
            }

            if(!gcmMessagingService.isGcmRegistrationCompleted()){
                gcmMessagingService.registerWithGcm();
            }
        }else{
            // Registration is completed
            afterInit();
        }
    }
}
