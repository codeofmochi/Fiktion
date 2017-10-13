package ch.epfl.sweng.fiktion;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;

public class DisplayNearestPoisActivity extends AppCompatActivity {

    String[] ls = {"one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten", "eleven", "twelve"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_nearest_pois);

        ListView lvFoundPois = (ListView)findViewById(R.id.displayResultPois);

        ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_expandable_list_item_1, ls);
        lvFoundPois.setAdapter(adapter);
    }
}
