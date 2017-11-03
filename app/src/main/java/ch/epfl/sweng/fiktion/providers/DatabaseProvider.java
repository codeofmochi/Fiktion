package ch.epfl.sweng.fiktion.providers;

import ch.epfl.sweng.fiktion.models.PointOfInterest;
import ch.epfl.sweng.fiktion.models.Position;


/**
 * Database provider
 *
 * @author Pedro Da Cunha
 */
public abstract class DatabaseProvider {

    /**
     * Listener that listens the result of the addition of a point of interest
     */
    public interface AddPoiListener {
        /**
         * what to do if the addition succeeded
         */
        void onSuccess();

        /**
         * what to do if the poi already exists
         */
        void onAlreadyExists();

        /**
         * what to do if the addition failed
         */
        void onFailure();
    }

    /**
     * Listener that listens the result of the retrieval of a point of interest
     */
    public interface GetPoiListener {

        /**
         * what to do if the retrieval succeeds
         *
         * @param poi the retrieved point of interest
         */
        void onSuccess(PointOfInterest poi);

        /**
         * what to do if no mathing point of interest is found
         */
        void onDoesntExist();

        /**
         * what to do if the retrieval failed
         */
        void onFailure();
    }

    /**
     * Listener that listens the results of searching near points of interest
     */
    public interface FindNearPoisListener {

        /**
         * what to do when we get a new near point of interest
         *
         * @param poi the point of interest
         */
        void onNewValue(PointOfInterest poi);

        /**
         * what to do if operation failed
         */
        void onFailure();
    }

    /**
     * Listener that listens the results of searching by text from user
     */
    public interface PoiSearchByTextListener {

        /**
         * what to do if the retrieval failed
         * what to do if the retrieval succeeds
         *
         * @param poi the retrieved point of interest
         */
        void onSuccess(PointOfInterest poi);

        /**
         * what to do if no mathing point of interest is found
         */
        void onDoesntExist();

        /**
         */
        void onFailure();
    }

    /**
     * add a point of interest to the database, inform the listener of the result
     *
     * @param poi      the point of interest
     * @param listener the listener
     */
    public abstract void addPoi(final PointOfInterest poi, final AddPoiListener listener);

    /**
     * get the point of interest from the database, inform the listener of the result
     *
     * @param name     the name of the desired point of interest
     * @param listener the listener
     */
    public abstract void getPoi(String name, final GetPoiListener listener);

    /**
     * find the points of interest that are within radius range from a position and inform the
     * listener of the results
     *
     * @param pos      the position
     * @param radius   the radius
     * @param listener the listener
     */
    public abstract void findNearPois(Position pos, int radius, final FindNearPoisListener listener);
}
