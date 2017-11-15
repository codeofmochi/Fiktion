package ch.epfl.sweng.fiktion.views;

import android.os.Bundle;

import ch.epfl.sweng.fiktion.R;
import ch.epfl.sweng.fiktion.views.parents.MenuDrawerActivity;

public class SettingsActivity extends MenuDrawerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // pass layout to parent for menu
        includeLayout = R.layout.activity_settings;
        super.onCreate(savedInstanceState);
    }
}
