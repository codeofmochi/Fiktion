package ch.epfl.sweng.fiktion;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

import static android.content.ContentValues.TAG;

public class AddPOIActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_poi);
    }

    public void addPOI(View view) {
        // Get the entered text
        final String poiName = ((EditText) findViewById(R.id.poiName)).getText().toString();
        if (poiName.isEmpty()) {
            ((TextView) findViewById(R.id.addConfirm)).setText("Can't add empty Point of interest");
        } else {
            // create random position with values between 0 and 100
            Random rand = new Random();
            Position pos = new Position(rand.nextDouble() * 100, rand.nextDouble() * 100);
            // create the point of interest
            final PointOfInterest poi = new PointOfInterest(poiName, pos);
            // get the database reference
            DatabaseReference db = FirebaseDatabase.getInstance().getReference();
            // get/create the reference of the point of interest
            final DatabaseReference poiRef = db.child("Points of interest").child(poi.name);
            // add only if the reference doesn't exist
            poiRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()) {
                        // display warning message
                        ((TextView) findViewById(R.id.addConfirm)).setText(poiName + " already exists");
                    } else {
                        // set value
                        poiRef.setValue(poi);
                        // display a confirmation message
                        ((TextView) findViewById(R.id.addConfirm)).setText(poiName + " added");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            
            ((EditText) findViewById(R.id.poiName)).setText("");
        }
    }
}
