package ch.epfl.sweng.fiktion.views;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
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

    private User user;

    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // pass layout to parent for menu
        includeLayout = R.layout.activity_settings;
        super.onCreate(savedInstanceState);

        userNewEmail = (EditText) findViewById(R.id.emailEdit);
        userNewName = (EditText) findViewById(R.id.usernameEdit);
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
                //TODO: decide what to do if user does nto exist in database
                user = null;
            }

            @Override
            public void onFailure() {
                //TODO: decide what to do if user fails to load from database or is not connected
                user = null;
            }
        });

        if (!AuthSingleton.auth.isEmailVerified()) {
            //TODO: modify button to verify email -> deactivate?
        }

    }

    public void updateEmail() {
        final String newEmail = userNewEmail.getText().toString();

        if (newEmail.isEmpty()) {
            //we only change email if the user has actually written something in the new email field
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
            Toast.makeText(this, "Please type a new and valid username", Toast.LENGTH_SHORT).show();
        }
    }

    public void savePersonalInfos(View v) {
        findViewById(R.id.saveAccountSettingsButton).setEnabled(false);
        if (user == null) {
            Toast.makeText(this, "You are not connected", Toast.LENGTH_SHORT).show();
            return;
        }
        updateEmail();
        updateUsername();
        findViewById(R.id.saveAccountSettingsButton).setEnabled(true);

    }


}
