package ch.epfl.sweng.fiktion;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

import static android.content.ContentValues.TAG;

public class AddPOIActivity extends AppCompatActivity {
    // database reference
    final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_poi);
    }

    public void addPOI(View view) {
        // Get the entered text
        final String poiName = ((EditText) findViewById(R.id.poiName)).getText().toString();
        if (poiName.isEmpty()) {
            ((TextView) findViewById(R.id.addConfirm)).setText("Write the name of your Point of interest");
        } else if (poiName.matches(".*[.$#/\\[\\]].*"))
        {
            ((TextView) findViewById(R.id.addConfirm)).setText("Those characters are not accepted: . $ # [ ] /");
        } else {
            // get/create the reference of the point of interest
            final DatabaseReference poiRef = dbRef.child("Points of interest").child(poiName);
            // add only if the reference doesn't exist
            poiRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()) {
                        // display warning message
                        ((TextView) findViewById(R.id.addConfirm)).setText(poiName + " already exists");
                    } else {
                        // display a confirmation message
                        ((TextView) findViewById(R.id.addConfirm)).setText(poiName + " added");
                        GeoFire geo = new GeoFire(dbRef.child("geofire"));

                        // create random position with values between 0 and 1
                        Random rand = new Random();
                        double coord1 = rand.nextDouble();
                        double coord2 = rand.nextDouble();
                        geo.setLocation(poiName, new GeoLocation(coord1, coord2));

                        // create point of interest
                        PointOfInterest poi = new PointOfInterest(poiName, new Position(coord1, coord2));
                        // set value
                        poiRef.setValue(poi);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            // reset the textview
            ((EditText) findViewById(R.id.poiName)).setText("");
        }
    }
}
