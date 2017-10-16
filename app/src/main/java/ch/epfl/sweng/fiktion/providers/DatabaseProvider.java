package ch.epfl.sweng.fiktion.providers;

import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import ch.epfl.sweng.fiktion.models.PointOfInterest;
import ch.epfl.sweng.fiktion.models.Position;


/**
 * Database provider
 *
 * @author Pedro Da Cunha
 */
public abstract class DatabaseProvider {

    /**
     * add a point of interest to the database, set the the TextView according to if the operation
     * succeeded
     *
     * @param poi         the point of interest
     * @param confirmText the TextView to modify
     */
    public abstract void addPoi(final PointOfInterest poi, final TextView confirmText);

    /**
     * find the points of interest that are within radius range from a position, add the results to
     * the adapter and display it with the ListView
     *
     * @param pos             the position
     * @param radius          the radius
     * @param resultsListView listView that displays the results
     * @param adapter         adapter that holds the list of results
     */
    public abstract void findNearPois(Position pos, int radius, final ListView resultsListView, final ArrayAdapter<String> adapter);
}
