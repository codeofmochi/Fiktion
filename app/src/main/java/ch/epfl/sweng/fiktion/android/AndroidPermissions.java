package ch.epfl.sweng.fiktion.android;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;

import ch.epfl.sweng.fiktion.R;

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
        // show explanation
        if (ActivityCompat.shouldShowRequestPermissionRationale(act, Manifest.permission.ACCESS_FINE_LOCATION)) {
            // notify user
            AlertDialog.Builder dialog = new AlertDialog.Builder(act);
            dialog.setMessage(act.getResources().getString(R.string.gps_permission));
            dialog.setPositiveButton(act.getResources().getString(R.string.OK), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    promptLocationPermission(act);
                }
            });
            dialog.setNegativeButton(act.getString(R.string.Cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                }
            });
            dialog.show();
        }
        else {
            // no explanation, request perm
            ActivityCompat.requestPermissions(act, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_FINE_LOCATION);
        }
    }
}
