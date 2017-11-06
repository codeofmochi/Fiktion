package ch.epfl.sweng.fiktion.providers;

import android.provider.Settings;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ch.epfl.sweng.fiktion.models.User;


/**
 * This class represents our application provider and uses FirebaseAuthentication
 * Created by Rodrigo on 17.10.2017.
 */

public class FirebaseAuthProvider extends AuthProvider {

    //testing
    private final static String TAG = "FBAuthProv";
    // firebase authentification instance
    private final FirebaseAuth auth;
    // firebase user that we authenticate
    private FirebaseUser user ;
    // firebase status
    /*
    private FirebaseAuth.AuthStateListener state;

    /**
     * Set a StateListener to detect user account changes
     *
     * @param act the current activity in which we want to detect an account change
     */
    /*
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
*/

    public FirebaseAuthProvider(){
        auth = FirebaseAuth.getInstance();
    }

    public FirebaseAuthProvider(FirebaseAuth fbAuth){
        auth = fbAuth;
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
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    //reset textViews content
                    // Sign in success
                    user = auth.getCurrentUser();
                    listener.onSuccess();
                } else {
                    // Sign in fails
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
        user = auth.getCurrentUser();
        //TODO elaborate email validation
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
            //TODO elaborate password validation
            if (password.length() < 6) {
                errMessage = "Password must be at least 6 characters";
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
        //create user in FirebaseAuthentication
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Account creation was successful in FirebaseAuthentication
                            //need to create user in our database

                            Providers.database.addUser(new User("", auth.getUid()), new DatabaseProvider.AddUserListener() {


                                @Override
                                public void onSuccess() {
                                    user = auth.getCurrentUser();
                                    listener.onSuccess();
                                }


                                @Override
                                public void onAlreadyExists() {
                                    listener.onFailure();
                                }


                                @Override
                                public void onFailure() {
                                    listener.onFailure();
                                }
                            });

                            listener.onSuccess();
                        } else {
                            // Account creation failed
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
        user = auth.getCurrentUser();
        if (user != null) {
            String email = user.getEmail();
            if (email != null) {
                auth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    //reset textViews content
                                    // Password reset email sent successfully
                                    listener.onSuccess();
                                } else {
                                    // Password reset email failed to send
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
     *
     * @param listener awaits the result and acts accordingly
     */
    @Override
    public void sendEmailVerification(final AuthListener listener) {
        user = auth.getCurrentUser();
        if (user != null) {
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
     *
     * @return true if user is signed in, false otherwise
     */
    @Override
    public Boolean isConnected() {
        return auth.getCurrentUser() != null;
    }

    /**
     * Starts request to retrieve currently signed in User or null if there is not any
     */
    @Override
    public void getCurrentUser(final DatabaseProvider.GetUserListener listener) {
        user = auth.getCurrentUser();
        if (user != null) {
            Providers.database.getUserById(user.getUid(), new DatabaseProvider.GetUserListener() {
                @Override
                public void onSuccess(User user) {
                    listener.onSuccess(user);
                }

                @Override
                public void onDoesntExist() {
                    listener.onDoesntExist();
                }

                @Override
                public void onFailure() {
                    listener.onFailure();
                }
            });
        } else {
            listener.onFailure();
        }
    }

    /*
     * Enables the user to change his username
     * @param newName new username provided by the user
     * @param listener actions to be done in case of failure or success
     */
/*    @Override
    public void changeName(String newName, final AuthListener listener) {
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(newName).build();

        user = auth.getCurrentUser();
        if (user != null) {
            user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        listener.onSuccess();
                    } else {
                        listener.onFailure();
                    }
                }
            });
        }else{
            listener.onFailure();
        }
    }
*/

    /**
     * Enables the user to change his primary email
     *
     * @param newEmail new email provided by the user
     * @param listener actions to be done in case of failure or success
     */
    @Override
    public void changeEmail(String newEmail, final AuthListener listener) {
        user = auth.getCurrentUser();
        if (user != null) {
            user.updateEmail(newEmail).
                    addOnCompleteListener(new OnCompleteListener<Void>() {
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
     * Enables the user to delete his account if he has signed in recently
     *
     * @param listener actions to be done in case of failure or success
     */
    @Override
    public void deleteAccount(final AuthListener listener, final DatabaseProvider.DeleteUserListener delListener) {
        user = auth.getCurrentUser();

        if (user != null) {
            System.out.println("Wain");
            user.delete()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                listener.onSuccess();
                                //delete user in our database
                                Providers.database.deleterUserById(user.getUid(), delListener);
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
     * @return true if user is email verified, false otherwise
     */
    @Override
    public Boolean isEmailVerified() {
        user = auth.getCurrentUser();
        return user != null && user.isEmailVerified();
    }

    /**
     * @return email of the current signed in user, null if there is not any user connected
     */
    @Override
    public String getEmail() {
        user = auth.getCurrentUser();
        if (auth.getCurrentUser() != null) {
            return user.getEmail();
        } else {
            return null;
        }

    }


}
