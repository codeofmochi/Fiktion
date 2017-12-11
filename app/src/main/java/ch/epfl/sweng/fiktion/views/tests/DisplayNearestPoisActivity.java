package ch.epfl.sweng.fiktion.views.tests;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import ch.epfl.sweng.fiktion.R;
import ch.epfl.sweng.fiktion.models.PointOfInterest;
import ch.epfl.sweng.fiktion.models.Position;
import ch.epfl.sweng.fiktion.providers.DatabaseProvider;

public class DisplayNearestPoisActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_nearest_pois);

        // recover the radius
        Intent intent = getIntent();
        int radius = intent.getIntExtra(FindNearestPoisActivity.RADIUS_KEY, 0);
        Position position = new Position(0, 0);
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1);

        // Creates a new ListView and adapter to display the research results
        ListView resultsListView = (ListView) findViewById(R.id.displayResultPois);
        resultsListView.setAdapter(adapter);
        DatabaseProvider.getInstance().findNearPOIs(position, radius, new DatabaseProvider.FindNearPOIsListener() {
            @Override
            public void onNewValue(PointOfInterest poi) {
                // add the poi name to the adapter
                adapter.add(poi.name());
            }

            @Override
            public void onFailure() {

            }
        });
    }
}
