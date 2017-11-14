package ch.epfl.sweng.fiktion.providers;

import java.util.List;

import ch.epfl.sweng.fiktion.models.PointOfInterest;

/**
 * Created by serdar on 10/27/2017.
 * It is an abstract class on which Algolia's search
 * methodology will be built on. The class forms the
 * basis for searching points of interest.
 */

public abstract class SearchProvider {

    /**
     * Adds a PointOfInterest object to searchable list
     * @param pointOfInterest POI to add
     */
    public abstract void addPoi(PointOfInterest pointOfInterest, final DatabaseProvider.AddPoiListener listener);

    /**
     *
     * @param name query POI's that contains parameter in their names
     * @param listener listens to search process
     */
    public abstract void searchByText(String name, final DatabaseProvider.PoiSearchByTextListener listener);

}
