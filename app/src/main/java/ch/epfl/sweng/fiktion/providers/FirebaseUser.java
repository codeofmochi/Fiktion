package ch.epfl.sweng.fiktion.providers;

import java.util.Set;

import ch.epfl.sweng.fiktion.models.User;

/**
 * A user implementation for Firebase
 *
 * @author pedro
 */
public class FirebaseUser {
    public String name;
    public String id;
    public Set<String> favourites;

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
        favourites = user.getFavourites();
    }

    /**
     * Returns the real version User
     *
     * @return the user
     */
    public User toUser() {
        return new User(name, id, favourites);
    }
}