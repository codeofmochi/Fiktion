package ch.epfl.sweng.fiktion.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;
import java.util.TreeSet;

import ch.epfl.sweng.fiktion.R;
import ch.epfl.sweng.fiktion.models.PointOfInterest;
import ch.epfl.sweng.fiktion.models.Position;
import ch.epfl.sweng.fiktion.providers.DatabaseProvider;
import ch.epfl.sweng.fiktion.views.parents.MenuDrawerActivity;

import static ch.epfl.sweng.fiktion.views.GetLocationFromMapActivity.NEW_POI_LATITUDE;
import static ch.epfl.sweng.fiktion.views.GetLocationFromMapActivity.NEW_POI_LONGITUDE;

public class AddPOIActivity extends MenuDrawerActivity {

    // List of the fictions name
    private final Set<String> fictionSet = new TreeSet<>();
    // Displayed fiction list (as a big string)
    private String fictionListText = "";
    // intent result codes
    private static final int LOCATION_RESULT = 1;
    // this activity's context
    private Context ctx = this;
    // error messages
    private final String ERR_STRING_FORMAT = "Those characters are not accepted: . $ # [ ] /";
    private final String ILLEGAL_CHARS_REGEX = ".*[.$#/\\[\\]].*";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        includeLayout = R.layout.activity_add_poi;
        super.onCreate(savedInstanceState);
    }

    /**
     * Triggered when an activity launched from here returns a value
     *
     * @param requestCode The code of the request
     * @param resultCode  The code of the result
     * @param data        the intent from which the result comes from
     */
    @Override
    @SuppressLint("SetTextI18n") // latitude and longitude are inputs, not hardcoded
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LOCATION_RESULT) {
            if (resultCode == RESULT_OK) {
                Double latitude = data.getDoubleExtra(NEW_POI_LATITUDE, 0);
                Double longitude = data.getDoubleExtra(NEW_POI_LONGITUDE, 0);


                ((EditText) findViewById(R.id.add_poi_latitude)).setText(latitude.toString());
                ((EditText) findViewById(R.id.add_poi_longitude)).setText(longitude.toString());
            }
        }
    }

    /**
     * Triggered when the "From Map" button is clicked
     * Launches a map activity to select a location
     *
     * @param view caller view
     */
    public void startGetLocationFromMapActivity(View view) {
        Intent i = new Intent(this, GetLocationFromMapActivity.class);
        startActivityForResult(i, LOCATION_RESULT);
    }

    /**
     * Triggered when the "From wiki linK" button is clicked
     * Launches a wikipedia link retriever activity to get coordinates
     *
     * @param view caller view
     */
    public void startGetLocationFromWikipedia(View view) {
        Intent i = new Intent(this, GetLocationFromWikipediaActivity.class);
        startActivityForResult(i, LOCATION_RESULT);
    }

    /**
     * Triggered when the "Add" fiction button is clicked
     * Adds fictions to the fictions list and display them in a view, checks some bad inputs (empty, etc...)
     *
     * @param view caller view
     */
    public void addFiction(View view) {
        final String fiction = ((EditText) findViewById(R.id.add_poi_fiction)).getText().toString();
        if (fiction.isEmpty()) {
            // warning message if no text was entered
            ((EditText) findViewById(R.id.add_poi_fiction)).setError("You can't enter an empty fiction name");
        } else if (fiction.matches(ILLEGAL_CHARS_REGEX)) {
            // warning message if unaccepted characters are present
            ((EditText) findViewById(R.id.add_poi_fiction)).setError(ERR_STRING_FORMAT);
        } else {
            if (!fictionSet.contains(fiction)) {
                fictionSet.add(fiction);
                if (fictionListText.isEmpty()) {
                    fictionListText = fictionListText.concat(fiction);
                } else {
                    fictionListText = fictionListText.concat(", " + fiction);
                }
                ((TextView) findViewById(R.id.add_poi_fiction_list)).setText(fictionListText);
                ((EditText) findViewById(R.id.add_poi_fiction)).setText("");
            }
        }
    }

    /**
     * Triggered by the "save this place" button
     * Gather all data in fields to create and send a correct POI to the database
     *
     * @param view the caller view
     */
    public void createAndSendPoi(View view) {
        final String name = ((EditText) findViewById(R.id.add_poi_name)).getText().toString();
        final String longitudeString = ((EditText) findViewById(R.id.add_poi_longitude)).getText().toString();
        final String latitudeString = ((EditText) findViewById(R.id.add_poi_latitude)).getText().toString();
        final String description = ((EditText) findViewById(R.id.add_poi_description)).getText().toString();
        final String city = ((EditText) findViewById(R.id.add_poi_city)).getText().toString();
        final String country = ((EditText) findViewById(R.id.add_poi_country)).getText().toString();
        double longitude = 0.0;
        double latitude = 0.0;

        boolean isCorrect = true;

        if (name.isEmpty()) {
            ((EditText) findViewById(R.id.add_poi_name)).setError("You can't enter an empty point of interest name");
            isCorrect = false;
        }
        if (name.matches(ILLEGAL_CHARS_REGEX)) {
            ((EditText) findViewById(R.id.add_poi_name)).setError(ERR_STRING_FORMAT);
            isCorrect = false;
        }

        if (city.isEmpty()) {
            ((EditText) findViewById(R.id.add_poi_city)).setError("City cannot be empty");
            isCorrect = false;
        }
        if (city.matches(ILLEGAL_CHARS_REGEX)) {
            ((EditText) findViewById(R.id.add_poi_city)).setError(ERR_STRING_FORMAT);
            isCorrect = false;
        }

        if (country.isEmpty()) {
            ((EditText) findViewById(R.id.add_poi_country)).setError("Country cannot be empty");
            isCorrect = false;
        }
        if (country.matches(ILLEGAL_CHARS_REGEX)) {
            ((EditText) findViewById(R.id.add_poi_country)).setError(ERR_STRING_FORMAT);
            isCorrect = false;
        }

        if (longitudeString.isEmpty()) {
            ((EditText) findViewById(R.id.add_poi_longitude)).setError("You can't enter an empty longitude");
            isCorrect = false;
        } else if (!isNumeric(longitudeString)) {
            ((EditText) findViewById(R.id.add_poi_longitude)).setError("You need to enter a number");
            isCorrect = false;
        } else {
            // If longitude is a number, parse it to double
            longitude = Double.parseDouble(longitudeString);

            if (longitude < -180 || longitude > 180) {
                ((EditText) findViewById(R.id.add_poi_longitude)).setError("The longitude must be in range [-180;180]");
                isCorrect = false;
            }
        }

        if (latitudeString.isEmpty()) {
            ((EditText) findViewById(R.id.add_poi_latitude)).setError("You can't enter an empty latitude");
            isCorrect = false;
        } else if (!isNumeric(latitudeString)) {
            ((EditText) findViewById(R.id.add_poi_latitude)).setError("You need to enter a number");
            isCorrect = false;
        } else {

            latitude = Double.parseDouble(latitudeString);
            if (latitude < -90 || latitude > 90) {
                ((EditText) findViewById(R.id.add_poi_latitude)).setError("The latitude must be in range [-90;90]");
                isCorrect = false;
            }
        }
        if (isCorrect) {
            PointOfInterest newPoi = new PointOfInterest(name, new Position(latitude, longitude), fictionSet, description, 0, country, city);
            DatabaseProvider.getInstance().addPoi(newPoi, new DatabaseProvider.AddPoiListener() {
                @Override
                public void onSuccess() {
                    showToast("The place " + name + " was successfully added");
                    // show newly created POI
                    Intent i = new Intent(ctx, POIPageActivity.class);
                    i.putExtra("POI_NAME", name);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }

                @Override
                public void onAlreadyExists() {
                    showToast("The place named " + name + " already exists !");
                }

                @Override
                public void onFailure() {
                    showToast("An error occured while adding " + name + " : please try again later");
                }
            });
        }
    }

    // send a toast with text s
    private void showToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    // check if a String is a number
    @SuppressWarnings("BooleanMethodIsAlwaysInverted") // Leaved as isNumeric to keep semantic
    private static boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");
    }
}
