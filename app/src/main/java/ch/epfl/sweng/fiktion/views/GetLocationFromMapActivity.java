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

public class GetLocationFromMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    // Maps and location provider for this activity
    public static GoogleMapsLocationProvider gmaps = new GoogleMapsLocationProvider();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_location_from_map);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapForLocation);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gmaps.mapReady(this, googleMap);
        /*
        gmaps.gmap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            Marker m;
            @Override
            public void onMapClick(LatLng latLng) {
                if (m != null) {
                    m.remove();
                }
                m = gmaps.gmap.addMarker(new MarkerOptions().position(latLng).title("new point of interest"));
            }
        });
        */
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
                return;
            }
        }
    }
}
