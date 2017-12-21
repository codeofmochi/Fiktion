package ch.epfl.sweng.fiktion.views;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.LocalDate;

import java.util.Calendar;

import ch.epfl.sweng.fiktion.R;
import ch.epfl.sweng.fiktion.models.PersonalUserInfos;
import ch.epfl.sweng.fiktion.models.Settings;
import ch.epfl.sweng.fiktion.models.User;
import ch.epfl.sweng.fiktion.providers.AuthProvider;
import ch.epfl.sweng.fiktion.providers.DatabaseProvider;
import ch.epfl.sweng.fiktion.utils.Config;
import ch.epfl.sweng.fiktion.views.parents.MenuDrawerActivity;
import ch.epfl.sweng.fiktion.views.utils.ActivityCodes;

public class SettingsActivity extends MenuDrawerActivity {

    private Activity ctx = this;
    private EditText userNewName;
    private EditText userNewEmail;

    private Button saveSettingsButton;
    private Button saveProfileSettingsButton;
    private Button verifyButton;
    private Button deleteButton;
    private Button signOutButton;
    private Button resetButton;

    // profile infos
    private PersonalUserInfos userPersonalInfos;
    private static LocalDate birthday;
    private Switch profilePublicSwitch;
    private EditText firstnameEdit;
    private EditText lastnameEdit;
    private EditText countryEdit;
    private static TextView birthdayText;
    private Button birthdayPickerButton;

    // notifications
    private Switch randomNotif;

    private SeekBar radiusSlider;
    private TextView radiusValue;

    private User user;
    private Settings settings;

    private AuthProvider auth = AuthProvider.getInstance();

    private Context context = this;

    /**
     * Date picker for birthday
     */
    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            //month needs to be incrementd because of API
            birthday = new LocalDate(year, month, day);
            birthdayText.setText(day + "/" + (month + 1) + "/" + year);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // pass layout to parent for menu
        includeLayout = R.layout.activity_settings;
        super.onCreate(savedInstanceState);

        userNewEmail = (EditText) findViewById(R.id.emailEdit);
        userNewName = (EditText) findViewById(R.id.usernameEdit);

