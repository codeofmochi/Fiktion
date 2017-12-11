package ch.epfl.sweng.fiktion.providers;

import ch.epfl.sweng.fiktion.listeners.AlreadyExists;
import ch.epfl.sweng.fiktion.listeners.DoesntExist;
import ch.epfl.sweng.fiktion.listeners.Failure;
import ch.epfl.sweng.fiktion.listeners.Get;
import ch.epfl.sweng.fiktion.listeners.Modify;
import ch.epfl.sweng.fiktion.listeners.Success;
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
    public interface AddPoiListener extends Success, AlreadyExists, Failure {
    }

    /**
     * Listener that listens the result of the retrieval of a point of interest
     */
    public interface GetPoiListener extends Get<PointOfInterest>, Modify<PointOfInterest>, DoesntExist, Failure {
    }

    /**
     * Listener that listens the result of the modification of a point of interest
     */
    public interface ModifyPOIListener extends Success, DoesntExist, Failure {
    }

    /**
     * Listener that listens the results of searching near points of interest
     */
    public interface FindNearPoisListener extends Get<PointOfInterest>, Failure {
    }

    /**
     * Listener that  listens the results of searching points of interest by text
     */
    public interface SearchPOIByTextListener extends Get<PointOfInterest>, Failure {
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
    public interface AddUserListener extends Success, AlreadyExists, Failure {
    }

    /**
     * Listener that listens the result of the retrieval of a user
     */
    public interface GetUserListener extends Get<User>, Modify<User>, DoesntExist, Failure {
    }

    /**
     * Listener that listens the result of the deletion of a user
     */
    public interface DeleteUserListener extends Success, DoesntExist, Failure {
    }

    /**
     * Listener that listens the result of the modification of a user
     */
    public interface ModifyUserListener extends Success, DoesntExist, Failure {
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
    public interface AddCommentListener extends Success, Failure {
    }

    /**
     * Listener that listens the result of the retrieval of a comment
     */
    public interface GetCommentListener extends Get<Comment>, Modify<Comment>, DoesntExist, Failure {
    }

    /**
     * Listener that listens the retrieving of comments
     */
    public interface GetCommentsListener extends Get<Comment>, Modify<Comment>, Failure {
    }

    /**
     * Listener that listens the result of a vote
     */
    public interface VoteListener extends Success, Failure {
    }

    public final static int UPVOTE = 1;
    public final static int NOVOTE = 0;
    public final static int DOWNVOTE = -1;

    /**
     * Listener that listens the result of the retrieval of a vote
     */
    public interface GetVoteListener extends Get<Integer>, Failure {
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
     * get the comment associated to the provided id, inform the listener of the result of the retrieval
     *
     * @param commentId the comment id
     * @param listener  the listener
     */
    public abstract void getComment(String commentId, GetCommentListener listener);

    /**
     * get the comments of a poi, inform the listener of the results
     *
     * @param poiName  the name of the poi
     * @param listener the listener
     */
    public abstract void getPOIComments(String poiName, GetCommentsListener listener);

    /**
     * This will change the vote of an user for a comment:
     * upvote(UPVOTE), downvote(DOWNVOTE) or remove a vote(NOVOTE)
     *
     * @param commentId    the id of the comment
     * @param userID       the id of the user
     * @param vote         the desired vote
     * @param previousVote the previous vote
     * @param listener     a listener that listens the result of the operation
     */
    public abstract void voteComment(String commentId, String userID, int vote, int previousVote, VoteListener listener);

    /**
     * get the vote of a user given to a comment, inform the listener of the result:
     * upvoted(UPVOTE), downvoted(DOWNVOTE) or no vote(NOVOTE)
     *
     * @param commentId the id of the comment
     * @param userID    the id of the user
     * @param listener  the listener
     */
    public abstract void getCommentVoteOfUser(String commentId, String userID, GetVoteListener listener);
}
