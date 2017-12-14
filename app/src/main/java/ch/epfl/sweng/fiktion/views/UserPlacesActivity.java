package ch.epfl.sweng.fiktion.views;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;

import ch.epfl.sweng.fiktion.R;
import ch.epfl.sweng.fiktion.models.PointOfInterest;
import ch.epfl.sweng.fiktion.models.User;
import ch.epfl.sweng.fiktion.providers.DatabaseProvider;
import ch.epfl.sweng.fiktion.views.utils.POIDisplayer;

public class UserPlacesActivity extends AppCompatActivity {

    // possible states
    public enum Action {
        VISITED,
        WISHLIST,
        FAVORITES
    }

    private Action state = Action.VISITED;
    private String profileType;
    // linear layout display
    private LinearLayout poiList;
    // empty text
    private TextView empty;
    // title text
    private TextView title;
    // user id
    private String userId;
    // user to display
    private User user;
    // context
    private Context ctx = this;

    /**
     * Triggered by bottom navigation
     */
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.tab_visited:
                    if (state != Action.VISITED) {
                        updateContent();
                        state = Action.VISITED;
                    }
                    return true;
                case R.id.tab_wishlist:
                    if (state != Action.WISHLIST) {
                        updateContent();
                        state = Action.WISHLIST;
                    }
                    return true;
                case R.id.tab_favorites:
                    if (state != Action.FAVORITES) {
                        updateContent();
                        state = Action.FAVORITES;
                    }
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_places);

        // find poi list
        poiList = (LinearLayout) findViewById(R.id.poiList);
        empty = (TextView) findViewById(R.id.empty);
        title = (TextView) findViewById(R.id.title);

        // setup bottom navigation bar
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // get user id
        Intent from = getIntent();
        userId = from.getStringExtra(ProfileActivity.USER_ID_KEY);
        profileType = from.getStringExtra(ProfileActivity.PROFILE_ACTION_KEY);
        if (profileType == null) profileType = ProfileActivity.PROFILE_ACTION_ANOTHER;

        // get first state
        updateContent();
    }

    /**
     * Updates activity's content given the current state
     */
    private void updateContent() {
        if (userId == null) return;

        // remove any child
        poiList.removeAllViews();
        // display title
        poiList.addView(title);
        // display empty message
        poiList.addView(empty);
        empty.setVisibility(View.VISIBLE);

        // fetch from database
        DatabaseProvider.getInstance().getUserById(userId, new DatabaseProvider.GetUserListener() {
            @Override
            public void onNewValue(User u) {
                user = u;

                // define action depending on state
                Collection<String> collection = new ArrayList<>();
                switch (state) {
                    case VISITED:
                        collection = user.getVisited();
                        title.setText(getString(R.string.places_visited_by, u.getName()));
                        break;
                    case WISHLIST:
                        collection = user.getWishlist();
                        title.setText(getString(R.string.places_in_wishlist, u.getName()));
                        break;
                    case FAVORITES:
                        collection = user.getFavourites();
                        title.setText(getString(R.string.places_in_favorites, u.getName()));
                        break;
                }

                // fetch POIs from db
                for (String poiId : collection) {
                    DatabaseProvider.getInstance().getPOI(poiId, new DatabaseProvider.GetPOIListener() {
                        @Override
                        public void onNewValue(final PointOfInterest poi) {
                            // display card of POI
                            View v = POIDisplayer.createPoiCard(poi, ctx);

                            if (profileType.equals(ProfileActivity.PROFILE_ACTION_ME)) {
                                addPOICardTouchActions(v, poi);
                            }

                            poiList.addView(v);
                            // hide empty message
                            empty.setVisibility(View.GONE);
                        }

                        @Override
                        public void onModifiedValue(PointOfInterest poi) { /*nothing */ }

                        @Override
                        public void onDoesntExist() { /*nothing */ }

                        @Override
                        public void onFailure() { /*nothing */ }
                    });
                }
            }

            @Override
            public void onModifiedValue(User u) { /* nothing */ }

            @Override
            public void onDoesntExist() {
                Snackbar.make(poiList, "User does not exist", Snackbar.LENGTH_INDEFINITE).show();
            }

            @Override
            public void onFailure() {
                Snackbar.make(poiList, "Error loading user", Snackbar.LENGTH_INDEFINITE).show();
            }
        });
    }


    private void addPOICardTouchActions(View v, final PointOfInterest poi) {
        // define all events that happen when modifying the user
        final DatabaseProvider.ModifyUserListener listener = new DatabaseProvider.ModifyUserListener() {
            @Override
            public void onSuccess() {
                Snackbar.make(poiList, R.string.place_removed, Snackbar.LENGTH_SHORT).show();
                updateContent();
            }

            @Override
            public void onDoesntExist() {
                Snackbar.make(poiList, R.string.data_not_found, Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onFailure() {
                Snackbar.make(poiList, R.string.request_failed, Snackbar.LENGTH_LONG).show();
            }
        };

        // background change
        v.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == android.view.MotionEvent.ACTION_DOWN)
                    v.setBackgroundColor(getResources().getColor(R.color.bgLightGray));
                else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL)
                    v.setBackgroundColor(getResources().getColor(R.color.white));
                // not consumed
                return false;
            }
        });

        // add deletion
        v.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                builder.setMessage(getString(R.string.deletion_confirmation_of, poi.name()))
                        .setCancelable(true)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                switch (state) {
                                    case WISHLIST:
                                        user.removeFromWishlist(poi.name(), listener);
                                        break;
                                    case FAVORITES:
                                        user.removeFavourite(poi.name(), listener);
                                        break;
                                    case VISITED:
                                        user.removeFromVisited(poi.name(), listener);
                                        break;
                                }
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert = builder.create();
                alert.show();

                // not consumed
                return false;
            }
        });
    }
}
