package ch.epfl.sweng.fiktion.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

/**
 * Created by rodri on 17.10.2017.
 */

public abstract class AuthProvider {

    /**
     * Defines what actions to take on auth op callback
     */
    public interface AuthListener {
        public abstract void onSuccess();
        public abstract void onFailure();
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
     * Return true if the email and password are valid, false otherwise.
     * @return empty string if valid, error message otherwise
     */
    public abstract String validateEmailAndPassword(String email, String password);

    /**
     * Creates a new account using the provided informations
     * @param email used to create the account
     * @param password used to create the account
     */
    public abstract void createUserWithEmailAndPassword(String email, String password);

    /**
     * Sends a password reset mail, defines what to do afterwards
     * @param listener what to do after email attempt
     */
    public abstract void sendPasswordResetEmail(AuthListener listener);

}
