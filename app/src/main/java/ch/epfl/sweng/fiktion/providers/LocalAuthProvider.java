package ch.epfl.sweng.fiktion.providers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

import ch.epfl.sweng.fiktion.models.User;

/**
 * This class defines a local auth provider for testing without actual contact with firebase
 * Created by Rodrigo on 18.10.2017.
 */

public class LocalAuthProvider extends AuthProvider {
    private final User defaultUser = new User("default", "defaultID", new TreeSet<String>(), new TreeSet<String>(), new LinkedList<String>());
    private final String defaultEmail = "default@email.ch";
    private List<User> userList = new ArrayList<>
            (Collections.singletonList(defaultUser));
    private List<String> mailList = new ArrayList<>
            (Collections.singletonList(defaultEmail));
    private User currUser = defaultUser;
    private Boolean signedIn = true;
    private String currentUserEmail = defaultEmail;
    private Boolean emailVerified = false;

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
        if(email.equals("user1@test.ch") && password.equals("testing")){
            currUser = new User("user1", "id1");
            signedIn = true;
            currentUserEmail = email;
            listener.onSuccess();
        } else if (mailList.contains(email)
                && password.equals("testing")) {
            listener.onSuccess();
            currUser = new User("", "defaultID",new TreeSet<String>(), new TreeSet<String>(),new LinkedList<String>());
            signedIn = true;
            currentUserEmail = email;
        } else {
            listener.onFailure();
        }
    }

    /**
     * Signs the user out of the application
     */
    @Override
    public void signOut() {
        //decide what happens when user signOut
        currUser = null;
        currentUserEmail = null;
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

        User newUser = new User("new", "newID", new TreeSet<String>(), new TreeSet<String>(), new LinkedList<String>());
        if (mailList.contains(email)) {
            listener.onFailure();
        } else {
            userList.add(newUser);
            mailList.add(email);
            currUser = newUser;
            currentUserEmail = email;
            signedIn = true;
            emailVerified = false;
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
        if (isConnected()) {
            listener.onSuccess();
        } else {
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
        if (isConnected()) {
            listener.onSuccess();
        } else {
            listener.onFailure();
        }
    }

    /**
     * @return true if there is a user currently signed in, false otherwise
     */
    @Override
    public Boolean isConnected() {
        return signedIn;
    }

    /**
     * starts a request to database to have currently signed in User or null if there is not any
     *
     * @param listener handles what to do after the request
     */
    @Override
    public void getCurrentUser(DatabaseProvider.GetUserListener listener) {
        if (isConnected()) {
            listener.onSuccess(currUser);
        } else {
            listener.onFailure();
        }
    }


    @Override
    public void changeEmail(String newEmail, AuthListener listener) {
        if (newEmail.equals("new@email.ch")) {
            currentUserEmail = newEmail;
            listener.onSuccess();

        } else {
            listener.onFailure();
        }
    }

    /**
     * Enables the user to delete his account if he has signed in recently
     *
     * @param listener    actions to be done in case of failure or success in firebase authentication
     * @param delListener actions to be done in case of failure, sucess or inexistant in database
     */
    @Override
    public void deleteAccount(AuthListener listener, DatabaseProvider.DeleteUserListener delListener) {
        if (isConnected()) {
            listener.onSuccess();
            delListener.onSuccess();

        } else {
            listener.onFailure();
        }

    }

    /**
     * @return true if user is email verified, false otherwise
     */
    @Override
    public Boolean isEmailVerified() {
        return emailVerified;
    }

    /**
     * @return email of the current signed in user, null if there is not any user connected
     */
    @Override
    public String getEmail() {
        return currentUserEmail;
    }

    public void reset(){
        userList = new ArrayList<>
                (Collections.singletonList(defaultUser));
        mailList = new ArrayList<>
                (Collections.singletonList(defaultEmail));
        currUser = defaultUser;
        signedIn = true;
        currentUserEmail = defaultEmail;
        emailVerified = false;
    }

}
