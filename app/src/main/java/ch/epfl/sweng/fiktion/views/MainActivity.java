package ch.epfl.sweng.fiktion.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import ch.epfl.sweng.fiktion.R;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void startFindNearestPoisActivity(View view) {
        Intent findPoiIntent = new Intent(this, FindNearestPoisActivity.class);
        startActivity(findPoiIntent);
    }

    public void startSignInActivity(View view) {
        //we advance to the login activity
        Intent signInActivity = new Intent(this, SignInActivity.class);
        startActivity(signInActivity);
    }

    public void startHomeActivity(View view) {
        Intent i = new Intent(this, HomeActivity.class);
        startActivity(i);
    }

}
