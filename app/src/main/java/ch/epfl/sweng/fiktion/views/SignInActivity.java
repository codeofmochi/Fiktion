package ch.epfl.sweng.fiktion.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import ch.epfl.sweng.fiktion.R;
import ch.epfl.sweng.fiktion.providers.AuthProvider;
import ch.epfl.sweng.fiktion.providers.AuthSingleton;

/**
 * This activity prompts a sign in or sign up if the user is not already connected
 * @author Rodrigo
 */
public class SignInActivity extends AppCompatActivity {

    private static final String TAG = "SignInLog";
    private EditText UserEmail;
    private EditText UserPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        Log.d(TAG, "Initialising sign in activity");

        //Initialise user content
        //Views
        UserEmail = (EditText) findViewById(R.id.User_Email);
        UserPassword = (EditText) findViewById(R.id.User_Password);
    }

    @Override
    public void onStart() {
        super.onStart();
        // If User is signed in we advance to the next activity, if User is null , UI will prompt a sign in
        updateUI(AuthSingleton.auth.isConnected());
    }


    /**
     * Signs the user in using firebase authentication
     *
     * @param email    provided by the user
     * @param password provided by te user
     */

    public void signIn(String email, String password) {
        //we need to check if the credentials are valid before attempting to sign in
        //first we check if the email is valid, do not proceed if it is not valid
        String emailErr = AuthSingleton.auth.validateEmail(email);
        if (!emailErr.isEmpty()) {

            //Log.d(TAG, "Email is not valid");
            //we set an error corresponding to the failure
            UserEmail.setError(emailErr);
            return;
        }

        //after making sure the email is valid we check if the password is valid and if not we do not proceed
        String passwordErr = AuthSingleton.auth.validatePassword(password);
        if (!passwordErr.isEmpty()) {

            //Log.d(TAG, "Password is not valid");
            //we set an error corresponding to the failure
            UserPassword.setError(passwordErr);
            return;
        }
        /*
        Log.d(TAG, "Credentials are valid");
        Log.d(TAG, "signIn:" + email);
        */
        AuthSingleton.auth.signIn(email, password, new AuthProvider.AuthListener() {
            @Override
            public void onSuccess() {
                //sign in was successful
                Log.d(TAG, "signIn successful");
                Toast.makeText(SignInActivity.this, "Login Successful!",
                        Toast.LENGTH_SHORT).show();
                updateUI(true);
            }

            @Override
            public void onFailure() {
                //sing in failed
                Log.d(TAG, "signIn failure");
                Toast.makeText(SignInActivity.this, R.string.toast_authentication_failed,
                        Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**
     * If User is signed in, user is taken to the settings
     *
     * @param isConnected true, if there is a user signed in, false otherwise
     */
    private void updateUI(Boolean isConnected) {
        //start details activity and end this one
        if (isConnected) {
            // intent to return to caller
            Intent i = new Intent();
            // send the intent to the parent
            setResult(RESULT_OK, i);
            // close this activity
            finish();
        }
    }


    //Methods are called by android and we have no use for the View v argument -> ignore waring
    //same for the other method with View v argument
    public void signIn(@SuppressWarnings("UnusedParameters") View v) {
        //user clicks on signin button
        signIn(UserEmail.getText().toString(), UserPassword.getText().toString());
    }

    public void register(@SuppressWarnings("UnusedParameters") View v) {
        //user clicks on register button
        //start registration activity
        Intent registerActivity = new Intent(this, RegisterActivity.class);
        startActivity(registerActivity);
    }


}
