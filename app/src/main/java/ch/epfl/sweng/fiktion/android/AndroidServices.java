package ch.epfl.sweng.fiktion.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.camera2.CameraManager;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;

import ch.epfl.sweng.fiktion.R;

/**
 * Helper statics for android services enable
 * Created by dialexo on 17.10.17.
 */

public final class AndroidServices {

    public interface OnGPSDisabledCallback {
        void onGPSDisabed();
    }

    // Permissions identifiers
    public static final int MY_PERMISSIONS_CAMERA = 1;

    /**
     * Checks if location is enabled, trigger callback otherwise
     *
     * @param context             from where we check the location
     * @param gpsDisabledCallback what to do if GPS is disabled
     */
    public static void checkLocationEnable(final Context context, final OnGPSDisabledCallback gpsDisabledCallback) {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        if (!gps_enabled && !network_enabled) {
            gpsDisabledCallback.onGPSDisabed();
        }
    }

    /**
     * Prompts enable location dialog if GPS is disabled
     */
    public static void promptLocationEnable(final Context context) {
        checkLocationEnable(context, new OnGPSDisabledCallback() {
            @Override
            public void onGPSDisabed() {
                // notify user
                AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                dialog.setMessage(context.getResources().getString(R.string.gps_network_not_enabled));
                dialog.setPositiveButton(context.getResources().getString(R.string.open_location_settings), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        context.startActivity(myIntent);
                        //get gps
                    }
                });
                dialog.setNegativeButton(context.getString(R.string.Cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) { /* do nothing */ }
                });
                dialog.show();
            }
        });
    }

    public static void promptCameraEnable(final Context context) {
        CameraManager cm = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        boolean camera_enabled = false;

        try {
            camera_enabled = (cm.getCameraIdList().length != 0);
        } catch (Exception ex) {
        }


        if (!camera_enabled) {
            // notify user
            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            dialog.setMessage("Camera is disabled");
            dialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) context, new String[]{android.Manifest.permission.CAMERA}, MY_PERMISSIONS_CAMERA);
                        }
                    }
            );
            dialog.setNegativeButton(context.getString(R.string.Cancel), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub

                }
            });
            dialog.show();
        }
    }
}
