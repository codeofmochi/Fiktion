package ch.epfl.sweng.fiktion.views;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Set;
import java.util.TreeSet;

import ch.epfl.sweng.fiktion.R;
import ch.epfl.sweng.fiktion.models.PointOfInterest;
import ch.epfl.sweng.fiktion.models.Position;
import ch.epfl.sweng.fiktion.models.User;
import ch.epfl.sweng.fiktion.models.posts.AddPOIPost;
import ch.epfl.sweng.fiktion.models.posts.Post;
import ch.epfl.sweng.fiktion.providers.AuthProvider;
import ch.epfl.sweng.fiktion.providers.DatabaseProvider;
import ch.epfl.sweng.fiktion.utils.CollectionsUtils;
import ch.epfl.sweng.fiktion.views.parents.MenuDrawerActivity;
import ch.epfl.sweng.fiktion.views.utils.ActivityCodes;
import ch.epfl.sweng.fiktion.views.utils.AuthenticationChecks;

import static ch.epfl.sweng.fiktion.views.GetLocationFromMapActivity.NEW_POI_LATITUDE;
import static ch.epfl.sweng.fiktion.views.GetLocationFromMapActivity.NEW_POI_LONGITUDE;

public class AddPOIActivity extends MenuDrawerActivity {

    // List of the fictions name
    private final Set<String> fictionSet = new TreeSet<>();
    // Displayed fiction list (as a big string)
    private String fictionListText = "";
    // this activity's context
    private Context ctx = this;
    // activities codes
    private final int SIGNIN_REQUEST = 0;


    // this activity can either add or edit POIs
    public enum Action {
        ADD, EDIT
    }

    private Action action;
    private String editName;

