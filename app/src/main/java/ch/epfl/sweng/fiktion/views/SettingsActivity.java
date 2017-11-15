package ch.epfl.sweng.fiktion.views;

import android.os.Bundle;

import ch.epfl.sweng.fiktion.R;

public class SettingsActivity extends MenuDrawerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }
}
