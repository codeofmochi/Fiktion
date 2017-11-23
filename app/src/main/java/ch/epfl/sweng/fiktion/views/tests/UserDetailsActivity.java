package ch.epfl.sweng.fiktion.views.tests;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.TreeSet;

import ch.epfl.sweng.fiktion.R;
import ch.epfl.sweng.fiktion.models.PointOfInterest;
import ch.epfl.sweng.fiktion.models.User;
import ch.epfl.sweng.fiktion.providers.AuthProvider;
import ch.epfl.sweng.fiktion.providers.DatabaseProvider;
import ch.epfl.sweng.fiktion.views.HomeActivity;
import ch.epfl.sweng.fiktion.views.SettingsActivity;
import ch.epfl.sweng.fiktion.views.SignInActivity;

/**
 * This activity displays the user's information and allows him to apply changes to its profile
 *
 * @author Rodrigo
 */
public class UserDetailsActivity extends AppCompatActivity {

    private DatabaseProvider database = DatabaseProvider.getInstance();

    //constants
    //LOGCAT
    private static final String TAG = "UserDetails";

    //UI modes
    private enum UIMode {
        defaultMode,
        userSignedOut,
        poiDoesNotExist,
        databasefailure,
        failure
    }

    private User currUser;
    //Authenticator initiation

    private final AuthProvider auth = AuthProvider.getInstance();

    //views
    private TextView user_name_view;
    private TextView user_email_view;
    private TextView user_verify_view;

    private EditText new_favourite_input;
    private EditText new_wish_input;

    //Buttons
    //Strings
    private String email;
    private String name;

    //lists information
    private TreeSet<String> favourites;
    private TreeSet<String> wishlist;

    ArrayAdapter<String> favAdapter;
    ArrayAdapter<String> wishAdapter;

    // Creates a new ListView and adapter to display the research results
    ListView favouritesListView;
    ListView wishListView;


    private void setUser(User user) {
        currUser = user;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        Log.d(TAG, "Initialising User Details activity");

        //initialise views
        user_name_view = (TextView) findViewById(R.id.detail_user_name);
        user_email_view = (TextView) findViewById(R.id.detail_user_email);
        user_verify_view = (TextView) findViewById(R.id.detail_user_verify);
        new_favourite_input = (EditText) findViewById(R.id.user_detail_addFav_text);
        new_wish_input = (EditText) findViewById(R.id.user_details_addWish_text);


    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "Started UserDetailsActivity");

        //initalize lists

        favAdapter = new ArrayAdapter<>(this.getApplicationContext(), android.R.layout.simple_list_item_1);
        wishAdapter = new ArrayAdapter<>(this.getApplicationContext(), android.R.layout.simple_list_item_1);

        favouritesListView = (ListView) findViewById(R.id.user_details_favourites_list);
        wishListView = (ListView) findViewById(R.id.user_details_wishlist_list);
        favouritesListView.setAdapter(favAdapter);
        wishListView.setAdapter(wishAdapter);
        //initialise user details

