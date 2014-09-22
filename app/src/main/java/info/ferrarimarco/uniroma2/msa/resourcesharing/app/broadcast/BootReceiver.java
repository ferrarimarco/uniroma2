package info.ferrarimarco.uniroma2.msa.resourcesharing.app.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.android.gms.location.LocationRequest;

public class BootReceiver extends BroadcastReceiver {
    public BootReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        LocationRequest noPowerlocationRequest = LocationRequest.create();
        noPowerlocationRequest.setPriority(LocationRequest.PRIORITY_NO_POWER);
        noPowerlocationRequest.setFastestInterval(1000 * 5);
    }
}
