package info.ferrarimarco.uniroma2.msa.resourcesharing.app.services.location;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

public class LocationService {

    private Geocoder geocoder;

    @Inject
    public LocationService(Context context) {
        geocoder = new Geocoder(context, Locale.getDefault());
    }

    public Address resolveAddress(Location location) {
        // Create a list to contain the result address
        List<Address> addresses = null;
        Address result = null;

        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        } catch (IOException e1) {
            Log.e(LocationService.class.getName(), "IO Exception in resolveAddress(): " + e1.getLocalizedMessage());
            // At least send lat e long
            addresses = new ArrayList<>();
            addresses.add(new Address(Locale.getDefault()));
            addresses.get(0).setLatitude(location.getLatitude());
            addresses.get(0).setLongitude(location.getLongitude());
        } catch (IllegalArgumentException e2) {
            // Error message to post in the log
            String errorString = "Illegal arguments " +
                    Double.toString(location.getLatitude()) +
                    " , " +
                    Double.toString(location.getLongitude()) +
                    " passed to address service";
            Log.e(LocationService.class.getName(), errorString);
            e2.printStackTrace();
        }
        // If the reverse geocode returned an address
        if (addresses != null && addresses.size() > 0) {
            result = addresses.get(0);
        } else {
            // TODO: handle this error condition
        }

        return result;
    }
}
