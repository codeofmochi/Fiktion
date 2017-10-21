package ch.epfl.sweng.fiktion.providers;

import ch.epfl.sweng.fiktion.models.User;

/**
 * Created by rodri on 17.10.2017.
 */

@SuppressWarnings("DefaultFileTemplate")
public abstract class AuthProvider {

    /**
     * Defines what actions to take on auth op callback
     */
    public interface AuthListener {
        void onSuccess();
        void onFailure();
    }

    /**
     * Signs in a user with an email, a password and what to do afterwards
     * @param email user email
     * @param password user password
     * @param listener what to do after login
     */
    public abstract void signIn(String email, String password, AuthListener listener);

    /**
     * Signs the user out of the application
     */
    public abstract void signOut();

    /**
     * Validate the email provided by the user.
     * @param email provided by the user
     * @return empty string if valid, error message otherwise
     */
    public abstract String validateEmail(String email);

    /**
     * Validate the password provided by the user.
     * @param password provided by the user
     * @return empty string if valid, error message otherwise
     */
    public abstract String validatePassword(String password);

    /**
     * Creates a new account using the provided informations
     * @param password used to create the account
     */
    public abstract void createUserWithEmailAndPassword(String email, String password, final AuthListener listener);

    /**
     * Sends a password reset mail, defines what to do afterwards
     * @param listener what to do after email attempt
     */
    public abstract void sendPasswordResetEmail(AuthListener listener);

    /**
     * Sends an email verification to the current user connected
     * @param listener awaits the result and acts accordingly
     */
    public abstract void sendEmailVerification(AuthListener listener);

    /**
     * Verifies if the user is currently connected or not
     * @return true if user is signed in, false otherwise
     */
    public abstract Boolean isConnected();

    /**
     *
     * @return Currently signed in User or null if there is not any
     */
    public abstract User getCurrentUser();

    public abstract void changeName(String name,final AuthListener listener);



}
