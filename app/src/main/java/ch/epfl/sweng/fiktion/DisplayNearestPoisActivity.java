package ch.epfl.sweng.fiktion;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import ch.epfl.sweng.fiktion.models.Position;

public class DisplayNearestPoisActivity extends DatabaseActivity {

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
        ArrayAdapter<String> adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1);
        database.findNearPois(position, radius, resultsListView, adapter);
    }
}
