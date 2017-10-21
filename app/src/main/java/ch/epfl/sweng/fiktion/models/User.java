package ch.epfl.sweng.fiktion.models;

import ch.epfl.sweng.fiktion.providers.AuthProvider;
import ch.epfl.sweng.fiktion.providers.Providers;

/**
 * Created by rodrigo on 09.10.2017.
 */

@SuppressWarnings("DefaultFileTemplate")
public class User {
    private String name;
    private String email;
    //we could use same id as firebase id or create our own id system
    private final String id;
    private boolean emailVerified;

    public User(String input_name, String input_email, String input_id, Boolean input_verified) {
        name = input_name;
        email = input_email;
        id = input_id;
        emailVerified = input_verified;
    }

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

    public void changeEmail(final String newEmail, final AuthProvider.AuthListener listener) {
        Providers.auth.changeName(newEmail, new AuthProvider.AuthListener() {
            @Override
            public void onSuccess() {
                email = newEmail;
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

    public Boolean isEmailVerified() {
        return emailVerified;
    }



}
