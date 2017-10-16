package ch.epfl.sweng.fiktion.providers;

import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.sweng.fiktion.models.PointOfInterest;
import ch.epfl.sweng.fiktion.models.Position;


/**
 * Local database provider
 *
 * @author pedro
 */
public class LocalDatabaseProvider extends DatabaseProvider {
    List<PointOfInterest> poiList = new ArrayList<>();

    /**
     * {@inheritDoc}
     */
    public void addPoi(PointOfInterest poi, TextView confirmText) {
        if (poiList.contains(poi)) {
            confirmText.setText(poi.name() + " already exists");
        } else {
            poiList.add(poi);
            confirmText.setText(poi.name() + " added");
        }
    }

    /**
     * {@inheritDoc}
     */
    public void findNearPois(Position pos, int radius, ListView resultsListView, ArrayAdapter<String> adapter) {
        for (PointOfInterest poi : poiList) {
            if (dist(pos.latitude(), pos.longitude(), poi.position().latitude(), poi.position().longitude()) <= radius) {
                adapter.add(poi.name());
            }
        }
        resultsListView.setAdapter(adapter);
    }

    /**
     * Returns the distance between two points with their latitude and longitude coordinates
     *
     * @param lat1  latitude of the first position
     * @param long1 longitude of the first position
     * @param lat2  latitude of the second position
     * @param long2 longitude of the second position
     * @return the distance
     */
    private double dist(double lat1, double long1, double lat2, double long2) {
        double theta = long1 - long2;
        double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
        return 111.18957696 * Math.toDegrees(Math.acos(dist));
    }
}