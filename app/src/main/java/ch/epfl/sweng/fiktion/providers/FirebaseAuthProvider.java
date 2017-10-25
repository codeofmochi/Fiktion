package ch.epfl.sweng.fiktion.providers;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import ch.epfl.sweng.fiktion.models.User;
import ch.epfl.sweng.fiktion.views.SignInActivity;

/**
 * Created by Rodrigo on 17.10.2017.
 */

@SuppressWarnings("DefaultFileTemplate")
class FirebaseAuthProvider extends AuthProvider {

    //testing
    private final static String TAG = "FBAuthProv";
    // firebase authentification instance
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    // firebase user that we authenticate
    private FirebaseUser user;
    // firebase status
    private FirebaseAuth.AuthStateListener state;

    /**
     * Set a StateListener to detect user account changes
     *
     * @param act the current activity in which we want to detect an account change
     */
    private void createStateListener(final Activity act) {
        state = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser newUser = firebaseAuth.getCurrentUser();
                if (newUser != null) {
                    //user is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + newUser.getUid());
                    //if user changed, recreate
                    if (user != newUser) {
                        auth.signOut();
                        Intent i = new Intent(act, SignInActivity.class);
                        act.startActivity(i);
                    }
                } else {
                    //user is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    Intent i = new Intent(act, SignInActivity.class);
                    act.startActivity(i);
                }
            }
        };
        auth.addAuthStateListener(state);
    }

    /**
     * Signs in a user with an email, a password and what to do afterwards
     *
     * @param email    user email
     * @param password user password
     * @param listener what to do after login
     */
    @Override
    public void signIn(String email, String password, final AuthListener listener) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    //reset textViews content
                    // Sign in success
                    Log.d(TAG, "signInWithEmail:success");
                    user = auth.getCurrentUser();
                    listener.onSuccess();
                } else {
                    // Sign in fails
                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                    listener.onFailure();
                }
            }
        });
    }

    /**
     * Signs the user out of the application
     */
    @Override
    public void signOut() {
        auth.signOut();
        user = null;
    }

    /**
     * Return true if the email and password are valid, false otherwise.
     *
     * @param email provided by the user
     * @return empty string if valid, error message otherwise
     */
    @Override
    public String validateEmail(String email) {
        String errMessage = "";
        //TODO elaborate email validation
        if (!email.contains("@")) {
            errMessage = "Requires a valid email";
            Log.d(TAG, "Email validation failed");
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
            Log.d(TAG, "Password validation failed");
        } else {
            //TODO elaborate password validation
            if (password.length() < 6) {
                errMessage = "Password must be at least 6 characters";
                Log.d(TAG, "Password validation failed");
            }
        }
        return errMessage;
    }

    /**
     * Creates a new account using the provided informations
     *
     * @param email    used to create the account
     * @param password used to create the account
     */
    @Override
    public void createUserWithEmailAndPassword(String email, String password, final AuthListener listener) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Account creation was successful
                            Log.d(TAG, "accountCreation: success");
                            listener.onSuccess();
                            user = auth.getCurrentUser();
                        } else {
                            // Account creation failed
                            Log.w(TAG, "accountCreation: failure", task.getException());
                            listener.onFailure();
                        }
                    }
                });
    }

    /**
     * Sends a password reset email, defines what to do afterwards
     *
     * @param listener what to do after email attempt
     */
    @Override
    public void sendPasswordResetEmail(final AuthListener listener) {
        String email = user.getEmail();
        if (user != null) {
            if (email != null) {
                auth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    //reset textViews content
                                    // Password reset email sent successfully
                                    Log.d(TAG, "TransmittingPasswordResetEmail: success");
                                    listener.onSuccess();
                                } else {
                                    // Password reset email failed to send
                                    Log.w(TAG, "TransmittingPasswordResetEmail: failure", task.getException());
                                    listener.onFailure();
                                }
                            }
                        });
            } else {
                listener.onFailure();
            }
        } else {
            listener.onFailure();
        }
    }

    /**
     * Sends an email verification to the current user connected
     * @param listener awaits the result and acts accordingly
     */
    @Override
    public void sendEmailVerification(final AuthListener listener) {
        user = auth.getCurrentUser();
        if (auth.getCurrentUser() != null) {
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        listener.onSuccess();
                    } else {
                        listener.onFailure();
                    }
                }
            });
        } else {
            listener.onFailure();
        }
    }

    /**
     * Verifies if the user is currently connected or not
     * @return true if user is signed in, false otherwise
     */
    @Override
    public Boolean isConnected() {
        return auth.getCurrentUser() != null;
    }

    /**
     *
     * @return Currently signed in User or null if there is not any
     */
    @Override
    public User getCurrentUser() {
        user = auth.getCurrentUser();
        if (isConnected()) {
            //TODO get user ID or EMAIL and get it from our database
            //for now I just create a new mock user
            return new User(user.getDisplayName(), user.getEmail(), user.getUid(), user.isEmailVerified());
        } else {
            return null;
        }
    }

    /**
     * Enables the user to change his username
     * @param newName new username provided by the user
     * @param listener actions to be done in case of failure or success
     */
    @Override
    public void changeName(String newName, final AuthListener listener) {
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(newName).build();

        if (isConnected()) {
            user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "DisplayName was updated");
                        listener.onSuccess();
                    } else {
                        Log.e(TAG, "DisplayName failed to update");
                        listener.onFailure();
                    }
                }
            });
        }else{
            listener.onFailure();
        }
    }

    /**
     * Enables the user to change his primary email
     * @param newEmail new email provided by the user
     * @param listener actions to be done in case of failure or success
     */
    @Override
    public void changeEmail(String newEmail, final AuthListener listener) {
        if (isConnected()) {
            user.updateEmail(newEmail).
                    addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Email was updated");
                        listener.onSuccess();
                    } else {
                        Log.e(TAG, "Email failed to update");
                        listener.onFailure();
                    }
                }
            });
        }else{
            listener.onFailure();
        }
    }

    /**
     * Enables the user to delete his account if he has signed in recently
     * @param listener actions to be done in case of failure or success
     */
    @Override
    public void deleteAccount(final AuthListener listener){
        //TODO delete application user in database after implementation of user storage in database
        if(user!= null) {
            user.delete()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "Account deleted successfully on firebase");
                                listener.onSuccess();
                            } else {
                                Log.d(TAG, "Failed to delete account on firebase");
                                listener.onFailure();
                            }
                        }
                    });
        } else{
            Log.d(TAG, "Failed to delete account on firebase, no user currently signed in");
            listener.onFailure();
        }
    }
}
