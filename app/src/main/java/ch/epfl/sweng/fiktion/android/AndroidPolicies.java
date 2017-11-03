package ch.epfl.sweng.fiktion.android;

import android.os.StrictMode;

/**
 * Helper statics for Android policies
 * Created by dialexo on 04.11.17.
 */

public class AndroidPolicies {

    /**
     * Allows all operations on main thread
     */
    public static void setAllowAllThreadPolicy() {
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
    }

    /**
     * Unlocks networking on main thread
     */
    public static void setAllowNetworkingOnMainThread() {
        setAllowAllThreadPolicy();
    }
}
