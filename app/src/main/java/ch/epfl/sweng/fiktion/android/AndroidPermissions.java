package ch.epfl.sweng.fiktion.android;

import android.Manifest;
import android.app.Activity;
import android.support.v4.app.ActivityCompat;

import static ch.epfl.sweng.fiktion.android.AndroidServices.MY_PERMISSIONS_CAMERA;

/**
 * Helper statics for permissions requests
 * Created by dialexo on 17.10.17.
 */

public final class AndroidPermissions {

    // Permissions requests IDs
    public static final int MY_PERMISSIONS_FINE_LOCATION = 1;
    public static final int MY_PERMISSIONS_REQUEST_READ_EXT_STORAGE = 123;


    /**
     * Prompts location permission dialog for runtime permissions
     */
    public static void promptLocationPermission(final Activity act) {
        ActivityCompat.requestPermissions(act, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_FINE_LOCATION);
    }

    public static void promptCameraPermission(final Activity act) {
        ActivityCompat.requestPermissions(act, new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_CAMERA);
    }


}
