package ch.epfl.sweng.fiktion.views;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import ch.epfl.sweng.fiktion.R;
import ch.epfl.sweng.fiktion.models.User;
import ch.epfl.sweng.fiktion.providers.AuthProvider;
import ch.epfl.sweng.fiktion.providers.DatabaseProvider;
import ch.epfl.sweng.fiktion.views.parents.MenuDrawerActivity;
import ch.epfl.sweng.fiktion.views.utils.ActivityCodes;
import ch.epfl.sweng.fiktion.views.utils.AuthenticationChecks;
import ch.epfl.sweng.fiktion.views.utils.POIDisplayer;

/**
 * Profile activity class
 */
public class ProfileActivity extends MenuDrawerActivity {

    public static String USER_ID_KEY = "USER_ID";

    private User user, me;
    private String userId, myUserId;
    private TextView username, realInfos, country;
    private ImageView profilePicture, profileBanner;
    private ImageButton action;
    private int bannerWidth = 500;
    private int bannerHeight = 270;
    private Activity ctx = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        includeLayout = R.layout.activity_profile;
        super.onCreate(savedInstanceState);

        //create user infos fields
        username = (TextView) findViewById(R.id.username);
        realInfos = (TextView) findViewById(R.id.userRealInfos);
        country = (TextView) findViewById(R.id.userCountry);
        //create user profile images fields
        profilePicture = (ImageView) findViewById(R.id.userProfilePicture);
        profileBanner = (ImageView) findViewById(R.id.userBanner);
        action = (ImageButton) findViewById(R.id.userAction);

        // set action text
        action.setImageDrawable(getResources().getDrawable(R.drawable.person_add_icon_24));

        // set default images
        profileBanner.setImageBitmap(POIDisplayer.cropAndScaleBitmapTo(BitmapFactory.decodeResource(getResources(), R.drawable.akibairl2), bannerWidth, bannerHeight));
        profilePicture.setImageBitmap(POIDisplayer.cropBitmapToSquare(BitmapFactory.decodeResource(getResources(), R.drawable.default_user)));

        // get userID from intent
        Intent from = getIntent();
        userId = from.getStringExtra(USER_ID_KEY);

        // get my user infos
        AuthProvider.getInstance().getCurrentUser(new DatabaseProvider.GetUserListener() {
            @Override
            public void onSuccess(User currUser) {
                // set my infos
                me = currUser;
                myUserId = currUser.getID();

                // assume my profile if no userId set or if it is my own id
                if (userId == null || userId.isEmpty() || userId.equals(myUserId)) {
                    user = currUser;
                    showMyProfile();
                }
            }

            @Override
            public void onDoesntExist() {
                // wrong login
                redirectToLogin();
            }

            @Override
            public void onFailure() {
                // user is not auth'd but it is his profile, show login
                if (userId == null || userId.isEmpty()) {
                    redirectToLogin();
                }
                // user may not be auth'd, but proceed here to show another user's profile without the need of being logged in
                showAnotherProfile();
            }
        });
    }

    /**
     * Show my own profile
     */
    private void showMyProfile() {
        // display profile
        updateInfos();
    }

    private void showAnotherProfile() {
        // get profile from DB


            // display profile
    }

    /**
     * Update visible infos
     */
    private void updateInfos() {
        username.setText(user.getName());
        //TODO : implement these in class User and retrieve them here
        realInfos.setText("John Doe, 21");
        country.setText("Switzerland");
    }

    /**
     * Triggered when an activity called here returns with a result
     *
     * @param requestCode the request code
     * @param resultCode the result code
     * @param data
     */
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

    /**
     * Prompt the User with a sign in
     */
    public void redirectToLogin() {
        // check if user is connected and has a valid account
        AuthenticationChecks.checkLoggedAuth(this, new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                AuthenticationChecks.goHome(ctx);
            }
        });
    }

    /**
     * Triggered by the Places action button
     *
     * @param view caller view
     */
    public void startUserPlacesActivity(View view) {
        Intent i = new Intent(this, UserPlacesActivity.class);
        i.putExtra(USER_ID_KEY, userId);
        startActivity(i);
    }

    /**
     * Triggered by the Pictures action button
     *
     * @param view caller view
     */
    public void startUserPicturesActivity(View view) {
        Intent i = new Intent(this, UserPicturesActivity.class);
        i.putExtra(USER_ID_KEY, userId);
        startActivity(i);
    }

    /**
     * Triggered by the Friends action button
     *
     * @param view caller view
     */
    public void startUserFriendsActivity(View view) {
        Intent i = new Intent(this, UserFriendsActivity.class);
        i.putExtra(USER_ID_KEY, userId);
        startActivity(i);
    }

    /**
     * Triggered by the Achievement action button
     *
     * @param view caller view
     */
    public void startUserAchievementsActivity(View view) {
        Intent i = new Intent(this, UserAchievementsActivity.class);
        i.putExtra(USER_ID_KEY, userId);
        startActivity(i);
    }
}