    @Override
    @SuppressWarnings("SetTextI18n")
    protected void onCreate(Bundle savedInstanceState) {
        includeLayout = R.layout.activity_add_poi;
        super.onCreate(savedInstanceState);

        // check if user is connected and has a valid account
        AuthenticationChecks.checkVerifieddAuth(this, new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                AuthenticationChecks.goHome((Activity) ctx);
            }
        });
        // check if user's account is verified, otherwise prompt verification and/or refresh
        if (!AuthProvider.getInstance().isEmailVerified()) {
            return;
        }


        // check if it is an edit request
        Intent from = getIntent();
        editName = from.getStringExtra("EDIT_POI_NAME");

        // assume it is an edit if no extra data
        if (editName == null || editName.isEmpty()) {
            action = Action.ADD;
        } else {
            action = Action.EDIT;
        }

        // if the goal is to edit, disable actions until fetched from database
        if (action == Action.EDIT) {

            // modify activity title
            this.setTitle("Edit " + editName);
            // show loading snackbar
            final Snackbar loadingSnackbar = Snackbar.make(findViewById(R.id.add_poi_scroll), R.string.loading_data, Snackbar.LENGTH_INDEFINITE);
            loadingSnackbar.show();
            // disable save button
            final Button saveButton = (Button) findViewById(R.id.add_poi_finish);
            saveButton.setEnabled(false);
            saveButton.setBackgroundColor(getResources().getColor(R.color.lightGray));

            // database request
            DatabaseProvider.getInstance().getPOI(editName, new DatabaseProvider.GetPOIListener() {
                @Override
                public void onNewValue(PointOfInterest poi) {
                    // set fields
                    EditText addPoiName = (EditText) findViewById(R.id.add_poi_name);
                    addPoiName.setText(poi.name());
                    addPoiName.setEnabled(false);
                    fictionSet.addAll(poi.fictions());
                    fictionListText = CollectionsUtils.mkString(poi.fictions(), ", ");
                    ((TextView) findViewById(R.id.add_poi_fiction_list)).setText(fictionListText);
                    ((EditText) findViewById(R.id.add_poi_latitude)).setText(Double.toString(poi.position().latitude()));
                    ((EditText) findViewById(R.id.add_poi_longitude)).setText(Double.toString(poi.position().longitude()));
                    ((EditText) findViewById(R.id.add_poi_city)).setText(poi.city());
                    ((EditText) findViewById(R.id.add_poi_country)).setText(poi.country());
                    ((EditText) findViewById(R.id.add_poi_description)).setText(poi.description());
                    // hide loading snackbar
                    loadingSnackbar.dismiss();
                    // enable save button
                    saveButton.setEnabled(true);
                    saveButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                }

                @Override
                public void onModifiedValue(PointOfInterest poi) {
                    // do nothing : we don't want the change to cancel the current edit
                }

                @Override
                public void onDoesntExist() {
                    loadingSnackbar.setText(R.string.data_not_found);
                }

                @Override
                public void onFailure() {
                    loadingSnackbar.setText(R.string.failed_to_fetch_data);
                }
            });
        }
    }

    @Override
    public void onStart() {
        super.onStart();
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
        switch (requestCode) {
            case ActivityCodes.LOCATION_RESULT:
                if (resultCode == RESULT_OK) {
                    Double latitude = data.getDoubleExtra(NEW_POI_LATITUDE, 0);
                    Double longitude = data.getDoubleExtra(NEW_POI_LONGITUDE, 0);

                    ((EditText) findViewById(R.id.add_poi_latitude)).setText(latitude.toString());
                    ((EditText) findViewById(R.id.add_poi_longitude)).setText(longitude.toString());
                }
                break;
            case ActivityCodes.SIGNIN_REQUEST:
                if (resultCode == RESULT_OK) {
                    this.recreate();
                }
                break;

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
        startActivityForResult(i, ActivityCodes.LOCATION_RESULT);
    }

    /**
     * Triggered when the "From wiki linK" button is clicked
     * Launches a wikipedia link retriever activity to get coordinates
     *
     * @param view caller view
     */
    public void startGetLocationFromWikipedia(View view) {
        Intent i = new Intent(this, GetLocationFromWikipediaActivity.class);
        startActivityForResult(i, ActivityCodes.LOCATION_RESULT);
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
            ((EditText) findViewById(R.id.add_poi_fiction)).setError("Fiction name cannot be empty");
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
            } else {
                showToast(fiction + " already added");
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
            ((EditText) findViewById(R.id.add_poi_name)).setError("Name cannot be empty");
            isCorrect = false;
        }

        if (city.isEmpty()) {
            ((EditText) findViewById(R.id.add_poi_city)).setError("City cannot be empty");
            isCorrect = false;
        }

        if (country.isEmpty()) {
            ((EditText) findViewById(R.id.add_poi_country)).setError("Country cannot be empty");
            isCorrect = false;
        }

        if (longitudeString.isEmpty()) {
            ((EditText) findViewById(R.id.add_poi_longitude)).setError("Longitude cannot be empty");
            isCorrect = false;
        } else if (!isNumeric(longitudeString)) {
            ((EditText) findViewById(R.id.add_poi_longitude)).setError("Please provide a valid number");
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
            ((EditText) findViewById(R.id.add_poi_latitude)).setError("Latitude cannot be empty");
            isCorrect = false;
        } else if (!isNumeric(latitudeString)) {
            ((EditText) findViewById(R.id.add_poi_latitude)).setError("Please provide a valid number");
            isCorrect = false;
        } else {

            latitude = Double.parseDouble(latitudeString);
            if (latitude < -90 || latitude > 90) {
                ((EditText) findViewById(R.id.add_poi_latitude)).setError("The latitude must be in range [-90;90]");
                isCorrect = false;
            }
        }

        if (fictionSet.isEmpty()) {
            ((EditText) findViewById(R.id.add_poi_fiction)).setError("Please add at least one fiction");
            isCorrect = false;
        }

        if (isCorrect) {
            // create new poi object with current data
            final PointOfInterest newPoi = new PointOfInterest(name, new Position(latitude, longitude), fictionSet, description, 0, country, city);

            switch (action) {
                case ADD: {
                    DatabaseProvider.getInstance().addPOI(newPoi, new DatabaseProvider.AddPOIListener() {
                        @Override
                        public void onSuccess() {
                            showToast("The place " + name + " was successfully added");
                            // show newly created POI
                            Intent i = new Intent(ctx, POIPageActivity.class);
                            i.putExtra("POI_NAME", name);
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i);

                            // get the user id to add a post of the poi addition
                            AuthProvider.getInstance().getCurrentUser(new DatabaseProvider.GetUserListener() {
                                @Override
                                public void onNewValue(User user) {
                                    try {
                                        Post post = new AddPOIPost(newPoi.name(), Calendar.getInstance().getTime());
                                        DatabaseProvider.getInstance().addUserPost(user.getID(), post, new DatabaseProvider.AddPostListener() {
                                            @Override
                                            public void onSuccess() {
                                            }

                                            @Override
                                            public void onFailure() {
                                            }
                                        });
                                    } catch (NoSuchAlgorithmException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onModifiedValue(User user) {
                                }

                                @Override
                                public void onDoesntExist() {
                                }

                                @Override
                                public void onFailure() {
                                }
                            });
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
                    break;
                }
                case EDIT: {
                    DatabaseProvider.getInstance().modifyPOI(newPoi, new DatabaseProvider.ModifyPOIListener() {
                        @Override
                        public void onSuccess() {
                            showToast("The place " + editName + " was modified");
                            // show newly created POI
                            Intent i = new Intent(ctx, POIPageActivity.class);
                            i.putExtra("POI_NAME", editName);
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i);
                        }

                        @Override
                        public void onDoesntExist() {
                            showToast("The place " + editName + " was not found");
                        }

                        @Override
                        public void onFailure() {
                            showToast("An error occured while modifying " + editName + " : please try again later");
                        }
                    });
                    break;
                }
                default: {
                    throw new IllegalStateException("Undefined action");
                }
            }
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

    /**
     * Get the current state of the activity
     *
     * @return the state of the activity
     */
    public Action getState() {
        return this.action;
    }
}
