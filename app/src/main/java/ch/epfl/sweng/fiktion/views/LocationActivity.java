package ch.epfl.sweng.fiktion.views;

import android.app.AlertDialog;
import android.os.Bundle;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Marker;

import ch.epfl.sweng.fiktion.R;
import ch.epfl.sweng.fiktion.views.parents.MapLocationActivity;

public class LocationActivity extends MapLocationActivity {

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
        super.onMapReady(googleMap);
        gmaps.showNearPOIs(50);

        /**
         * Shows a summary of the marker if clicked on
         */
        gmaps.addMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker marker) {
                //checks if the marker is a POI marker
                if(!marker.getTitle().equals("My position")){
                    AlertDialog.Builder alertbox = new AlertDialog.Builder(LocationActivity.this);
                    alertbox.setMessage(marker.getTitle() + " was clicked. It's at: "
                            + marker.getPosition().latitude + ", " + marker.getPosition().longitude + ".");

                    alertbox.show();
                }
                // Return false to indicate that we have not consumed the event and that we wish
                // for the default behavior to occur (which is for the camera to move such that the
                // marker is centered and for the marker's info window to open, if it has one).
                return false;
            }
        });

    }


}
