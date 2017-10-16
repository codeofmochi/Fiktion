package ch.epfl.sweng.fiktion.providers;

import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ch.epfl.sweng.fiktion.models.PointOfInterest;
import ch.epfl.sweng.fiktion.models.Position;

/**
 * Firebase database provider
 *
 * @author pedro
 */
public class FirebaseDatabaseProvider extends DatabaseProvider {
    private final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
    private final GeoFire geofire = new GeoFire(dbRef.child("geofire"));

    /**
     * {@inheritDoc}
     */
    public void addPoi(final PointOfInterest poi, final TextView confirmText) {
        final String poiName = poi.name();
        // get/create the reference of the point of interest
        final DatabaseReference poiRef = dbRef.child("Points of interest").child(poiName);
        // add only if the reference doesn't exist
        poiRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // display warning message if poi already exsists
                    confirmText.setText(poiName + " already exists");
                } else {
                    // set values in database
                    FirebasePointOfInterest fPoi = new FirebasePointOfInterest(poi);
                    poiRef.setValue(fPoi);
                    Position pos = poi.position();
                    geofire.setLocation(poiName, new GeoLocation(pos.latitude(), pos.longitude()));

                    // display a confirmation message
                    confirmText.setText(poiName + " added");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                confirmText.setText("failed to add " + poiName);
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    public void findNearPois(Position pos, int radius, final ListView resultsListView, final ArrayAdapter<String> adapter) {
        // query the points of interests within the radius
        GeoQuery geoQuery = geofire.queryAtLocation(new GeoLocation(pos.latitude(), pos.longitude()), radius);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                adapter.add(key);
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                if (adapter.isEmpty()) {
                    adapter.add("No results found");
                }
                // Show the results by setting the Adapter
                resultsListView.setAdapter(adapter);
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {
                adapter.clear();
                adapter.add("An error has occurred");
                resultsListView.setAdapter(adapter);
            }
        });
    }
}
