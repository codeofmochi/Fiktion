package ch.epfl.sweng.fiktion.providers;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import ch.epfl.sweng.fiktion.models.Settings;
import ch.epfl.sweng.fiktion.models.User;
import ch.epfl.sweng.fiktion.utils.Config;

/**
 * A user implementation for Firebase
 *
 * @author pedro
 */
public class FirebaseUser {
    public String name = "";
    public String id = "";
    public Map<String, Boolean> favourites = new TreeMap<>();
    public Map<String, Boolean> wishlist = new TreeMap<>();
    public Map<String, Boolean> visited = new LinkedHashMap<>();
    public Map<String, Boolean> friendlist = new TreeMap<>();
    public Map<String, Boolean> friendRequests = new TreeMap<>();
    public Boolean isPublicProfile = true;
    public Map<String, Boolean> upvotes = new TreeMap<>();
    public FirebaseSettings settings = new FirebaseSettings(new Settings(Settings.DEFAULT_SEARCH_RADIUS));

    /**
     * Default constructor for calls to DataSnapshot.getValue(FirebaseUser.class)
     */
    public FirebaseUser() {
    }

    /**
     * Constructs a Firebase user
     *
     * @param user a user
     */
    public FirebaseUser(User user) {
        name = user.getName();
        id = user.getID();
        for (String fav : user.getFavourites()) {
            favourites.put(fav, true);
        }

        for (String wish : user.getWishlist()) {
            wishlist.put(wish, true);
        }

        for (String friend : user.getFriendlist()) {
            friendlist.put(friend, true);
        }

        for (String request : user.getRequests()) {
            friendRequests.put(request, true);
        }

        isPublicProfile = user.isPublicProfile();

        for (String upvote : user.getUpvoted()) {
            upvotes.put(upvote, true);
        }

        settings = new FirebaseSettings(user.getSettings());
    }

    /**
     * Returns the real version User
     *
     * @return the user
     */
    public User toUser() {
        return new User(name, id, new TreeSet<>(favourites.keySet()),
                new TreeSet<>(wishlist.keySet()),
                new TreeSet<>(friendlist.keySet()),
                new TreeSet<>(friendRequests.keySet()),
                new LinkedList<>(visited.keySet()),
                isPublicProfile,
                new TreeSet<>(upvotes.keySet()),
                settings.toSettings());
    }
}