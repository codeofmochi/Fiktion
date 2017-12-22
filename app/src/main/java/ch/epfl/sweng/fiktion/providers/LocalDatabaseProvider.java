package ch.epfl.sweng.fiktion.providers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import ch.epfl.sweng.fiktion.models.Comment;
import ch.epfl.sweng.fiktion.models.PointOfInterest;
import ch.epfl.sweng.fiktion.models.Position;
import ch.epfl.sweng.fiktion.models.User;
import ch.epfl.sweng.fiktion.models.posts.Post;
import ch.epfl.sweng.fiktion.utils.HelperMethods;


/**
 * Local database provider
 *
 * @author pedro
 */
public class LocalDatabaseProvider extends DatabaseProvider {

    private List<PointOfInterest> poiList = new ArrayList<>();

    private final User defaultUser = new User("default", "defaultID");
    public List<User> users = new ArrayList<>(Collections.singletonList(defaultUser));
    private Map<String, List<String>> poiComments = new TreeMap<>();
    private Map<String, Comment> comments = new TreeMap<>();

    private Map<String, Set<GetCommentListener>> getCommentListeners = new TreeMap<>();
    private Map<String, Set<GetCommentsListener>> getCommentsListeners = new TreeMap<>();
    private Map<String, Set<GetPOIListener>> getPOIListeners = new TreeMap<>();

    private Map<String, Set<GetUserListener>> getUserListeners = new TreeMap<>();

    private Map<String, Set<Post>> posts = new TreeMap<>();
    private Map<String, Set<GetPostListener>> getPostListeners = new TreeMap<>();


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
    public void getAllPOIs(int numberOfPOIs, GetMultiplePOIsListener listener) {
        for (int i = 0; (numberOfPOIs == ALL_POIS || i < numberOfPOIs) && i < poiList.size(); ++i) {
            listener.onNewValue(poiList.get(i));
        }
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
                PointOfInterest mPOI = new PointOfInterest(poi.name(), poi.position(), poi.fictions(),
                        poi.description(), aPOI.rating(), poi.country(), poi.city());
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
    public void findNearPOIs(Position pos, int radius, ch.epfl.sweng.fiktion.providers.DatabaseProvider.GetMultiplePOIsListener listener) {
        if (pos.latitude() == 1000 && pos.longitude() == 1000) {
            listener.onFailure();
            return;
        }

        for (PointOfInterest poi : poiList) {
            if (HelperMethods.dist(pos.latitude(), pos.longitude(), poi.position().latitude(), poi.position().longitude()) <= radius) {
                listener.onNewValue(poi);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void searchByText(String text, DatabaseProvider.GetMultiplePOIsListener listener) {
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
            if (getUserListeners.containsKey(user.getID())) {
                for (GetUserListener l : getUserListeners.get(user.getID()))
                    l.onNewValue(user);
            }

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

        if (!getUserListeners.containsKey(id)) {
            getUserListeners.put(id, new HashSet<GetUserListener>());
        }
        getUserListeners.get(id).add(listener);

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

    /**
     * {@inheritDoc}
     */
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
                if (getUserListeners.containsKey(user.getID())) {
                    for (GetUserListener l : getUserListeners.get(user.getID()))
                        l.onModifiedValue(user);
                }

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

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void voteComment(String commentId, String userID, int vote, int previousVote, VoteListener listener) {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void getCommentVoteOfUser(String commentId, String userID, GetVoteListener listener) {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addUserPost(String userId, Post post, AddPostListener listener) {
        if (!posts.containsKey(userId)) {
            posts.put(userId, new HashSet<Post>());
        }
        posts.get(userId).add(post);

        if (getPostListeners.containsKey(userId)) {
            for (GetPostListener l : getPostListeners.get(userId)) {
                l.onNewValue(post);
            }
        }

        listener.onSuccess();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void getUserPosts(String userId, GetPostListener listener) {
        if (posts.containsKey(userId)) {
            for (Post post : posts.get(userId)) {
                listener.onNewValue(post);
            }
        }

        if (!getPostListeners.containsKey(userId)) {
            getPostListeners.put(userId, new HashSet<GetPostListener>());
        }
        getPostListeners.get(userId).add(listener);
    }
}