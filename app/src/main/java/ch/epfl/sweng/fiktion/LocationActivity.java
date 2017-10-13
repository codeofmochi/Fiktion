package ch.epfl.sweng.fiktion;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class LocationActivity extends AppCompatActivity implements OnMapReadyCallback {

    // Google Maps
    private GoogleMap mMap;
    // My location
    private Location myPosition;
    // Marker on Google Maps
    private Marker myPositionMarker;
    // Camera on first location change
    private boolean firstLocationChange = true;

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
        mMap = googleMap;
        // enable zoom controls
        mMap.getUiSettings().setZoomControlsEnabled(true);

        // check permissions
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        // enable my position
        mMap.setMyLocationEnabled(true);

        // listen on location change
        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location arg0) {
                // update my position
                myPosition = arg0;
                // update position marker
                if (firstLocationChange) {
                    // update camera
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(myPosition.getLatitude(), myPosition.getLongitude())));
                    mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
                    firstLocationChange = false;
                }
                else {
                    myPositionMarker.remove();
                }
                myPositionMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(arg0.getLatitude(), arg0.getLongitude())).title("My Position"));
            }
        });
    }
}
