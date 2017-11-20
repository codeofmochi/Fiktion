package ch.epfl.sweng.fiktion.views;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import ch.epfl.sweng.fiktion.R;
import ch.epfl.sweng.fiktion.models.User;
import ch.epfl.sweng.fiktion.providers.AuthProvider;
import ch.epfl.sweng.fiktion.providers.AuthSingleton;
import ch.epfl.sweng.fiktion.providers.DatabaseProvider;
import ch.epfl.sweng.fiktion.providers.DatabaseSingleton;
import ch.epfl.sweng.fiktion.views.parents.MenuDrawerActivity;

public class SettingsActivity extends MenuDrawerActivity {

    private EditText userNewName;
    private EditText userNewEmail;

    private Button saveSettingsButton;
    private Button verifyButton;
    private Button deleteButton;

    private User user;

    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // pass layout to parent for menu
        includeLayout = R.layout.activity_settings;
        super.onCreate(savedInstanceState);

        userNewEmail = (EditText) findViewById(R.id.emailEdit);
        userNewName = (EditText) findViewById(R.id.usernameEdit);

        saveSettingsButton = (Button) findViewById(R.id.saveAccountSettingsButton);
        verifyButton = (Button) findViewById(R.id.verifiedButton);
        deleteButton = (Button) findViewById(R.id.deleteAccountButton);
    }

    @Override
    public void onStart() {
        super.onStart();

        AuthSingleton.auth.getCurrentUser(DatabaseSingleton.database, new DatabaseProvider.GetUserListener() {
            @Override
            public void onSuccess(User currUser) {
                user = currUser;
                userNewName.setHint(user.getName());
                userNewEmail.setHint(AuthSingleton.auth.getEmail());
            }

            @Override
            public void onDoesntExist() {
                //TODO: decide what to do if user does not exist in database
                user = null;
                //Account settings disappear
                findViewById(R.id.accountSettingsTitle).setVisibility(View.GONE);
                findViewById(R.id.accountSettings).setVisibility(View.GONE);
                //Profile settings disappear
                findViewById(R.id.profileSettingsTitle).setVisibility(View.GONE);
                findViewById(R.id.profileSettings).setVisibility(View.GONE);
            }

            @Override
            public void onFailure() {
                //TODO: decide what to do if user fails to load from database or is not connected
                user = null;
                //Account settings disappear
                findViewById(R.id.accountSettingsTitle).setVisibility(View.GONE);
                findViewById(R.id.accountSettings).setVisibility(View.GONE);
                //Profile settings disappear
                findViewById(R.id.profileSettingsTitle).setVisibility(View.GONE);
                findViewById(R.id.profileSettings).setVisibility(View.GONE);
            }
        });

        if (AuthSingleton.auth.isEmailVerified()) {
            //TODO: modify button to verify email -> deactivate?
            //or set visibility gone to text and button
            verifyButton.setVisibility(View.GONE);
            findViewById(R.id.verifiedText).setVisibility(View.GONE);
        }

    }

    /**
     * Updates User's email
     */
    private void updateEmail() {
        final String newEmail = userNewEmail.getText().toString();

        if (newEmail.isEmpty()) {
            //we only change email if the user has actually written something in the new email field
            return;
        }

        if (newEmail.equals(AuthSingleton.auth.getEmail())) {
            userNewEmail.setError("Please type a new and valid email");
            return;
        }

        String errMessage = AuthSingleton.auth.validateEmail(newEmail);

        //validate name choice
        if (errMessage.isEmpty()) {
            AuthSingleton.auth.changeEmail(newEmail, new AuthProvider.AuthListener() {
                @Override
                public void onSuccess() {
                    userNewEmail.setHint(AuthSingleton.auth.getEmail());
                    Toast.makeText(context,
                            "User's email is now : " + newEmail,
                            Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure() {
                    Toast.makeText(context,
                            "Failed to update User's email. You may need to re-authenticate",
                            Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, errMessage, Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * Updates User's username
     */
    public void updateUsername() {
        final String newUsername = userNewName.getText().toString();
        if (newUsername.isEmpty()) {
            //we only change username if the user has actually written something in the new username field
            return;
        }

        //validate name choice
        if (!newUsername.equals(user.getName())) {

            user.changeName(DatabaseSingleton.database, newUsername, new AuthProvider.AuthListener() {
                @Override
                public void onSuccess() {
                    userNewName.setHint(user.getName());
                    userNewName.setError(null);
                    Toast.makeText(context,
                            "Username updated to : " + newUsername,
                            Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure() {
                    Toast.makeText(context,
                            "Failed to update User's name.",
                            Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            userNewName.setError("Please type a new and valid username");
        }
    }

    /**
     * Updates User's profile with new information retrieved in activity's EditText fields
     */
    public void savePersonalInfos(@SuppressWarnings("UnusedParameters") View v) {
        //reset errors
        userNewEmail.setError(null);
        userNewName.setError(null);
        //start update
        saveSettingsButton.setEnabled(false);
        if (user == null) {
            Toast.makeText(this, "You are not signed in", Toast.LENGTH_SHORT).show();
            return;
        }
        updateUsername();
        updateEmail();
        saveSettingsButton.setEnabled(true);
    }

    /**
     * Sends a verification email to the User's current email
     */
    public void sendEmailVerification(@SuppressWarnings("UnusedParameters") View v) {
        // Send verification email only if user does not have a verified email
        verifyButton.setEnabled(false);

        if (AuthSingleton.auth.isConnected()) {
            AuthSingleton.auth.sendEmailVerification(new AuthProvider.AuthListener() {
                @Override
                public void onSuccess() {
                    Toast.makeText(context,
                            "Verification email sent",
                            Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure() {
                    Toast.makeText(context,
                            "Failed to send verification email.",
                            Toast.LENGTH_SHORT).show();

                }
            });
        } else {
            //handles the case if user is not currently signed right after calling this method
            Toast.makeText(context, "You are not signed in", Toast.LENGTH_SHORT).show();
            //TODO maybe start a sign in activity
        }
        verifyButton.setEnabled(true);
    }


    private void goHome() {
        Intent home = new Intent(this, HomeActivity.class);
        startActivity(home);
        this.finish();
    }

    public void clickDeleteAccount(View v) {
        // Instantiate an AlertDialog.Builder with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        // Chain together various setter methods to set the dialog characteristics
        builder.setMessage("You are about to permanently delete your account, do you wish to continue?")
                .setTitle("Fiktion")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteAccount();
                        dialog.dismiss();
                    }
                });
        // Get the AlertDialog from create()
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED);
    }

    private void deleteAccount() {
        if (AuthSingleton.auth.isConnected()) {

            AuthSingleton.auth.deleteAccount(
                    //first we delete firebase account
                    new AuthProvider.AuthListener() {
                        @Override
                        public void onSuccess() {
                            //we do not do anything,
                            // we need to wait that the user is correctly deleted from database
                        }

                        @Override
                        public void onFailure() {
                            deleteButton.setEnabled(true);
                            Toast.makeText(context,
                                    "You did not sign in recently, please re-authenticate and try again", Toast.LENGTH_LONG).show();
                        }
                    },
                    //then we deleted database User
                    new DatabaseProvider.DeleteUserListener() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(context,
                                    "User account deleted successfully",
                                    Toast.LENGTH_SHORT).show();
                            goHome();
                        }

                        @Override
                        public void onDoesntExist() {
                            //account was deleted in firebase previously and does not exist in database = deleted
                            Toast.makeText(context,
                                    "User account deleted successfully",
                                    Toast.LENGTH_SHORT).show();
                            goHome();
                        }

                        @Override
                        public void onFailure() {
                            Toast.makeText(context,
                                    "There was a problem deleting your account, signing you out",
                                    Toast.LENGTH_SHORT).show();
                            AuthSingleton.auth.signOut();
                            goHome();
                        }
                    });

        } else {
            deleteButton.setEnabled(true);
            Toast.makeText(context,
                    "No user currently signed in", Toast.LENGTH_LONG).show();
            this.recreate();
        }
    }

    public void clickSignOut(View v){
        AuthSingleton.auth.signOut();
        goHome();
    }
}
