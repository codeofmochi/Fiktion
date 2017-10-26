package ch.epfl.sweng.fiktion.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import ch.epfl.sweng.fiktion.R;

public class HomeActivity extends MenuDrawerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // give layout to parent class
        includeLayout = R.layout.activity_home;
        super.onCreate(savedInstanceState);
    }

    /**
     * Starts the location activity
     */
    public void startLocationActivity(View view) {
        Intent i = new Intent(this, LocationActivity.class);
        startActivity(i);
    }
}
