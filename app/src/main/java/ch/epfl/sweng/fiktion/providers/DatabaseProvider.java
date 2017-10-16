package ch.epfl.sweng.fiktion.providers;

import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import ch.epfl.sweng.fiktion.PointOfInterest;
import ch.epfl.sweng.fiktion.Position;

/**
 * Created by pedro on 15/10/17.
 */

public interface DatabaseProvider {
    public abstract void addPoi(final PointOfInterest poi, final TextView confirmText);

    public abstract void findNearPois(Position pos, int radius, final ListView resultsListView, final ArrayAdapter<String> adapter);
}
