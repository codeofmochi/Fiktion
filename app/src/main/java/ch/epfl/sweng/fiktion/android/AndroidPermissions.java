package ch.epfl.sweng.fiktion.android;

import android.Manifest;
import android.app.Activity;
import android.support.v4.app.ActivityCompat;

/**
 * Helper statics for permissions requests
 * Created by dialexo on 17.10.17.
 */

public final class AndroidPermissions {

    public static final int MY_PERMISSIONS_FINE_LOCATION = 1;

    /**
     * Prompts location permission dialog for runtime permissions
     */
    public static void promptLocationPermission(final Activity act) {
        ActivityCompat.requestPermissions(act, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_FINE_LOCATION);
    }
}
