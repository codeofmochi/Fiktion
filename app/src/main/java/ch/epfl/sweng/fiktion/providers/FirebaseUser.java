package ch.epfl.sweng.fiktion.providers;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import ch.epfl.sweng.fiktion.models.User;

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
    }

    /**
     * Returns the real version User
     *
     * @return the user
     */
    public User toUser() {
        return new User(name, id, favourites.keySet(), wishlist.keySet());
    }
}