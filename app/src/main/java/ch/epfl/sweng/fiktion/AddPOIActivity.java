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
        // Get the text from the plain text
        String poiName = ((EditText) findViewById(R.id.poiName)).getText().toString();
        if (poiName.isEmpty()) {
            ((TextView) findViewById(R.id.addConfirm)).setText("Can't add empty Point of interest");
        } else {
            Random rand = new Random();
            Position pos = new Position(rand.nextDouble() * 100, rand.nextDouble() * 100);
            PointOfInterest poi = new PointOfInterest(poiName, pos);
            DatabaseReference db = FirebaseDatabase.getInstance().getReference();
            DatabaseReference poiRef = db.child("Points of interest").child(poi.name);
            poiRef.setValue(poi);
            ((TextView) findViewById(R.id.addConfirm)).setText(poiName + " added");
            ((EditText) findViewById(R.id.poiName)).setText("");
        }
    }
}
