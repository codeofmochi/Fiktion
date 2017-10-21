package ch.epfl.sweng.fiktion.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import ch.epfl.sweng.fiktion.R;
import ch.epfl.sweng.fiktion.models.User;
import ch.epfl.sweng.fiktion.providers.AuthProvider;
import ch.epfl.sweng.fiktion.providers.Providers;

public class UserDetailsActivity extends AppCompatActivity {

    //constants
    //LOGCAT
    private static final String TAG = "UserDetails";

    //UI modes
    private enum UIMode {
        defaultMode,
        userSignedOut
    }

    //Authenticator initiation

    private final AuthProvider auth = Providers.auth;
    private User user;

    //views
    private TextView user_name_view;
    private TextView user_email_view;
    private TextView user_verify_view;
    private EditText user_newName;
    private EditText user_newEmail;
    //Buttons
    private Button confirmName;
    private Button pwReset;
    //Strings
    private String email;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        Log.d(TAG, "Initialising User Details activity");

        user = auth.getCurrentUser();


        //initialise views
        user_name_view = (TextView) findViewById(R.id.detail_user_name);
        user_email_view = (TextView) findViewById(R.id.detail_user_email);
        user_verify_view = (TextView) findViewById(R.id.detail_user_verify);
        user_newName = (EditText) findViewById(R.id.detail_new_name);
        user_newEmail = (EditText) findViewById(R.id.detail_new_email);

