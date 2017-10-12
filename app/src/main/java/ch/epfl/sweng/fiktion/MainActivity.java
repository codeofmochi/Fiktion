package ch.epfl.sweng.fiktion;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {
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
}
