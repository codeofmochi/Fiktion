package ch.epfl.sweng.fiktion;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Random;

public class AddPOIActivity extends AppCompatActivity {
    // database reference


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_poi);
    }

    public void addPOI(View view) {
        // Get the entered text
        final String poiName = ((EditText) findViewById(R.id.poiName)).getText().toString();
        TextView confirmText = (TextView) findViewById(R.id.addConfirmationText);
        if (poiName.isEmpty()) {
            // warning message if no text was entered
            confirmText.setText("Please write the name of your Point of interest");
        } else if (poiName.matches(".*[.$#/\\[\\]].*")) {
            // warning message if unaccepted characters are present
            confirmText.setText("Those characters are not accepted: . $ # [ ] /");
        } else {
            // Random number generator to get random position values
            Random rand = new Random();
            Position pos = new Position(rand.nextDouble(), rand.nextDouble());
            PointOfInterest poi = new PointOfInterest(poiName, pos);
            // ask the database to add the poi
            DatabaseProvider db = new FirebaseDatabaseProvider();
            db.addPoi(poi, confirmText);
            ((EditText) findViewById(R.id.poiName)).setText("");
        }
    }
}
