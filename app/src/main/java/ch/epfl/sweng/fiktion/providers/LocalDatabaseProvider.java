package ch.epfl.sweng.fiktion.providers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import ch.epfl.sweng.fiktion.models.Comment;
import ch.epfl.sweng.fiktion.models.PointOfInterest;
import ch.epfl.sweng.fiktion.models.Position;
import ch.epfl.sweng.fiktion.models.Settings;
import ch.epfl.sweng.fiktion.models.User;


/**
 * Local database provider
 *
 * @author pedro
 */
public class LocalDatabaseProvider extends DatabaseProvider {
    private final User defaultUser = new User("default", "defaultID");
    private final User user1 = new User("user1", "id1");
    // Initiating friendlists and friendRequests
    private final String[] frList = new String[]{"defaultID"};
    private final String[] rList = new String[]{"id1"};
    private final String[] fakeFList = new String[]{"idfake"};
    private final String[] fakeRList = new String[]{"idfake"};
    private final String[] favList = new String[]{"fav POI"};
    private final String[] whishList = new String[]{"wish POI"};
    private final String[] visitedList = new String[]{"vis POI"};

    // user has "fav POI" as favourite, "vis POI" in visited and "wish POI" in wishlist
    private final User userWVFav = new User("userWVFav", "idwvfav", new TreeSet<>(Arrays.asList(favList)), new TreeSet<>(Arrays.asList(whishList)),
            new TreeSet<String>(), new TreeSet<String>(),
            new LinkedList<>(Arrays.asList(visitedList)), true, new TreeSet<String>(), new Settings(Settings.DEFAULT_SEARCH_RADIUS));

    // user is friend with defaultUser and has user1 in his requests
    private final User userFR = new User("userFR", "idfr", new TreeSet<String>(), new TreeSet<String>(),
            new TreeSet<>(Arrays.asList(frList)), new TreeSet<>(Arrays.asList(rList)),
            new LinkedList<String>(), true, new TreeSet<String>(), new Settings(Settings.DEFAULT_SEARCH_RADIUS));

    // user with a friend that is not stored in the database
    private final User userFakeF = new User("userFakeF", "idfakef", new TreeSet<String>(), new TreeSet<String>(),
            new TreeSet<>(Arrays.asList(fakeFList)), new TreeSet<String>(),
            new LinkedList<String>(), true, new TreeSet<String>(), new Settings(Settings.DEFAULT_SEARCH_RADIUS));

    // user has request from fake friend
    private final User userFakeR = new User("userFakeR", "idfaker", new TreeSet<String>(), new TreeSet<String>(),
            new TreeSet<String>(), new TreeSet<>(Arrays.asList(fakeRList)),
            new LinkedList<String>(), true, new TreeSet<String>(), new Settings(Settings.DEFAULT_SEARCH_RADIUS));

    private final List<User> initialList = Arrays.asList(defaultUser, user1, userFR, userFakeF, userFakeR, userWVFav);
    public List<PointOfInterest> poiList = new ArrayList<>();
    public List<User> users = new ArrayList<>(initialList);
    private Map<String, List<String>> poiComments = new TreeMap<>();
    private Map<String, Comment> comments = new TreeMap<>();

    private Map<String, Set<GetCommentListener>> getCommentListeners = new TreeMap<>();
    private Map<String, Set<GetCommentsListener>> getCommentsListeners = new TreeMap<>();
    private Map<String, Set<GetPOIListener>> getPOIListeners = new TreeMap();

    /**
     * {@inheritDoc}
     */

