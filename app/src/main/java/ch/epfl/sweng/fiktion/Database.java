package ch.epfl.sweng.fiktion;

import android.util.Log;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import static android.content.ContentValues.TAG;

/**
 * Created by pedro on 10/10/17.
 */

public class Database {
    private DatabaseReference database = FirebaseDatabase.getInstance().getReference();

    public void addPOI(PointOfInterest newPOI) {
        DatabaseReference poi = database.child("Points of interest").child(newPOI.name);
        poi.setValue(newPOI);
        poi.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                PointOfInterest value = dataSnapshot.getValue(PointOfInterest.class);
                Log.d(TAG, "Point of interest name: " + value.name);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "Failed to read value.", databaseError.toException());
            }
        });
    }
}
