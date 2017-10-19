package ch.epfl.sweng.fiktion.views;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.InterpolatorRes;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ch.epfl.sweng.fiktion.R;
import ch.epfl.sweng.fiktion.models.PointOfInterest;
import ch.epfl.sweng.fiktion.models.Position;
import ch.epfl.sweng.fiktion.providers.DatabaseProvider;

import static ch.epfl.sweng.fiktion.providers.Providers.database;
import static ch.epfl.sweng.fiktion.views.GetLocationFromMapActivity.NEW_POI_LATITUDE;
import static ch.epfl.sweng.fiktion.views.GetLocationFromMapActivity.NEW_POI_LONGITUDE;

public class AddPOIActivity extends AppCompatActivity {

    // List of the fictions name
    private List<String> fictionList = new ArrayList<>();
    // Displayed fiction list (as a big string)
    private String fictionListText = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_poi);
    }

    // get the coordinates from the child GetLocationFromMapActivity
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                Double latitude = data.getDoubleExtra(NEW_POI_LATITUDE, 0);
                Double longitude = data.getDoubleExtra(NEW_POI_LONGITUDE, 0);

                ((EditText)findViewById(R.id.add_poi_latitude)).setText(latitude.toString());
                ((EditText)findViewById(R.id.add_poi_longitude)).setText(longitude.toString());
            }
        }
    }

    public void startGetLocationFromMapActivity(View view) {
        Intent getLocationFromMapIntent = new Intent(this, GetLocationFromMapActivity.class);
        startActivityForResult(getLocationFromMapIntent, 1);
    }

   // Adds fictions to the fictions list and display them in a view, checks some bad inputs (empty, etc...)
    public void addFiction(View view) {
        final String fiction = ((EditText) findViewById(R.id.add_poi_fiction)).getText().toString();
        if (fiction.isEmpty()) {
            // warning message if no text was entered
            Toast.makeText(this, "You can't enter an empty fiction name", Toast.LENGTH_SHORT).show();
        } else if (fiction.matches(".*[.$#/\\[\\]].*")) {
            // warning message if unaccepted characters are present
            Toast.makeText(this, "Those characters are not accepted: . $ # [ ] /", Toast.LENGTH_SHORT).show();
            ((EditText) findViewById(R.id.add_poi_fiction)).setText("");
        } else {
            fictionList.add(fiction);
            if(fictionListText.isEmpty()) {
                fictionListText = fictionListText.concat(fiction);
            } else {
                fictionListText = fictionListText.concat(", " + fiction);
            }
            ((TextView) findViewById(R.id.add_poi_fiction_list)).setText(fictionListText);
            ((EditText) findViewById(R.id.add_poi_fiction)).setText("");
        }
    }

    public void creatAndSendPoi(View view) {
        final String name = ((EditText) findViewById(R.id.add_poi_name)).getText().toString();
        final String longitudeString = ((EditText) findViewById(R.id.add_poi_longitude)).getText().toString();
        final String latitudeString = ((EditText) findViewById(R.id.add_poi_latitude)).getText().toString();
        double longitude = 0.0;
        double latitude = 0.0;

        if(name.isEmpty()) {
            // warning message if no text was entered
            Toast.makeText(this, "You can't enter an empty fiction name", Toast.LENGTH_SHORT).show();
        } else if (name.matches(".*[.$#/\\[\\]].*")) {
            // warning message if unaccepted characters are present
            Toast.makeText(this, "Those characters are not accepted: . $ # [ ] /", Toast.LENGTH_SHORT).show();
        } else {
            if(isNumeric(longitudeString) && isNumeric(latitudeString)) {
                longitude = Double.parseDouble(longitudeString);
                latitude = Double.parseDouble(latitudeString);

                if(longitude < -180 || longitude > 180 || latitude < -90 | latitude > 90) {
                    Toast.makeText(this, "Please enter coordinates in range -90 to 90 for latitude and -180 to 180 for longitude", Toast.LENGTH_SHORT).show();
                    ((EditText) findViewById(R.id.add_poi_longitude)).setText("");
                    ((EditText) findViewById(R.id.add_poi_latitude)).setText("");
                } else {
                    database.addPoi(new PointOfInterest(name, new Position(latitude, longitude)), new DatabaseProvider.AddPoiListener() {
                        @Override
                        public void onSuccess() {
                            showToast("The Point of Interest " + name + " was added !");
                        }

                        @Override
                        public void onAlreadyExists() {
                            showToast("The Point of Interest " + name + " already exists !");
                        }

                        @Override
                        public void onFailure() {
                            showToast("Failed to add "+ name + " !");
                        }
                    });
                }
            } else {
                Toast.makeText(this, "Please enter valid coordinates", Toast.LENGTH_SHORT).show();
                ((EditText) findViewById(R.id.add_poi_longitude)).setText("");
                ((EditText) findViewById(R.id.add_poi_latitude)).setText("");
            }
        }

    }
    // send a toast with text s
    private void showToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    // check if a String is a number
    private static boolean isNumeric(String str)
    {
        return str.matches("-?\\d+(\\.\\d+)?");
    }
}