    public void addPOI(PointOfInterest poi, AddPOIListener listener) {
        if (poi.name().contains("ADDPOIS")) {
            listener.onSuccess();
            return;
        }
        if (poi.name().contains("ADDPOIA")) {
            listener.onAlreadyExists();
            return;
        }
        if (poi.name().contains("ADDPOIF")) {
            listener.onFailure();
            return;
        }

        if (poiList.contains(poi)) {
            // inform the listener that the poi already exists
            listener.onAlreadyExists();
        } else {
            // add the poi
            poiList.add(poi);

            // inform the listeners that listen the retrieval of a poi with poi.name that it now exists
            if (getPOIListeners.containsKey(poi.name())) {
                for (GetPOIListener getPOIListener : getPOIListeners.get(poi.name())) {
                    getPOIListener.onNewValue(poi);
                }
            }

            // inform the listener that the operation succeeded
            listener.onSuccess();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void getPOI(String name, GetPOIListener listener) {
        if (name.contains("GETPOIS")) {
            listener.onNewValue(new PointOfInterest("SUCCESS",
                    new Position(0, 0),
                    new TreeSet<String>(),
                    "",
                    0,
                    "",
                    ""));
            return;
        }
        if (name.contains("GETPOIM")) {
            listener.onModifiedValue(new PointOfInterest("MODIFIED",
                    new Position(0, 0),
                    new TreeSet<String>(),
                    "",
                    0,
                    "",
                    ""));
            return;
        }
        if (name.contains("GETPOID")) {
            listener.onDoesntExist();
            return;
        }
        if (name.contains("GETPOIF")) {
            listener.onFailure();
            return;
        }


        if (!getPOIListeners.containsKey(name)) {
            getPOIListeners.put(name, new HashSet<GetPOIListener>());
        }
        getPOIListeners.get(name).add(listener);

        for (PointOfInterest poi : poiList) {
            if (poi.name().equals(name)) {
                // inform the listener that we have the poi
                listener.onNewValue(poi);
                return;
            }
        }

        // inform the listener that the poi doesnt exist
        listener.onDoesntExist();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void modifyPOI(PointOfInterest poi, ModifyPOIListener listener) {
        if (poi.name().contains("MODIFYPOIS")) {
            listener.onSuccess();
            return;
        }
        if (poi.name().contains("MODIFYPOID")) {
            listener.onDoesntExist();
            return;
        }
        if (poi.name().contains("MODIFYPOIF")) {
            listener.onFailure();
            return;
        }

        for (int i = 0; i < poiList.size(); ++i) {
            PointOfInterest aPOI = poiList.get(i);
            if (poi.equals(aPOI)) {
                PointOfInterest mPOI = new PointOfInterest(poi.name(),poi.position(),poi.fictions(),
                        poi.description(),aPOI.rating(),poi.country(), poi.city());
                poiList.set(i, mPOI);
                listener.onSuccess();

                // inform the listeners that listen the retrieval of a poi with poi.name that it has been modified
                if (getPOIListeners.containsKey(poi.name())) {
                    for (GetPOIListener getPOIListener : getPOIListeners.get(poi.name())) {
                        getPOIListener.onModifiedValue(mPOI);
                    }
                }

                return;
            }
        }
        listener.onDoesntExist();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void upvote(String poiName, ModifyPOIListener listener) {
        if (poiName.contains("UPVOTES")) {
            listener.onSuccess();
            return;
        }
        if (poiName.contains("UPVOTED")) {
            listener.onDoesntExist();
            return;
        }
        if (poiName.contains("UPVOTEF")) {
            listener.onFailure();
            return;
        }

        for (int i = 0; i < poiList.size(); ++i) {
            PointOfInterest poi = poiList.get(i);
            if (poiName.equals(poi.name())) {
                PointOfInterest poiPlus = new PointOfInterest(poi.name(), poi.position(), poi.fictions(),
                        poi.description(), poi.rating() + 1, poi.country(), poi.city());
                poiList.set(i, poiPlus);

                // inform the listeners that listen the retrieval of a poi with poi.name that it has been modified
                if (getPOIListeners.containsKey(poi.name())) {
                    for (GetPOIListener getPOIListener : getPOIListeners.get(poi.name())) {
                        getPOIListener.onModifiedValue(poiPlus);
                    }
                }

                listener.onSuccess();
                return;
            }
        }
        listener.onDoesntExist();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void downvote(String poiName, ModifyPOIListener listener) {
        if (poiName.contains("DOWNVOTES")) {
            listener.onSuccess();
            return;
        }
        if (poiName.contains("DOWNVOTED")) {
            listener.onDoesntExist();
            return;
        }
        if (poiName.contains("DOWNVOTEF")) {
            listener.onFailure();
            return;
        }

        for (int i = 0; i < poiList.size(); ++i) {
            PointOfInterest poi = poiList.get(i);
            if (poiName.equals(poi.name())) {
                PointOfInterest poiMinus = new PointOfInterest(poi.name(), poi.position(), poi.fictions(),
                        poi.description(), poi.rating() - 1, poi.country(), poi.city());
                poiList.set(i, poiMinus);

                // inform the listeners that listen the retrieval of a poi with poi.name that it has been modified
                if (getPOIListeners.containsKey(poi.name())) {
                    for (GetPOIListener getPOIListener : getPOIListeners.get(poi.name())) {
                        getPOIListener.onModifiedValue(poiMinus);
                    }
                }

                listener.onSuccess();
                return;
            }
        }
        listener.onDoesntExist();
    }

    /**
     * {@inheritDoc}
     */
    public void findNearPOIs(Position pos, int radius, FindNearPOIsListener listener) {
        if (pos.latitude() == 1000 && pos.longitude() == 1000) {
            listener.onFailure();
            return;
        }

        for (PointOfInterest poi : poiList) {
            if (dist(pos.latitude(), pos.longitude(), poi.position().latitude(), poi.position().longitude()) <= radius) {
                listener.onNewValue(poi);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void searchByText(String text, SearchPOIByTextListener listener) {
        if (text.contains("SEARCHN")) {
            listener.onNewValue(new PointOfInterest("NEWVALUE",
                    new Position(0, 0),
                    new TreeSet<String>(),
                    "",
                    0,
                    "",
                    ""));
            return;
        }
        if (text.contains("SEARCHF")) {
            listener.onFailure();
            return;
        }

        for (PointOfInterest poi : poiList) {
            if (poi.name().contains(text) ||
                    poi.description().contains(text) ||
                    poi.city().contains(text) ||
                    poi.country().contains(text)) {
                listener.onNewValue(poi);
            }
        }
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void addUser(User user, AddUserListener listener) {
        if (user.getID().contains("ADDUSERS")) {
            listener.onSuccess();
            return;
        }
        if (user.getID().contains("ADDUSERA")) {
            listener.onAlreadyExists();
            return;
        }
        if (user.getID().contains("ADDUSERF")) {
            listener.onFailure();
            return;
        }

        boolean contains = false;
        String id = user.getID();
        // go through all the users and check if there is one with the same id as the user in parameter
        for (User u : users) {
            contains |= u.getID().equals(id);
        }
        if (contains) {
            listener.onAlreadyExists();
        } else {
            users.add(user);
            listener.onSuccess();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void getUserById(String id, GetUserListener listener) {
        if (id.contains("GETUSERS")) {
            listener.onNewValue(new User(id, id));
            return;
        }
        if (id.contains("GETUSERD")) {
            listener.onDoesntExist();
            return;
        }
        if (id.contains("GETUSERF")) {
            listener.onFailure();
            return;
        }

        for (User u : users) {
            if (u.getID().equals(id)) {
                listener.onNewValue(u);
                return;
            }
        }
        listener.onDoesntExist();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleterUserById(String id, DeleteUserListener listener) {
        if (id.contains("DELETEUSERS")) {
            listener.onSuccess();
            return;
        }
        if (id.contains("DELETEUSERD")) {
            listener.onDoesntExist();
            return;
        }
        if (id.contains("DELETEUSERF")) {
            listener.onFailure();
            return;
        }

        for (User u : users) {
            if (u.getID().equals(id)) {
                users.remove(u);
                listener.onSuccess();
                return;
            }
        }
        listener.onDoesntExist();
    }

    @Override
    public void modifyUser(final User user, final ModifyUserListener listener) {
        if (user.getID().contains("MODIFYUSERS")) {
            listener.onSuccess();
            return;
        }
        if (user.getID().contains("MODIFYUSERD")) {
            listener.onDoesntExist();
            return;
        }
        if (user.getID().contains("MODIFYUSERF")) {
            listener.onFailure();
            return;
        }

        deleterUserById(user.getID(), new DeleteUserListener() {
            @Override
            public void onSuccess() {
                users.add(user);
                listener.onSuccess();
            }

            @Override
            public void onDoesntExist() {
                listener.onDoesntExist();
            }

            @Override
            public void onFailure() {
                listener.onFailure();
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addComment(Comment comment, String poiName, AddCommentListener listener) {
        if (poiName.contains("ADDCOMMENTS")) {
            listener.onSuccess();
            return;
        }
        if (poiName.contains("ADDCOMMENTF")) {
            listener.onFailure();
            return;
        }
        if (poiComments.containsKey(poiName)) {
            List<String> poiCommentsIds = poiComments.get(poiName);
            poiCommentsIds.add(comment.getId());
        } else {
            List<String> poiCommentsIds = new ArrayList<>();
            poiCommentsIds.add(comment.getId());
            poiComments.put(poiName, poiCommentsIds);
        }
        comments.put(comment.getId(), comment);


        if (getCommentListeners.containsKey(comment.getId())) {
            for (GetCommentListener l : getCommentListeners.get(comment.getId())) {
                l.onNewValue(comment);
            }
        }
        if (getCommentsListeners.containsKey(poiName)) {
            for (GetCommentsListener l : getCommentsListeners.get(poiName)) {
                l.onNewValue(comment);
            }
        }
        listener.onSuccess();
    }

    @Override
    public void getComment(String commentId, GetCommentListener listener) {
        if (commentId.contains("GETCOMMENTN")) {
            listener.onNewValue(new Comment("GETCOMMENTN", "GETCOMMENTN", "author", new Date(0), 0));
            return;
        }
        if (commentId.contains("GETCOMMENTM")) {
            listener.onNewValue(new Comment("GETCOMMENTM", "GETCOMMENTM", "author", new Date(0), 0));
            return;
        }
        if (commentId.contains("GETCOMMENTD")) {
            listener.onDoesntExist();
            return;
        }
        if (commentId.contains("GETCOMMENTF")) {
            listener.onFailure();
            return;
        }

        if (!getCommentListeners.containsKey(commentId)) {
            getCommentListeners.put(commentId, new HashSet<GetCommentListener>());
        }
        getCommentListeners.get(commentId).add(listener);


        if (comments.containsKey(commentId)) {
            listener.onNewValue(comments.get(commentId));
        } else {
            listener.onDoesntExist();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void getPOIComments(String poiName, final GetCommentsListener listener) {
        if (poiName.contains("GETCOMMENTN")) {
            listener.onNewValue(new Comment("GETCOMMENTN", "GETCOMMENTN", "author", new Date(0), 0));
            return;
        }
        if (poiName.contains("GETCOMMENTM")) {
            listener.onNewValue(new Comment("GETCOMMENTM", "GETCOMMENTM", "author", new Date(0), 0));
            return;
        }
        if (poiName.contains("GETCOMMENTF")) {
            listener.onFailure();
            return;
        }
        if (!getCommentsListeners.containsKey(poiName)) {
            getCommentsListeners.put(poiName, new HashSet<GetCommentsListener>());
        }
        getCommentsListeners.get(poiName).add(listener);


        if (poiComments.containsKey(poiName)) {
            for (String id : poiComments.get(poiName)) {
                getComment(id, new GetCommentListener() {
                    @Override
                    public void onNewValue(Comment comment) {
                        listener.onNewValue(comment);
                    }

                    @Override
                    public void onModifiedValue(Comment comment) {
                        listener.onModifiedValue(comment);
                    }

                    @Override
                    public void onDoesntExist() {
                    }

                    @Override
                    public void onFailure() {
                        listener.onFailure();
                    }
                });
            }
        }
    }

    @Override
    public void voteComment(String commentId, String userID, int vote, int previousVote, VoteListener listener) {

    }

    @Override
    public void getCommentVoteOfUser(String commentId, String userID, GetVoteListener listener) {

    }
}