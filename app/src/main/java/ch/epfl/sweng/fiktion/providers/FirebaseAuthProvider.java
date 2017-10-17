package ch.epfl.sweng.fiktion.providers;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuth.AuthStateListener;
import com.google.firebase.auth.FirebaseUser;

import ch.epfl.sweng.fiktion.views.MainActivity;
import ch.epfl.sweng.fiktion.views.SignInActivity;
import ch.epfl.sweng.fiktion.views.UserDetailsActivity;

import static android.support.v4.content.ContextCompat.startActivity;

/**
 * Created by rodri on 17.10.2017.
 */

public class FirebaseAuthProvider extends AuthProvider {

    //testing
    public final static String TAG = "FBAuthProv";
    // firebase authentification instance
    private final FirebaseAuth fb = FirebaseAuth.getInstance();
    // firebase user that we authenticate
    private final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    // firebase status
    private FirebaseAuth.AuthStateListener state;

    /**
     * Set a StateListener to detect user account changes
     * @param act the current activity in which we want to detect an account change
     */
    public void createStateListener(final Activity act) {
        state = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser nUser = firebaseAuth.getCurrentUser();
                if (nUser != null) {
                    //user is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + nUser.getUid());
                    //if user changed, recreate
                    if (user != nUser) {
                        Intent i = new Intent(act, SignInActivity.class);
                        act.startActivity(i);
                    }
                } else {
                    //user is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    Intent i = new Intent(act, SignInActivity.class);
                    act.startActivity(i);
                }
                Toast.makeText(act, "You have been logged out unexpectedly. Please try again.", Toast.LENGTH_SHORT);
            }
        };
    }

    /**
     * Signs in a user with an email, a password and what to do afterwards
     *
     * @param email    user email
     * @param password user password
     * @param listener what to do after login
     */
    @Override
    public void signIn(String email, String password, AuthListener listener) {

    }

    /**
     * Signs the user out of the application
     */
    @Override
    public void signOut() {

    }

    /**
     * Return true if the email and password are valid, false otherwise.
     *
     * @param email
     * @param password
     * @return empty string if valid, error message otherwise
     */
    @Override
    public String validateEmailAndPassword(String email, String password) {
        return null;
    }

    /**
     * Creates a new account using the provided informations
     *
     * @param email    used to create the account
     * @param password used to create the account
     */
    @Override
    public void createUserWithEmailAndPassword(String email, String password) {

    }

    /**
     * Sends a password reset mail, defines what to do afterwards
     *
     * @param listener what to do after email attempt
     */
    @Override
    public void sendPasswordResetEmail(AuthListener listener) {

    }
}
