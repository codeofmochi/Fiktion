package ch.epfl.sweng.fiktion.providers;

import ch.epfl.sweng.fiktion.models.User;

/**
 * A user implementation for Firebase
 *
 * @author pedro
 */
public class FirebaseUser {
    public String name;
    public String email;
    public String id;
    public boolean emailVerified;

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
        email = user.getEmail();
        id = user.getID();
        emailVerified = user.isEmailVerified();
    }

    /**
     * Returns the real version User
     *
     * @return the user
     */
    public User toUser() {
        return new User(name, email, id, emailVerified);
    }
}