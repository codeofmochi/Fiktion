package ch.epfl.sweng.fiktion;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

/**
 * Created by pedro on 14/10/17.
 */

public class Firebase {
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
                if(dataSnapshot.exists()) {
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
}
