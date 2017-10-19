package ch.epfl.sweng.fiktion.views;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ExpandableListView;
import android.widget.ListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ch.epfl.sweng.fiktion.R;

import static ch.epfl.sweng.fiktion.views.GetLocationFromMapActivity.NEW_POI_LATITUDE;
import static ch.epfl.sweng.fiktion.views.GetLocationFromMapActivity.NEW_POI_LONGITUDE;

public class AddPOIActivity extends AppCompatActivity {

    private ExpandableListView listView;
    private FictionInputExpandableListAdapter fIListAdapter;
    private List<String> listDataHeader;
    private HashMap<String,List<String>> listHash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_poi);

        listView = (ExpandableListView)findViewById(R.id.add_poi_added_fictions);
        initData();
        fIListAdapter = new FictionInputExpandableListAdapter(this,listDataHeader,listHash);
        listView.setAdapter(fIListAdapter);
    }

    private void initData() {
        listDataHeader = new ArrayList<>();
        listHash = new HashMap<>();

        listDataHeader.add("Fictions Added");

        List<String> fictionsAdded = new ArrayList<>();
        fictionsAdded.add("test1");
        fictionsAdded.add("test2");
        fictionsAdded.add("test3");
        fictionsAdded.add("test4");
        fictionsAdded.add("test5");
        fictionsAdded.add("test6");
        fictionsAdded.add("test7");
        fictionsAdded.add("test8");
        fictionsAdded.add("test9");
        fictionsAdded.add("test10");
        fictionsAdded.add("test11");
        fictionsAdded.add("test12");
        fictionsAdded.add("test13");
        fictionsAdded.add("test14");
        fictionsAdded.add("test15");
        fictionsAdded.add("test16");

        listHash.put(listDataHeader.get(0), fictionsAdded);
    }

    public void getLocationFromMap() {
        Intent intent = new Intent(this, GetLocationFromMapActivity.class);
        startActivityForResult(intent, 1);
    }

    // get the coordinates from the child GetLocationFromMapActivity
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                double latitude = data.getDoubleExtra(NEW_POI_LATITUDE, 0);
                double longitude = data.getDoubleExtra(NEW_POI_LONGITUDE, 0);
                // TODO use this values
            }
        }
    }
}
