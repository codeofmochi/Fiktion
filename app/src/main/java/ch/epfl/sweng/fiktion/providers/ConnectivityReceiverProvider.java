package ch.epfl.sweng.fiktion.providers;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.net.InetAddress;

/**
 * Created by serdar on 12/8/2017.
 * A class that enables checking internet activity
 */

public class ConnectivityReceiverProvider {

    /**
     * Checks network connectivity
     * @param context state of the activity that calls that service
     * @return false if there is no connected network, true otherwise
     */
    public boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

    /**
     * This method checks internet availability
     * @return true if it successfully connects to "google.com" successfully, false otherwise
     */
    public boolean isInternetAvailable() {
        try {
            InetAddress inetAddress;
            InetAddress ipAddr = InetAddress.getByName("google.com"); //You can replace it with your name
            return !ipAddr.equals("");

        } catch (Exception e) {
            return false;
        }
    }


}
