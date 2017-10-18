package ch.epfl.sweng.fiktion.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import ch.epfl.sweng.fiktion.R;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /* Starts the map location activity */
    public void startNearbyActivity(View view) {
        Intent mapIntent = new Intent(this, LocationActivity.class);
        startActivity(mapIntent);
    }

    public void startAddPOIActivity(View view) {
        Intent addPoiIntent = new Intent(this, SimpleAddPOIActivity.class);
        startActivity(addPoiIntent);
    }

    public void startFindNearestPoisActivity(View view) {
        Intent findPoiIntent = new Intent(this, FindNearestPoisActivity.class);
        startActivity(findPoiIntent);
    }

    public void startSignInActivity(View view) {
        Log.d(TAG, "Advancing");
        //we advance to the login activity
        Intent signInActivity = new Intent(this, SignInActivity.class);
        startActivity(signInActivity);
    }

}