        if (auth.isConnected()) {
            Log.d(TAG, "Request for the currently  signed in user signed in");
            // Name, email address, and profile photo Url
            auth.getCurrentUser(new DatabaseProvider.GetUserListener() {
                @Override
                public void onSuccess(User currUser) {
                    name = currUser.getName();
                    email = auth.getEmail();
                    if (auth.isEmailVerified()) {
                        user_verify_view.setText(R.string.email_is_verified);
                    } else {
                        user_verify_view.setText(R.string.email_is_not_verified);
                    }
                    favourites = new TreeSet<>(currUser.getFavourites());
                    wishlist = new TreeSet<>(currUser.getWishlist());
                    updateUI(UIMode.defaultMode);
                    setUser(currUser);
                }

                @Override
                public void onDoesntExist() {
                }

                @Override
                public void onFailure() {
                }
            });


        } else {

            //this case will probably never happen
            Log.d(TAG, "Could not initialise user details, user is not signed in");
            Toast.makeText(this, "User signed out unexpectedly", Toast.LENGTH_SHORT).show();
            Intent homeActivity = new Intent(this, HomeActivity.class);
            startActivity(homeActivity);
            finish();
        }
    }

    /**
     * This method signs the user out from Fiktion
     */
    private void signOut() {
        auth.signOut();
        updateUI(UIMode.userSignedOut);
        Log.d(TAG, "User is signed out");
    }


    /**
     * this method will set the UI according to the mode it is.
     * It will prompt a sign in if the user is not currently signed in
     * It will display user's informations if he is signed in
     *
     * @param mode UIMode that we want to set for the UI
     */
    private void updateUI(UIMode mode) {
        if (mode.equals(UIMode.defaultMode)) {
            //UI default mode
            //initialise views and buttons
            user_name_view.setText(name);
            user_email_view.setText(email);
            for (String fav : favourites) {
                favAdapter.add(fav);
            }
            for (String wish : wishlist) {
                wishAdapter.add(wish);
            }
        } else if (mode.equals(UIMode.userSignedOut)) {
            Log.d(TAG, "Return to signIn activity");
            Intent login = new Intent(this, SignInActivity.class);
            finish();
            startActivity(login);
        } else if (mode.equals(UIMode.databasefailure)) {
            Toast.makeText(this, "Database failed to load data", Toast.LENGTH_SHORT).show();
        } else if (mode.equals(UIMode.poiDoesNotExist)) {
            Toast.makeText(this, "Position of Interest does not exist", Toast.LENGTH_SHORT).show();
        } else if (mode.equals((UIMode.failure))) {
            Toast.makeText(this, "Failed to perform given operation", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * Starts the sign out request
     */
    public void clickSignOut(@SuppressWarnings("UnusedParameters") View v) {
        Log.d(TAG, "Signing Out");
        signOut();
    }

    /**
     * Starts and activity where the user can edit his profile
     */
    public void clickEditAccount(@SuppressWarnings("UnusedParameters") View v) {
        Log.d(TAG, "Start Edit Account Activity");
        Intent editAccountActivity = new Intent(this, SettingsActivity.class);
        startActivity(editAccountActivity);
    }

    /**
     * Add new favourite point of interest to the current user profile
     */
    public void clickAddFavourite(@SuppressWarnings("UnusedParameters") View v) {
        final String poiName = new_favourite_input.getText().toString();
        if(!poiName.isEmpty() ) {
            database.getPoi(poiName, new DatabaseProvider.GetPoiListener() {
                @Override
                public void onSuccess(final PointOfInterest poi) {

                    currUser.addFavourite(poiName, new AuthProvider.AuthListener() {
                        @Override
                        public void onSuccess() {
                            favAdapter.add(poi.name());
                            recreate();
                        }

                        @Override
                        public void onFailure() {
                            updateUI(UIMode.failure);
                        }
                    });
                }

                @Override
                public void onModified(PointOfInterest poi) {
                }

                @Override
                public void onDoesntExist() {
                    updateUI(UIMode.poiDoesNotExist);
                }

                @Override
                public void onFailure() {
                    updateUI(UIMode.databasefailure);
                }
            });
        } else {
            Toast.makeText(this, "Please type a known position of interest", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Add new point of interest to the wishlist of the current user profile
     */
    public void clickAddToWishlist(@SuppressWarnings("UnusedParameters") View v) {
        final String poiName = new_wish_input.getText().toString();
        if(!poiName.isEmpty()) {
            database.getPoi(poiName, new DatabaseProvider.GetPoiListener() {
                @Override
                public void onSuccess(final PointOfInterest poi) {

                    currUser.addToWishlist(poiName, new AuthProvider.AuthListener() {
                        @Override
                        public void onSuccess() {
                            wishAdapter.add(poi.name());
                            recreate();
                        }

                        @Override
                        public void onFailure() {
                            updateUI(UIMode.failure);
                        }
                    });
                }

                @Override
                public void onModified(PointOfInterest poi) {
                }

                @Override
                public void onDoesntExist() {
                    updateUI(UIMode.poiDoesNotExist);
                }

                @Override
                public void onFailure() {
                    updateUI(UIMode.databasefailure);
                }
            });
        }else{
            Toast.makeText(this, "Please type a known position of interest", Toast.LENGTH_SHORT).show();
        }
    }
}
