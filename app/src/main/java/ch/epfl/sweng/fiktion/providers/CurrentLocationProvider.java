package ch.epfl.sweng.fiktion.providers;

import android.app.Activity;
import android.location.Location;

import com.google.android.gms.tasks.OnSuccessListener;

import ch.epfl.sweng.fiktion.android.AndroidServices;
import ch.epfl.sweng.fiktion.utils.Config;

/**
 * Created by Rodrigo on 11.12.2017.
 */

public abstract class CurrentLocationProvider {


    private static CurrentLocationProvider currentLocationProvider;

    /**
     * @return an instance of an authentication provider
     */
    public static CurrentLocationProvider getInstance(Activity ctx) {
        if (currentLocationProvider == null) {
            if (Config.TEST_MODE) {
                currentLocationProvider = new LocalFusedLocationProvider();
            } else {
                currentLocationProvider = new FusedLocationProvider(ctx);
            }
        }
        return currentLocationProvider;
    }

    /**
     * Destroys current authentication instance
     */
    public static void destroyInstance() {
        currentLocationProvider = null;
    }


    /**
     * Checks the permissions and starts a request for a lastLocation of the device
     *
     * @param ctx                 Activity that requests the lastLocation
     * @param listener            handles the actions after the request is successful
     * @param gpsDisabledCallback Action if the GPS is disabled
     */
    public abstract void getLastLocation(Activity ctx, final OnSuccessListener<Location> listener, final AndroidServices.OnGPSDisabledCallback gpsDisabledCallback);

}
