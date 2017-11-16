package ch.epfl.sweng.fiktion.views;

import android.os.Bundle;

import ch.epfl.sweng.fiktion.R;
import ch.epfl.sweng.fiktion.views.parents.MenuDrawerActivity;

/**
 * Profile activity class
 */
public class ProfileActivity extends MenuDrawerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        includeLayout = R.layout.activity_profile;
        super.onCreate(savedInstanceState);
    }
}
