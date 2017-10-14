package ch.epfl.sweng.fiktion;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

public class DisplayNearestPoisActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_nearest_pois);

        // recover the radius
        Intent intent = getIntent();
        int radius = intent.getIntExtra(FindNearestPoisActivity.RADIUS_KEY, 0);

        // Creates a new ListView to display the research results
        ListView resultsListView = (ListView) findViewById(R.id.displayResultPois);
        Position position = new Position(0, 0);
        FireDatabase.findNearPois(position, radius, resultsListView, getApplicationContext());

    }
}