        saveProfileSettingsButton = (Button) findViewById(R.id.saveProfileSettingsButton);
        saveSettingsButton = (Button) findViewById(R.id.saveAccountSettingsButton);
        verifyButton = (Button) findViewById(R.id.verifiedButton);
        deleteButton = (Button) findViewById(R.id.deleteAccountButton);
        signOutButton = (Button) findViewById(R.id.signOutButton);
        resetButton = (Button) findViewById(R.id.passwordReset);
        // get date picker
        birthdayPickerButton = (Button) findViewById(R.id.birthdayButton);
        birthdayPickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getFragmentManager(), "datePicker");
            }
        });

        //search radius slider and text set up

        radiusValue = (TextView) findViewById(R.id.searchRadiusNum);
        radiusValue.setText(String.valueOf(Config.settings.getSearchRadius()));
        radiusSlider = (SeekBar) findViewById(R.id.searchRadiusSlider);
        radiusSlider.setMax(199);
        radiusSlider.setProgress(Config.settings.getSearchRadius() - 1);

        radiusSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int radius = progress + 1;
                radiusValue.setText(String.valueOf(radius));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Config.settings = new Settings(Integer.parseInt(radiusValue.getText().toString()));
            }
        });


        // find profile infos fields
        profilePublicSwitch = (Switch) findViewById(R.id.publicProfileSwitch);
        firstnameEdit = (EditText) findViewById(R.id.firstNameEdit);
        lastnameEdit = (EditText) findViewById(R.id.lastNameEdit);
        countryEdit = (EditText) findViewById(R.id.countryEdit);
        birthdayText = (TextView) findViewById(R.id.birthdayDisplay);

        // profile is public setting
        profilePublicSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(final CompoundButton buttonView, boolean isChecked) {
                user.changeProfilePrivacy(isChecked, new AuthProvider.AuthListener() {
                    @Override
                    public void onFailure() {
                        Toast.makeText(ctx, R.string.request_failed, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onSuccess() {
                        Toast.makeText(ctx, R.string.privacy_updated, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        // notifs
        randomNotif = (Switch) findViewById(R.id.someNotificationSwitch);
        randomNotif.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    int notifId = 0;
                    NotificationCompat.Builder notifBuilder =
                            new NotificationCompat.Builder(context)
                                    .setAutoCancel(true)
                                    .setSmallIcon(R.mipmap.ic_launcher)
                                    .setContentTitle("New notification from Fiktion")
                                    .setContentText("Congratz. Happy now?")
                                    .setVibrate(new long[]{500, 500})
                                    .setSound(android.provider.Settings.System.DEFAULT_NOTIFICATION_URI);
                    NotificationManager notifManager =
                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    if (notifManager != null) {
                        notifManager.notify(notifId, notifBuilder.build());
                    }
                }
            }
        });
        setButtons(false);

    }

    @Override
    public void onStart() {
        super.onStart();

        auth.getCurrentUser(new DatabaseProvider.GetUserListener() {
            @Override
            public void onNewValue(User currUser) {
                user = currUser;
                userPersonalInfos = user.getPersonalUserInfos();
                setButtons(true);
                // set current values as hints
                userNewName.setHint(user.getName());
                userNewEmail.setHint(auth.getEmail());
                firstnameEdit.setHint(userPersonalInfos.getFirstName());
                lastnameEdit.setHint(userPersonalInfos.getLastName());
                countryEdit.setHint(userPersonalInfos.getCountry());
                LocalDate userBirthday = userPersonalInfos.getBirthday();
                birthdayText.setText(userBirthday.getDayOfMonth() + "/" + (userBirthday.getMonthOfYear() + 1) + "/" + userBirthday.getYear());
                settings = user.getSettings();
                int progress = settings.getSearchRadius();
                radiusValue.setText(String.valueOf(progress));
                radiusSlider.setProgress(progress - 1);
                radiusSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        int radius = progress + 1;
                        radiusValue.setText(String.valueOf(radius));
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                        user.updateSettingsRadius(Integer.parseInt(radiusValue.getText().toString()), new DatabaseProvider.ModifyUserListener() {
                            @Override
                            public void onSuccess() {
                                Config.settings = settings;
                            }

                            @Override
                            public void onDoesntExist() {
                                Toast.makeText(ctx, "Database Exception : user missing", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure() {
                                Toast.makeText(ctx, "Failed to updated search radius value", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }

            @Override
            public void onModifiedValue(User user) {

            }

            @Override
            public void onDoesntExist() {
                user = null;
                //Account settings disappear
                findViewById(R.id.accountLoginButton).setVisibility(View.VISIBLE);
                findViewById(R.id.accountSettings).setVisibility(View.GONE);
                //Profile settings disappear
                findViewById(R.id.profileSettingsTitle).setVisibility(View.GONE);
                findViewById(R.id.profileSettings).setVisibility(View.GONE);
            }

            @Override
            public void onFailure() {
                user = null;
                //Account settings disappear
                findViewById(R.id.accountLoginButton).setVisibility(View.VISIBLE);
                findViewById(R.id.accountSettings).setVisibility(View.GONE);
                //Profile settings disappear
                findViewById(R.id.profileSettingsTitle).setVisibility(View.GONE);
                findViewById(R.id.profileSettings).setVisibility(View.GONE);
            }
        });

        if (auth.isEmailVerified()) {
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

        if (newEmail.equals(auth.getEmail())) {
            userNewEmail.setError("Please type a new and valid email");
            return;
        }

        String errMessage = auth.validateEmail(newEmail);

        //validate name choice
        if (errMessage.isEmpty()) {
            auth.changeEmail(newEmail, new AuthProvider.AuthListener() {
                @Override
                public void onSuccess() {
                    userNewEmail.setHint(auth.getEmail());
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
    private void updateUsername() {
        final String newUsername = userNewName.getText().toString();
        if (newUsername.isEmpty()) {
            //we only change username if the user has actually written something in the new username field
            return;
        }

        //validate name choice
        if (!newUsername.equals(user.getName())) {

            user.changeName(newUsername, new DatabaseProvider.ModifyUserListener() {
                @Override
                public void onSuccess() {
                    userNewName.setHint(user.getName());
                    userNewName.setError(null);
                    Toast.makeText(context,
                            "Username updated to : " + newUsername,
                            Toast.LENGTH_SHORT).show();
                }


                @Override
                public void onDoesntExist() {
                    Toast.makeText(context,
                            "User no longer exists in database",
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

    private void setButtons(boolean enabled) {
        deleteButton.setEnabled(enabled);
        resetButton.setEnabled(enabled);
        saveSettingsButton.setEnabled(enabled);
        saveProfileSettingsButton.setEnabled(enabled);
        verifyButton.setEnabled(enabled);
        signOutButton.setEnabled(enabled);
        birthdayPickerButton.setEnabled(enabled);
        profilePublicSwitch.setEnabled(enabled);
    }

    /**
     * Finishes this activity and takes the user to home activity
     */
    private void goHome() {
        Intent home = new Intent(this, HomeActivity.class);
        home.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(home);
        this.finish();
    }

    /**
     * Attempts to delete user's account
     */
    private void deleteAccount() {
        if (auth.isConnected()) {

            auth.deleteAccount(
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
                            auth.signOut();
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

    /**
     * Updates User's profile with new information retrieved in activity's EditText fields
     */
    public void savePersonalInfos(@SuppressWarnings("UnusedParameters") View v) {
        //reset errors
        userNewEmail.setError(null);
        userNewName.setError(null);
        //start update

        saveSettingsButton.setEnabled(false);
        if (!auth.isConnected()) {
            Toast.makeText(this, "You are not signed in", Toast.LENGTH_SHORT).show();
            recreate();
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

        if (auth.isConnected()) {
            auth.sendEmailVerification(new AuthProvider.AuthListener() {
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
            recreate();
        }
        verifyButton.setEnabled(true);
    }

    /**
     * Starts a dialog the confirms if the user really wants to delete his account
     */
    public void clickDeleteAccount(@SuppressWarnings("UnusedParameters") View v) {
        // Instantiate an AlertDialog.Builder with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        // Sets up the dialog builder
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
        // Get the dialog that confirms if user wants to permanently delete his account
        AlertDialog deleteDialog;
        deleteDialog = builder.create();
        deleteDialog.show();
        deleteDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
        deleteDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED);
    }

    /**
     * Signs the user out
     */
    public void clickSignOut(@SuppressWarnings("UnusedParameters") View v) {
        auth.signOut();
        goHome();
    }

    /**
     * Sends a reset password email to the user's current email
     */
    public void clickResetPassword(@SuppressWarnings("UnusedParameters") View v) {
        // Disable button
        resetButton.setEnabled(false);

        if (auth.isConnected()) {
            auth.sendPasswordResetEmail(new AuthProvider.AuthListener() {
                @Override
                public void onSuccess() {
                    Toast.makeText(context,
                            "Password reset email sent",
                            Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure() {
                    Toast.makeText(context,
                            "Failed to send password reset email.",
                            Toast.LENGTH_SHORT).show();

                }
            });
        } else {
            //handles the case if user is not currently signed right after calling this method
            Toast.makeText(context, "You are not signed in", Toast.LENGTH_SHORT).show();
            recreate();
        }
        resetButton.setEnabled(true);
    }

    /**
     * Triggered by save profile button
     */
    public void saveProfile(View view) {
        PersonalUserInfos oldValues = userPersonalInfos;
        String inputFirstName = firstnameEdit.getText().toString();
        String inputLastName = lastnameEdit.getText().toString();
        String inputCountry = countryEdit.getText().toString();
        String newFirstName = inputFirstName.isEmpty() ? oldValues.getFirstName() : inputFirstName;
        String newLastName = inputLastName.isEmpty() ? oldValues.getLastName() : inputLastName;
        String newCountry = inputCountry.isEmpty() ? oldValues.getCountry() : inputCountry;

        PersonalUserInfos newValues = new PersonalUserInfos(birthday, newFirstName, newLastName, newCountry);
        user.updatePersonalInfos(newValues, new DatabaseProvider.ModifyUserListener() {
            @Override
            public void onDoesntExist() {
                Toast.makeText(ctx, "Data not found", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure() {
                Toast.makeText(ctx, "Failed to update profile", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess() {
                Toast.makeText(ctx, "Profile updates successfully!", Toast.LENGTH_SHORT).show();
            }
        });
    }


    /**
     * Triggered by login button click
     *
     * @param view The caller view
     */
    public void redirectToLogin(View view) {
        Intent i = new Intent(this, SignInActivity.class);
        startActivityForResult(i, ActivityCodes.SIGNIN_REQUEST);
    }

    /**
     * Triggered by result from another activity launched from here
     *
     * @param requestCode The code of the requested activity
     * @param resultCode  The result code
     * @param data        The extra data from the activity
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ActivityCodes.SIGNIN_REQUEST: {
                if (resultCode == RESULT_OK) {
                    this.recreate();
                }
                break;
            }
        }
    }
}
