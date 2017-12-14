package ch.epfl.sweng.fiktion.providers;

import android.app.Activity;
import android.location.Location;

import com.google.android.gms.tasks.OnSuccessListener;

import ch.epfl.sweng.fiktion.android.AndroidServices;

/**
 * Provider of lastPosition for testing
 * Created by Rodrigo on 11.12.2017.
 */

class LocalFusedLocationProvider extends CurrentLocationProvider {
    /**
     * Checks the permissions and starts a request for a lastLocation of the device
     *
     * @param ctx      Activity that requests the lastLocation
     * @param listener handles the actions after the request is successful
     */
    @Override
    public void getLastLocation(Activity ctx, OnSuccessListener<Location> listener, AndroidServices.OnGPSDisabledCallback gpsDisabledCallback) {
        Location defaultLocation = new Location("mockprovider");
        defaultLocation.setLatitude(6.56);
        defaultLocation.setLongitude(46.5167);
        listener.onSuccess(defaultLocation);
    }
}
