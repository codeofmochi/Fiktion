package ch.epfl.sweng.fiktion.views;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Marker;

import ch.epfl.sweng.fiktion.R;
import ch.epfl.sweng.fiktion.models.PointOfInterest;
import ch.epfl.sweng.fiktion.models.Position;
import ch.epfl.sweng.fiktion.providers.DatabaseProvider;
import ch.epfl.sweng.fiktion.providers.DatabaseSingleton;
import ch.epfl.sweng.fiktion.views.parents.MapLocationActivity;
import ch.epfl.sweng.fiktion.views.utils.POIDisplayer;

public class LocationActivity extends MapLocationActivity {

    // poi cards attributes
    private final Context ctx = this;
    private ConstraintLayout frame;
    private View pv;
    private Position cache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // find frame of activity
        frame = (ConstraintLayout) findViewById(R.id.locationFrame);
    }

    /**
     * Adds the list view button in the action bar
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 0, 0, "List")
                .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (gmaps.hasLocation()) {
                            cache = new Position(gmaps.getLocation().getLatitude(), gmaps.getLocation().getLongitude());
                        }
                        startNearbyListActivity();
                        return true;
                    }
                })
                .setIcon(R.drawable.list_icon_40)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
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
                if (!marker.getTitle().equals("My position")) {
                    // get POI from db
                    DatabaseSingleton.database.getPoi(marker.getTitle(), new DatabaseProvider.GetPoiListener() {
                        @Override
                        public void onSuccess(PointOfInterest poi) {
                            // remove old view if any
                            if (pv != null) frame.removeView(pv);
                            // create POI card
                            pv = POIDisplayer.createPoiCard(poi, ctx);
                            // display POI card in ConstraintLayout
                            frame.addView(pv);
                            ConstraintSet constraints = new ConstraintSet();
                            constraints.clone(frame);
                            constraints.connect(pv.getId(), ConstraintSet.START, frame.getId(), ConstraintSet.START, 15);
                            constraints.connect(pv.getId(), ConstraintSet.BOTTOM, frame.getId(), ConstraintSet.BOTTOM, 15);
                            constraints.connect(pv.getId(), ConstraintSet.END, frame.getId(), ConstraintSet.END, 15);
                            constraints.applyTo(frame);
                        }

                        @Override
                        public void onModified(PointOfInterest poi) {

                        }

                        @Override
                        public void onDoesntExist() {

                        }

                        @Override
                        public void onFailure() {

                        }
                    });
                }
                // Return false to indicate that we have not consumed the event
                return false;
            }
        });

    }

    public void startNearbyListActivity() {
        if (cache == null) {
            Toast.makeText(this, R.string.loading_text, Toast.LENGTH_SHORT).show();
            return;
        }
        Intent i = new Intent(ctx, NearbyListActivity.class);
        i.putExtra("LATITUDE", cache.latitude());
        i.putExtra("LONGITUDE", cache.longitude());
        startActivity(i);
    }
}
