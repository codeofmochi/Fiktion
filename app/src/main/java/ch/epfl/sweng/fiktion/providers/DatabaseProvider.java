package ch.epfl.sweng.fiktion.providers;

import ch.epfl.sweng.fiktion.models.Comment;
import ch.epfl.sweng.fiktion.models.PointOfInterest;
import ch.epfl.sweng.fiktion.models.Position;
import ch.epfl.sweng.fiktion.models.User;
import ch.epfl.sweng.fiktion.utils.Config;


/**
 * Database provider
 *
 * @author Pedro Da Cunha
 */
public abstract class DatabaseProvider {

    private static DatabaseProvider database;

    /**
     * return the database provider
     *
     * @return the database provider
     */
    public static DatabaseProvider getInstance() {
        if (database == null) {
            if (Config.TEST_MODE)
                database = new LocalDatabaseProvider();
            else
                database = new FirebaseDatabaseProvider();
        }
        return database;
    }

    /**
     * Sets the current instance to the given database instance
     *
     * @param dbInstance database instance
     */
    public static void setInstance(DatabaseProvider dbInstance) {
        database = dbInstance;
    }

    /**
     * Destroys the current database instance
     */
    public static void destroyInstance() {
        database = null;
    }

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
         * what to do if the poi is modified
         *
         * @param poi the modified poi
         */
        void onModified(PointOfInterest poi);

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
     * Listener that listens the result of the modification of a point of interest
     */
    public interface ModifyPOIListener {
        /**
         * what to do if the modification succeeds
         */
        void onSuccess();

        /**
         * what to do if the poi doesn't exist
         */
        void onDoesntExist();

        /**
         * what to do if the modification failed
         */
        void onFailure();
    }


    /**
     * parent listener for searching points of interest
     */
    private interface SearchPOIsListener {

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
     * Listener that listens the results of searching near points of interest
     */
    public interface FindNearPoisListener extends SearchPOIsListener {
    }

    /**
     * Listener that  listens the results of searching points of interest by text
     */
    public interface SearchPOIByTextListener extends SearchPOIsListener {
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
     * Modify an existing point of interest and inform the listener of the result, the modification
     * doesn't change the rating -> call upvote/downvote if you want to change the rating
     *
     * @param poi      the new point of interest
     * @param listener the listener
     */
    public abstract void modifyPOI(PointOfInterest poi, ModifyPOIListener listener);

    /**
     * increases by 1 the rating of a point of interest, inform the listener of the result
     *
     * @param poiName  the name of the poi
     * @param listener the listener
     */
    public abstract void upvote(String poiName, ModifyPOIListener listener);

    /**
     * decreases by 1 the rating of a point of interest, inform the listener of the result
     *
     * @param poiName  the name of the poi
     * @param listener the listener
     */
    public abstract void downvote(String poiName, ModifyPOIListener listener);

    /**
     * find the points of interest that are within radius range from a position and inform the
     * listener of the results
     *
     * @param pos      the position
     * @param radius   the radius
     * @param listener the listener
     */
    public abstract void findNearPois(Position pos, int radius, FindNearPoisListener listener);

    /**
     * seach the points of interest that contain a text in one of their fields and "send" them to
     * the listener
     *
     * @param text     the text we search
     * @param listener the listener
     */
    public abstract void searchByText(String text, SearchPOIByTextListener listener);

    /**
     * Listener that listens the result of the addition of a user
     */
    public interface AddUserListener {

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
     * Listener that listens the result of the retrieval of a user
     */
    public interface GetUserListener {

        /**
         * what to do if the retrieval succeeds
         *
         * @param user the retrieved user
         */
        void onSuccess(User user);

        /**
         * what to do if no mathing user id is found
         */
        void onDoesntExist();

        /**
         * what to do if the retrieval failed
         */
        void onFailure();
    }

    private interface OperationOnExistingUserListener {

        /**
         * what to do if the deletion succeeded
         */
        void onSuccess();

        /**
         * what to do if the user doesn't exist
         */
        void onDoesntExist();

        /**
         * what to do if the deletion failed
         */
        void onFailure();
    }

    /**
     * Listener that listens the result of the deletion of a user
     */
    public interface DeleteUserListener extends OperationOnExistingUserListener {
    }

    /**
     * Listener that listens the result of the modification of a user
     */
    public interface ModifyUserListener extends OperationOnExistingUserListener {
    }

    /**
     * add a user to the database, inform the listener of the result
     *
     * @param user     the user
     * @param listener the listener
     */
    public abstract void addUser(final User user, AddUserListener listener);

    /**
     * get the user associated to the id, inform the listener of the result
     *
     * @param id       the id
     * @param listener the listener
     */
    public abstract void getUserById(String id, GetUserListener listener);

    /**
     * delete the user associated to the id, inform the listener of the result
     *
     * @param id       the id
     * @param listener the listener
     */
    public abstract void deleterUserById(String id, DeleteUserListener listener);

    /**
     * modify the user, inform the listener of the result of the modification
     *
     * @param user     the user
     * @param listener the listener
     */
    public abstract void modifyUser(User user, ModifyUserListener listener);

    /**
     * Listener that listens the result of the add
     */
    public interface AddCommentListener {

        /**
         * what to do if the addition succeeded
         */
        void onSuccess();

        /**
         * what to do if the addition failed
         */
        void onFailure();
    }

    public interface GetCommentsListener {
        void onNewValue(Comment comment);

        void onFailure();
    }

    /**
     * add a comment, inform the listener of the result
     *
     * @param comment  the comment to add
     * @param poiName  the name of the POI
     * @param listener the listener
     */
    public abstract void addComment(Comment comment, String poiName, AddCommentListener listener);

    /**
     * get the comments of a poi, inform the listener of the results
     *
     * @param poiName  the name of the poi
     * @param listener the listener
     */
    public abstract void getComments(String poiName, GetCommentsListener listener);
}
