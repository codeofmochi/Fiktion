package ch.epfl.sweng.fiktion.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

import ch.epfl.sweng.fiktion.R;
import ch.epfl.sweng.fiktion.models.Position;
import ch.epfl.sweng.fiktion.providers.GoogleMapsLocationProvider;
import ch.epfl.sweng.fiktion.views.parents.MapLocationActivity;

public class GetLocationFromMapActivity extends MapLocationActivity {
    public static final String NEW_POI_LATITUDE = "ch.epfl.sweng.fiktion.GetLocationFromMapActivity.newLatitude";
    public static final String NEW_POI_LONGITUDE = "ch.epfl.sweng.fiktion.GetLocationFromMapActivity.newLongitude";

    private Position newPosition;

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
        super.onMapReady(googleMap);
        findViewById(R.id.selfLocationButton).setVisibility(View.VISIBLE);
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

    // use own position to retrieve
    public void setOwnPosition(View view) {
        if (gmaps.getLocation() == null) {
            // if the location is not loaded yet, show a loading message
            Toast.makeText(this, "Loading location...", Toast.LENGTH_SHORT).show();
        } else {
            // get the user's position from the map and retrieve it
            newPosition = gmaps.getPosition();
            retrieveCoordinates(null);
        }
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
}
