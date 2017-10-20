package ch.epfl.sweng.fiktion.providers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.epfl.sweng.fiktion.models.User;
import ch.epfl.sweng.fiktion.providers.AuthProvider;

/**
 * Created by Rodrigo on 18.10.2017.
 */

public class LocalAuthProvider extends AuthProvider {

    List<User> userList = new ArrayList<User>
            (Collections.singletonList(new User("", "test@test.ch", "ID")));
    User currUser;
    Boolean signedIn = false;

    /**
     * Signs in a user with an email, a password and what to do afterwards
     *
     * @param email    user email
     * @param password user password
     * @param listener what to do after login
     */
    @Override
    public void signIn(String email, String password, AuthListener listener) {
        //we use same ID for every user in the tests. Firebase does not allow to create 2 account with same email
        //so we will focus on accounts with the same email
        if (password.equals("testing")) {
            currUser = new User("", email, "ID");
            signedIn = true;
            listener.onSuccess();
        }
    }

    /**
     * Signs the user out of the application
     */
    @Override
    public void signOut() {
        //decide what happens when user signOut
        currUser = null;
        signedIn = false;
    }

    /**
     * Validate the email provided by the user.
     *
     * @param email provied by the user
     * @return empty string if valid, error message otherwise
     */
    @Override
    public String validateEmail(String email) {
        String errMessage = "";
        if (!email.contains("@")) {
            errMessage = "Requires a valid email";
        }
        return errMessage;
    }

    /**
     * Validate the password provided by the user.
     *
     * @param password provided by the user
     * @return empty string if valid, error message otherwise
     */
    @Override
    public String validatePassword(String password) {
        String errMessage = "";
        if (password.isEmpty()) {
            errMessage = "Requires a valid password";
        } else {
            if (password.length() < 6) {
                errMessage = "Password must be at least 6 characters";
            }
        }
        return errMessage;
    }

    /**
     * Creates a new account using the provided informations.
     *
     * @param email    used to create the account
     * @param password used to create the account
     * @param listener that knows what to do with the results
     */
    @Override
    public void createUserWithEmailAndPassword(String email, String password, AuthListener listener) {
        //we use same ID for every user in the tests. Firebase does not allow to create 2 account with same email
        //so we will focus on accounts with the same email
        User newUser = new User("", email, "ID");
        if (userList.contains(newUser)) {
            listener.onFailure();
        } else {
            userList.add(newUser);
            currUser = newUser;
            signedIn = true;
            listener.onSuccess();
        }
    }

    /**
     * Sends a password reset mail, defines what to do afterwards
     *
     * @param listener what to do after email attempt
     */
    @Override
    public void sendPasswordResetEmail(AuthListener listener) {
        if(isConnected()){
            listener.onSuccess();
        }else {
            listener.onFailure();
        }
    }

    /**
     * Sends a verification email, defines what to do afterwards
     *
     * @param listener what to do after email attempt
     */
    @Override
    public void sendEmailVerification(AuthListener listener) {
        if(isConnected()){
            listener.onSuccess();
        }else {
            listener.onFailure();
        }
    }

    /**
     *
     * @return true if there is a user currently signed in, false otherwise
     */
    @Override
    public Boolean isConnected() {
        return signedIn;
    }
}
