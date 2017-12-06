package ch.epfl.sweng.fiktion.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import ch.epfl.sweng.fiktion.R;
import ch.epfl.sweng.fiktion.views.utils.AuthenticationChecks;

public class UserPlacesActivity extends AppCompatActivity {

    // linear layout display
    private LinearLayout poiList;
    // empty text
    private TextView empty;
    // user id
    private String userId;

    /**
     * Triggered by bottom navigation
     */
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.tab_visited:
                    return true;
                case R.id.tab_wishlist:
                    return true;
                case R.id.tab_favorites:
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

        // setup bottom navigation bar
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // get user id
        Intent from = getIntent();
        userId = from.getStringExtra(ProfileActivity.USER_ID_KEY);
    }

}
