package info.ferrarimarco.uniroma2.msa.resourcesharing.app.services.intent;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;

import com.google.android.gms.location.LocationClient;

public class LocationTrackingIntentService extends IntentService{

    public LocationTrackingIntentService(){
        super("LocationTrackingIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent){
        Location location = intent.getParcelableExtra(LocationClient.KEY_LOCATION_CHANGED);
        if(location != null){
            UserIntentService.startActionUpdateUserInfo(this, location);
        }
    }
}
