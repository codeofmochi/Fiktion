package ch.epfl.sweng.fiktion.views;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Random;
import java.util.TreeSet;

import ch.epfl.sweng.fiktion.R;
import ch.epfl.sweng.fiktion.models.PointOfInterest;
import ch.epfl.sweng.fiktion.models.Position;
import ch.epfl.sweng.fiktion.providers.DatabaseProvider;
import ch.epfl.sweng.fiktion.providers.Providers;

public class SimpleAddPOIActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_poi_simple);
    }

    public void addPOI(View view) {
        // Get the entered text
        final String poiName = ((EditText) findViewById(R.id.poiName)).getText().toString();
        final TextView confirmText = (TextView) findViewById(R.id.addConfirmationText);
        if (poiName.isEmpty()) {
            // warning message if no text was entered
            confirmText.setText(R.string.request_poi_name);
        } else if (poiName.matches(".*[.$#/\\[\\]].*")) {
            // warning message if unaccepted characters are present
            confirmText.setText(R.string.invalid_characters);
        } else {
            // Random number generator to get random position values
            Random rand = new Random();
            Position pos = new Position(rand.nextDouble(), rand.nextDouble());
            PointOfInterest poi = new PointOfInterest(poiName, pos, new TreeSet<String>(), "", 0, "", "");
            // ask the database to add the poi
            Providers.database.addPoi(poi, new DatabaseProvider.AddPoiListener() {
                @Override
                @SuppressLint("SetTextI18n") // no reason to set a variable for added
                public void onSuccess() {
                    confirmText.setText(poiName + " added");
                }

                @Override
                @SuppressLint("SetTextI18n") // no reason to set a variable for already exists
                public void onAlreadyExists() {
                    confirmText.setText(poiName + " already exists");
                }

                @Override
                @SuppressLint("SetTextI18n") // no reason to set a variable for failed to add
                public void onFailure() {
                    confirmText.setText("failed to add " + poiName);
                }
            });
            ((EditText) findViewById(R.id.poiName)).setText("");
        }
    }
}
