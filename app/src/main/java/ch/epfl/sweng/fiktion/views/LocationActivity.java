package ch.epfl.sweng.fiktion.views;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import ch.epfl.sweng.fiktion.R;
import ch.epfl.sweng.fiktion.android.AndroidPermissions;
import ch.epfl.sweng.fiktion.providers.GoogleMapsLocationProvider;

public class LocationActivity extends AppCompatActivity implements OnMapReadyCallback {

    // Maps and location provider for this activity
    GoogleMapsLocationProvider gmaps = new GoogleMapsLocationProvider();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        // setup google maps
        gmaps.mapReady(this, googleMap);
    }
}
