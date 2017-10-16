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

public class LocalDatabaseProvider implements DatabaseProvider {
    List<PointOfInterest> poiList = new ArrayList<>();

    public void addPoi(PointOfInterest poi, TextView confirmText) {
        if (poiList.contains(poi)) {
            confirmText.setText(poi.name() + " already exists");
        } else {
            poiList.add(poi);
            confirmText.setText(poi.name() + " added");
        }
    }

    public void findNearPois(Position pos, int radius, ListView resultsListView, ArrayAdapter<String> adapter) {
        for (PointOfInterest poi : poiList) {
            if (dist(pos.latitude(), pos.longitude(), poi.position().latitude(), poi.position().longitude()) <= radius) {
                adapter.add(poi.name());
            }
        }
        resultsListView.setAdapter(adapter);
    }

    private double dist(double lat1, double long1, double lat2, double long2) {
        double theta = long1 - long2;
        double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
        return 111.18957696 * Math.toDegrees(Math.acos(dist));
    }
}