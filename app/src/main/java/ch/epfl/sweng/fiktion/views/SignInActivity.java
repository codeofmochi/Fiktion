package ch.epfl.sweng.fiktion.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ch.epfl.sweng.fiktion.R;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {


    private static final String TAG = "SignIn";
    private EditText UserEmail;
    private EditText UserPassword;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        Log.d(TAG, "Initialising sign in activity");

        //Initialise user content

        //Views
        UserEmail = (EditText) findViewById(R.id.User_Email);
        UserPassword = (EditText) findViewById(R.id.User_Password);

        //Firebase Authenticator
        mAuth = FirebaseAuth.getInstance();

        //TODO: store logged in/out state in the app
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    //user is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    //user is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };

        // If User is signed in we advance to the next activity, if User is null , UI will prompt a sign in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        mAuth.addAuthStateListener(mAuthListener);
        updateUI(currentUser);

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    /**
     * This method checks if the credentials are valid by firebase standards
     *
     * @return true is the credentials are valid, false otherwise
     */
    private boolean validateCredentials() {
        boolean validEmail = false;
        boolean validPassword = false;
        String email = UserEmail.getText().toString();
        String password = UserPassword.getText().toString();
        Log.d(TAG, "Validating credentials");

        if (password.isEmpty()) {
            UserPassword.setError(getString(R.string.required_password_error));
            Log.d(TAG, "Password validation failed");
        } else {
            //TODO elaborate password validation
            if (password.length() >= 6) {
                validPassword = true;
                UserPassword.setError(null);
            } else {
                UserPassword.setError(getString(R.string.invalid_password_error));
                Log.d(TAG, "Password validation failed");
            }
        }
        //TODO elaborate email validation
        if (email.contains("@")) {
            validEmail = true;
            UserEmail.setError(null);
        } else {
            UserEmail.setError(getString(R.string.invalid_email_error));
            Log.d(TAG, "Email validation failed");

        }


        return validEmail && validPassword;
    }

    /**
     * Signs the user in using firebase authentication
     *
     * @param email    provided by the user
     * @param password provided by te user
     */
    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        //we need to check if the credentials are valid before attempting to sign in
        if (!validateCredentials()) {
            Log.d(TAG, "Not valid credentials");
            return;
        }
        Log.d(TAG, "Credentials are valid");
        Log.d(TAG, "signIn:" + email);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //reset textViews content
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(SignInActivity.this, "Login Successful!",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(user);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(SignInActivity.this, R.string.toast_authentication_failed,
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    /**
     * If User is signed in, user is taken to the user details screen
     *
     * @param user firebase user
     */
    private void updateUI(FirebaseUser user) {
        if (user != null) {
            //start details activity and end this one
            Intent user_details_activity = new Intent(this, UserDetailsActivity.class);
            startActivity(user_details_activity);
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        //user clicks on signin button
        if (i == R.id.SignInButton) {
            signIn(UserEmail.getText().toString(), UserPassword.getText().toString());
        }
        //user clicks on register button
        else if (i == R.id.RegisterButton) {
            //start registration activity
            Intent registerActivity = new Intent(this, RegisterActivity.class);
            startActivityForResult(registerActivity, 1);
        }
    }


}