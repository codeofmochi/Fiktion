package ch.epfl.sweng.fiktion.providers;

import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.sweng.fiktion.PointOfInterest;
import ch.epfl.sweng.fiktion.Position;

/**
 * Created by pedro on 16/10/17.
 */

public class LocalDatabaseProvider extends DatabaseProvider {
    List<String> poiList = new ArrayList<>();

    public void addPoi(PointOfInterest poi, TextView confirmText) {
        String poiName = poi.name();
        if (poiList.contains(poiName)) {
            confirmText.setText(poiName + " already exists");
        } else {
            poiList.add(poiName);
            confirmText.setText(poiName + " added");
        }
    }

    public void findNearPois(Position pos, int radius, ListView resultsListView, ArrayAdapter<String> adapter) {

    }
}