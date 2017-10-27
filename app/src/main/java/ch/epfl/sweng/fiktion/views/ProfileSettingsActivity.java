package ch.epfl.sweng.fiktion.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import ch.epfl.sweng.fiktion.R;
import ch.epfl.sweng.fiktion.models.User;
import ch.epfl.sweng.fiktion.providers.AuthProvider;
import ch.epfl.sweng.fiktion.providers.Providers;

public class ProfileSettingsActivity extends AppCompatActivity {

    private final String TAG = "UpdateProfile";

    //auth
    private final AuthProvider auth = Providers.auth;
    private User user;

    //TextViews
    private EditText user_newName;
    private EditText user_newEmail;

    //Buttons
    private Button pwReset;
    private Button deleteAccount;
    private Button emailVerification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);

        //initialise variables
        //views
        user_newName = (EditText) findViewById(R.id.update_new_name);
        user_newEmail = (EditText) findViewById(R.id.update_new_email);
        //buttons
        pwReset = (Button) findViewById(R.id.update_reset_password);
        deleteAccount = (Button) findViewById(R.id.update_delete_account);
        emailVerification = (Button) findViewById(R.id.update_email_verification);

    }

    @Override
    public void onStart() {
        super.onStart();
        user = auth.getCurrentUser();
    }

    /**
     * This method will send a verification email to the currently signed in user
     */
    private void sendEmailVerification() {
        // Disable button
        emailVerification.setEnabled(false);

        // Send verification email only if user does not have a verified email

        if (auth.isConnected()) {
            // Send verification email only if user does not have a verified email
            user = auth.getCurrentUser();
            if (user.isEmailVerified()) {
                emailVerification.setEnabled(true);
                Toast.makeText(this, "User's email is verified", Toast.LENGTH_SHORT).show();
                return;
            }
            auth.sendEmailVerification(new AuthProvider.AuthListener() {
                @Override
                public void onSuccess() {
                    Log.d(TAG, "Sending was successful");
                    Toast.makeText(ProfileSettingsActivity.this,
                            "Verification email sent",
                            Toast.LENGTH_SHORT).show();
                    emailVerification.setEnabled(true);

                }

                @Override
                public void onFailure() {
                    Log.e(TAG, "sendEmailVerification failed");
                    Toast.makeText(ProfileSettingsActivity.this,
                            "Failed to send verification email.",
                            Toast.LENGTH_SHORT).show();
                    emailVerification.setEnabled(true);

                }
            });
        } else {
            //handles the case if user is not currently signed right after calling this method
            Toast.makeText(ProfileSettingsActivity.this, "Sign in state changed, signing out", Toast.LENGTH_SHORT).show();
            emailVerification.setEnabled(true);
            //TODO maybe start a sign in activity
        }

    }

    /**
     * This method will send a request to change the current user's username
     */
    private void confirmName() {
        final String newName = user_newName.getText().toString();
        findViewById(R.id.update_confirm_name).setEnabled(false);

        //validate name choice
        if (!newName.isEmpty()
                && !newName.equals(user.getName())
                && newName.length() <= 15) {

            user.changeName(newName, new AuthProvider.AuthListener() {
                @Override
                public void onSuccess() {
                    findViewById(R.id.update_confirm_name).setEnabled(true);
                    Toast.makeText(ProfileSettingsActivity.this,
                            "User's name is now : " + newName,
                            Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure() {
                    findViewById(R.id.update_confirm_name).setEnabled(true);
                    Toast.makeText(ProfileSettingsActivity.this,
                            "Failed to update User's name.",
                            Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            findViewById(R.id.update_confirm_name).setEnabled(true);
            Toast.makeText(this, "Please type a new username", Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * This method will send a request to change the current user's email address.
     * It will fail if the user has not signed in recently
     */
    private void confirmEmail() {
        final String newEmail = user_newEmail.getText().toString();
        findViewById(R.id.update_confirm_email).setEnabled(false);

        //validate name choice
        if (!newEmail.isEmpty()
                && !newEmail.equals(user.getEmail())) {

            user.changeEmail(newEmail, new AuthProvider.AuthListener() {
                @Override
                public void onSuccess() {
                    findViewById(R.id.update_confirm_email).setEnabled(true);
                    Toast.makeText(ProfileSettingsActivity.this,
                            "User's email is now : " + newEmail,
                            Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure() {
                    findViewById(R.id.update_confirm_email).setEnabled(true);
                    Toast.makeText(ProfileSettingsActivity.this,
                            "Failed to update User's email. You may need to re-authenticate",
                            Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            findViewById(R.id.update_confirm_email).setEnabled(true);
            Toast.makeText(this, "Please type a new email", Toast.LENGTH_SHORT).show();
        }
        this.onRestart();

    }

    /**
     * this method will send a password reset email to the currently signed in user
     */
    private void sendPasswordResetEmail() {
        // Disable button
        pwReset.setEnabled(false);

        if (auth.isConnected()) {
            auth.sendPasswordResetEmail(new AuthProvider.AuthListener() {
                @Override
                public void onSuccess() {
                    pwReset.setEnabled(true);
                    Log.d(TAG, "Sending was successful");
                    Toast.makeText(ProfileSettingsActivity.this,
                            "Password reset email sent",
                            Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure() {
                    pwReset.setEnabled(true);
                    Log.e(TAG, "sendPasswordResetEmail failed");
                    Toast.makeText(ProfileSettingsActivity.this,
                            "Failed to send password reset email.",
                            Toast.LENGTH_SHORT).show();

                }
            });
        } else {
            //handles the case if user is not currently signed right after calling this method
            Toast.makeText(ProfileSettingsActivity.this, "No User currently signed in", Toast.LENGTH_SHORT).show();
            // Re-enable button
            pwReset.setEnabled(true);
        }


    }

    /**
     * This method will delete the user's account if he is recently signed in, fail otherwise
     */
    private void deleteAccount() {
        deleteAccount.setEnabled(false);

        if (auth.isConnected()) {

            auth.deleteAccount(new AuthProvider.AuthListener() {
                @Override
                public void onSuccess() {
                    Toast.makeText(ProfileSettingsActivity.this,
                            "Account deleted successfully", Toast.LENGTH_SHORT).show();
                    deleteAccount.setEnabled(true);
                    goHome();
                }

                @Override
                public void onFailure() {
                    deleteAccount.setEnabled(true);
                    Toast.makeText(ProfileSettingsActivity.this,
                            "You did not sign in recently, please re-authenticate and try again", Toast.LENGTH_LONG).show();
                }
            });

        } else {
            deleteAccount.setEnabled(true);
            Toast.makeText(ProfileSettingsActivity.this,
                    "No user currently signed in", Toast.LENGTH_LONG).show();
            //TODO maybe start a signin activity
        }
    }

    private void goHome(){
        Intent homeActivity = new Intent(this, HomeActivity.class);
        startActivity(homeActivity);
        finish();
    }
    /**
     * Starts the email verification request
     */
    //Methods are called by android and we have no use for the View v argument -> ignore waring
    //same for the other click*(View v) methods
    public void clickSendEmailVerification(@SuppressWarnings("UnusedParameters") View v) {
        Log.d(TAG, "Sending Email Verification");
        sendEmailVerification();
    }

    /**
     * Start the name change request
     */
    public void clickConfirmNameChange(@SuppressWarnings("UnusedParameters") View v) {
        confirmName();
    }

    /**
     * Start the email change request
     */
    public void clickConfirmEmailChange(@SuppressWarnings("UnusedParameters") View v) {
        confirmEmail();
    }

    /**
     * Start the password reset email request
     */
    public void clickSendPasswordReset(@SuppressWarnings("UnusedParameters") View v) {
        Log.d(TAG, "Sending password reset email");
        sendPasswordResetEmail();
    }

    /**
     * Start the delete account request
     */
    public void clickDeleteAccount(@SuppressWarnings("UnusedParameters") View v) {
        deleteAccount();
    }

}
