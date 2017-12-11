package ch.epfl.sweng.fiktion.providers;

import java.util.List;

import ch.epfl.sweng.fiktion.listeners.Failure;
import ch.epfl.sweng.fiktion.models.PointOfInterest;

/**
 * Created by serdar on 10/27/2017.
 * It is an abstract class on which Algolia's search
 * methodology will be built on. The class forms the
 * basis for searching points of interest.
 */

public abstract class SearchProvider {

    /**
     * Listener that listens the results of searching by text from user
     */
    public interface SearchPOIsByTextListener extends Failure{

        /**
         * what to do if the retrieval succeeds
         *
         * @param poiIDs the retrieved points of interest
         */
        void onSuccess(List<String> poiIDs);
    }

    /**
     * Adds a PointOfInterest object to searchable list
     *
     * @param poi      POI to add
     * @param listener listens the add result
     */
    public abstract void addPOI(PointOfInterest poi, DatabaseProvider.AddPOIListener listener);

    /**
     * Modify a point of interest and inform the listener of the result
     *
     * @param poi      the modified point of interest
     * @param listener the listener
     */
    public abstract void modifyPOI(PointOfInterest poi, DatabaseProvider.ModifyPOIListener listener);

    /**
     * @param text     query POI's that contains parameter in their names
     * @param listener listens to search process
     */
    public abstract void searchByText(String text, SearchPOIsByTextListener listener);

}
