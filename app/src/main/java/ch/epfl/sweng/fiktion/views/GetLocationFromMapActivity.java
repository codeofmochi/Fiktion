package ch.epfl.sweng.fiktion.views;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import ch.epfl.sweng.fiktion.R;
import ch.epfl.sweng.fiktion.android.AndroidPermissions;
import ch.epfl.sweng.fiktion.android.AndroidServices;
import ch.epfl.sweng.fiktion.models.Position;
import ch.epfl.sweng.fiktion.providers.GoogleMapsLocationProvider;

public class GetLocationFromMapActivity extends AppCompatActivity implements OnMapReadyCallback {
    public static final String NEW_POI_LATITUDE = "ch.epfl.sweng.fiktion.GetLocationFromMapActivity.newLatitude";
    public static final String NEW_POI_LONGITUDE = "ch.epfl.sweng.fiktion.GetLocationFromMapActivity.newLongitude";

    // Maps and location provider for this activity
    public static GoogleMapsLocationProvider gmaps = new GoogleMapsLocationProvider();
    Position newPosition;

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
        // setup google maps
        gmaps.mapReady(this, googleMap);

        // add a listener that listens marker placements
        gmaps.addMarkerPlacementListener(new GoogleMapsLocationProvider.MarkerPlacementListener() {
            @Override
            public void onAddedMarker(Position position) {
                // show a confirmation button when a marker is added
                findViewById(R.id.getNewLocationButton).setVisibility(View.VISIBLE);
                // set the coordinates to retrieve
                setNewCoords(position);
            }
        });
    }

    // sets the position that will be retrieve
    private void setNewCoords(Position position) {
        newPosition = position;
    }

    // retrieves the coordonates to the parent activity
    public void retrieveCoordinates(View view) {
        // intent with the latitude and longitude
        Intent retrieveCoordsIntent = new Intent();
        retrieveCoordsIntent.putExtra(NEW_POI_LATITUDE, newPosition.latitude());
        retrieveCoordsIntent.putExtra(NEW_POI_LONGITUDE, newPosition.longitude());
        // send the intent to the parent
        setResult(RESULT_OK, retrieveCoordsIntent);
        // close this activity
        finish();
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
