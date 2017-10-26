package ch.epfl.sweng.fiktion.views;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import ch.epfl.sweng.fiktion.R;
import ch.epfl.sweng.fiktion.android.AndroidPermissions;
import ch.epfl.sweng.fiktion.android.AndroidServices;
import ch.epfl.sweng.fiktion.providers.GoogleMapsLocationProvider;

public class MapLocationActivity extends AppCompatActivity implements OnMapReadyCallback {

    // Maps and location provider for this and children activities
    public GoogleMapsLocationProvider gmaps = new GoogleMapsLocationProvider();

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        // setup google maps
        gmaps.mapReady(this, googleMap);
    }

    /**
     * Reset location provider when focus is lost
     */
    @Override
    public void onPause() {
        super.onPause();
        // resetting the provider here ensures that a new location is get on activity resume
        gmaps = new GoogleMapsLocationProvider();
    }

    /**
     * Handle permission request result
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case AndroidPermissions.MY_PERMISSIONS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    AndroidServices.promptLocationEnable(this);
                    // restart gmaps
                    this.recreate();
                } else {
                    // permission denied
                }
            }
        }
    }
}

