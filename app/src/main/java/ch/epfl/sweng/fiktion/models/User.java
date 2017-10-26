package ch.epfl.sweng.fiktion.models;

import ch.epfl.sweng.fiktion.providers.AuthProvider;
import ch.epfl.sweng.fiktion.providers.Providers;

/**This class represents the User in the application
 *@author Rodrigo
 */

public class User {
    private String name;
    private String email;
    //we could use same id as firebase id or create our own id system
    private final String id;
    private boolean emailVerified;

    /**
     * Creates a new User with given paramaters
     * @param input_name Username
     * @param input_email Main user email
     * @param input_id User id
     * @param input_verified true if user has a verified email, false otherwise
     */
    public User(String input_name, String input_email, String input_id, Boolean input_verified) {
        name = input_name;
        email = input_email;
        id = input_id;
        emailVerified = input_verified;
    }

    /**
     * Changes this user's username
     * @param newName New username value
     * @param listener Handles what happens in case of success or failure of the changement
     */
    public void changeName(final String newName, final AuthProvider.AuthListener listener) {
        Providers.auth.changeName(newName, new AuthProvider.AuthListener() {
            @Override
            public void onSuccess() {
                name = newName;
                listener.onSuccess();
            }

            @Override
            public void onFailure() {
                listener.onFailure();
            }
        });
    }

    /**
     * Changes this user's email
     * @param newEmail New email value
     * @param listener What to do in case of success or failure of the changement
     */
    public void changeEmail(final String newEmail, final AuthProvider.AuthListener listener) {
        Providers.auth.changeEmail(newEmail, new AuthProvider.AuthListener() {
            @Override
            public void onSuccess() {
                email = newEmail;
                emailVerified = false;
                listener.onSuccess();
            }

            @Override
            public void onFailure() {
                listener.onFailure();
            }
        });
    }


    @Override
    public boolean equals(Object other) {
        if (other == null || !User.class.isAssignableFrom(other.getClass())) {
            return false;
        }

        User otherUser = (User) other;

        return this.name.equals(otherUser.name)
                && this.email.equals(otherUser.email)
                && this.id.equals(otherUser.id);
    }

    /**
     * @return the user display name
     */
    public String getName() {
        return name;
    }

    /**
     * @return user email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @return user ID
     */
    public String getID() {
        return id;
    }

    /**
     * CHecks wether the user has a verified email or not
     * @return true if he has a verified email, false otherwise
     */
    public Boolean isEmailVerified() {
        return emailVerified;
    }


}