        //initialise button
        confirmName = (Button) findViewById(R.id.detail_confirm_name);
        pwReset = (Button) findViewById(R.id.detail_reset_password);

    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "Started UserDetailsActivity");
        //initialise user details and firebase authentication

        if (auth.isConnected()) {
            // Name, email address, and profile photo Url
            user = auth.getCurrentUser();
            name = user.getName();
            email = user.getEmail();
            if(user.isEmailVerified()){
                user_verify_view.setText("Email is verified");
            } else{
                user_verify_view.setText("Email is not verified");
            }

            //Uri photoUrl = user.getPhotoUrl();
            //String uid = user.getID();
            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getToken() instead. [I will keep this advice for now]
            updateUI(UIMode.defaultMode);
        } else {
            //this case will probably never happen
            Log.d(TAG, "Could not initialise user details, user is not signed in");
        }

    }

    /**
     * This method signs the user out from Fiktion
     */
    private void signOut() {

        if (auth.isConnected()) {
            auth.signOut();
            //firebase authentication listener will see
            // that user signed out and call onAuthStateChanged
            updateUI(UIMode.userSignedOut);
            Log.d(TAG, "User is signed out");
        }
    }

    /**
     * This method will send a verification email to the currently signed in user
     */
    private void sendEmailVerification() {
        // Disable button
        findViewById(R.id.verification_button).setEnabled(false);

        // Send verification email only if user does not have a verified email
        if(user.isEmailVerified()){
            Toast.makeText(this,"User's email is verified",Toast.LENGTH_SHORT).show();
            return;
        }

        if (auth.isConnected()) {
            auth.sendEmailVerification(new AuthProvider.AuthListener() {
                @Override
                public void onSuccess() {
                    Log.d(TAG, "Sending was successful");
                    Toast.makeText(UserDetailsActivity.this,
                            "Verification email sent",
                            Toast.LENGTH_SHORT).show();
                    findViewById(R.id.verification_button).setEnabled(true);

                }

                @Override
                public void onFailure() {
                    Log.e(TAG, "sendEmailVerification failed");
                    Toast.makeText(UserDetailsActivity.this,
                            "Failed to send verification email.",
                            Toast.LENGTH_SHORT).show();
                    findViewById(R.id.verification_button).setEnabled(true);

                }
            });
        } else {
            //handles the case if user is not currently signed right after calling this method
            Toast.makeText(UserDetailsActivity.this, "No User currently signed in", Toast.LENGTH_SHORT).show();
            findViewById(R.id.verification_button).setEnabled(true);
        }

    }

    private void sendPasswordResetEmail() {
        // Disable button
        findViewById(R.id.detail_reset_password).setEnabled(false);

        if (auth.isConnected()) {
            auth.sendPasswordResetEmail(new AuthProvider.AuthListener() {
                @Override
                public void onSuccess() {
                    findViewById(R.id.detail_reset_password).setEnabled(true);

                    Log.d(TAG, "Sending was successful");
                    Toast.makeText(UserDetailsActivity.this,
                            "Password reset email sent",
                            Toast.LENGTH_SHORT).show();
                    pwReset.setVisibility(View.GONE);
                }

                @Override
                public void onFailure() {
                    findViewById(R.id.detail_reset_password).setEnabled(true);
                    Log.e(TAG, "sendPasswordResetEmail failed");
                    Toast.makeText(UserDetailsActivity.this,
                            "Failed to send password reset email.",
                            Toast.LENGTH_SHORT).show();

                }
            });
        } else {
            //handles the case if user is not currently signed right after calling this method
            Toast.makeText(UserDetailsActivity.this, "No User currently signed in", Toast.LENGTH_SHORT).show();
            // Re-enable button
            findViewById(R.id.detail_reset_password).setEnabled(true);
        }


    }

    private void updateUI(UIMode mode) {
        if (mode.equals(UIMode.defaultMode)) {
            //UI default mode
            //initialise views and buttons
            user_name_view.setText(name);
            user_email_view.setText(email);
            user_newName.setVisibility(View.VISIBLE);
            confirmName.setVisibility(View.VISIBLE);
            if(user.isEmailVerified()){
                pwReset.setVisibility(View.VISIBLE);
            } else{
                pwReset.setVisibility(View.GONE);
            }
        } else if (mode.equals(UIMode.userSignedOut)) {
            Log.d(TAG, "Return to signIn activity");
            Intent login = new Intent(this, SignInActivity.class);
            finish();
            startActivity(login);
        }
    }


    private void confirmName() {
        final String newName = user_newName.getText().toString();
        findViewById(R.id.detail_confirm_name).setEnabled(false);

        //validate name choice
        if (!newName.isEmpty()
                && !newName.equals(user.getName())
                && newName.length() <= 15) {

            user.changeName(newName, new AuthProvider.AuthListener() {
                @Override
                public void onSuccess() {
                    user_name_view.setText(newName);
                    recreate();
                    Toast.makeText(UserDetailsActivity.this,
                            "User's name is now : " + newName,
                            Toast.LENGTH_LONG).show();
                    user_newName.getText().clear();
                }

                @Override
                public void onFailure() {
                    findViewById(R.id.detail_confirm_name).setEnabled(true);
                    Toast.makeText(UserDetailsActivity.this,
                            "Failed to update User's name.",
                            Toast.LENGTH_SHORT).show();
                }
            });
        } else{
            findViewById(R.id.detail_confirm_name).setEnabled(true);
            Toast.makeText(this, "Please type a new username", Toast.LENGTH_SHORT).show();
        }

    }

    private void confirmEmail() {
        final String newEmail = user_newName.getText().toString();
        findViewById(R.id.detail_confirm_email).setEnabled(false);

        //validate name choice
        if (!newEmail.isEmpty()
                && !newEmail.equals(user.getEmail())) {

            user.changeEmail(newEmail, new AuthProvider.AuthListener() {
                @Override
                public void onSuccess() {
                    user_email_view.setText(newEmail);
                    recreate();
                    Toast.makeText(UserDetailsActivity.this,
                            "User's email is now : " + newEmail,
                            Toast.LENGTH_LONG).show();
                    user_newName.getText().clear();
                }

                @Override
                public void onFailure() {
                    findViewById(R.id.detail_confirm_email).setEnabled(true);
                    Toast.makeText(UserDetailsActivity.this,
                            "Failed to update User's email. You may need to re-authenticate",
                            Toast.LENGTH_SHORT).show();
                }
            });
        } else{
            findViewById(R.id.detail_confirm_email).setEnabled(true);
            Toast.makeText(this, "Please type a new email", Toast.LENGTH_SHORT).show();
        }

    }

    public void clickSendEmailVerification(@SuppressWarnings("UnusedParameters") View v) {
        Log.d(TAG, "Sending Email Verification");
        sendEmailVerification();
    }

    public void clickSignOut(@SuppressWarnings("UnusedParameters") View v) {
        Log.d(TAG, "Signing Out");
        signOut();
    }

    public void clickSendPasswordReset(@SuppressWarnings("UnusedParameters") View v) {
        Log.d(TAG, "Sending password reset email");
        sendPasswordResetEmail();
    }

    public void clickConfirmNameChange(@SuppressWarnings("UnusedParameters") View v){
        confirmName();
    }

    public void clickConfirmEmailChange(@SuppressWarnings("UnusedParameters") View v){
        confirmEmail();
    }
}
