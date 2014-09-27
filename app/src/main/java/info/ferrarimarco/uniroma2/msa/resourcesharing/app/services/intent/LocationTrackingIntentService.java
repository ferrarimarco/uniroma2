package info.ferrarimarco.uniroma2.msa.resourcesharing.app.services.intent;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;

import com.google.android.gms.location.LocationClient;

import dagger.ObjectGraph;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.util.ObjectGraphUtils;

public class LocationTrackingIntentService extends IntentService{

    public LocationTrackingIntentService(){
        super("LocationTrackingIntentService");
    }

    @Override
    public void onCreate(){
        ObjectGraph objectGraph = ObjectGraphUtils.getObjectGraph(getApplicationContext());
        objectGraph.inject(this);
    }

    @Override
    protected void onHandleIntent(Intent intent){
        Location location = intent.getParcelableExtra(LocationClient.KEY_LOCATION_CHANGED);
        if(location != null){
            UserIntentService.startActionUpdateUserInfo(this, location);
        }
    }
}
