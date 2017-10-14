package ch.epfl.sweng.fiktion;

import android.content.Context;
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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pedro on 14/10/17.
 */

public class FireDatabase {
    final private static DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
    final private static GeoFire geofire = new GeoFire(dbRef.child("geofire"));

    public static void addPoi(final PointOfInterest poi, final TextView confirmText) {
        final String poiName = poi.name;
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
                    // display a confirmation message
                    confirmText.setText(poiName + " added");
                    Position pos = poi.position;
                    // set values in database
                    geofire.setLocation(poiName, new GeoLocation(pos.latitude, pos.longitude));
                    poiRef.setValue(poi);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                confirmText.setText("failed to add " + poiName);
            }
        });
    }

    public static void findNearPois(Position pos, int radius, final ListView resultsListView, final Context context) {
        // query the points of interests within the radius
        GeoQuery geoQuery = geofire.queryAtLocation(new GeoLocation(pos.latitude, pos.longitude), radius);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            private List<String> ls = new ArrayList<>();

            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                ls.add(key);
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                // Creates a new adapter for the input list into the ListView
                ArrayAdapter adapter = new ArrayAdapter(context, android.R.layout.simple_list_item_1, ls);
                // Sets the Adapter
                resultsListView.setAdapter(adapter);
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }
}
