package ch.epfl.sweng.fiktion.views;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import ch.epfl.sweng.fiktion.R;
import ch.epfl.sweng.fiktion.models.User;
import ch.epfl.sweng.fiktion.providers.AuthProvider;
import ch.epfl.sweng.fiktion.providers.DatabaseProvider;
import ch.epfl.sweng.fiktion.views.parents.MenuDrawerActivity;
import ch.epfl.sweng.fiktion.views.utils.AuthenticationChecks;
import ch.epfl.sweng.fiktion.views.utils.POIDisplayer;

/**
 * Profile activity class
 */
public class ProfileActivity extends MenuDrawerActivity {

    public static String USER_ID_KEY = "USER_ID";

    private User user;
    private String userId;
    private TextView username, realInfos, country;
    private ImageView profilePicture, profileBanner;
    private int bannerWidth = 500;
    private int bannerHeight = 270;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        includeLayout = R.layout.activity_profile;
        super.onCreate(savedInstanceState);

        // check if user is connected and has a valid account
        AuthenticationChecks.checkAuthState(this);

        //create user infos fields
        username = (TextView) findViewById(R.id.username);
        realInfos = (TextView) findViewById(R.id.userRealInfos);
        country = (TextView) findViewById(R.id.userCountry);
        //create user profile images fields
        profilePicture = (ImageView) findViewById(R.id.userProfilePicture);
        profileBanner = (ImageView) findViewById(R.id.userBanner);

        // set default images
        profileBanner.setImageBitmap(POIDisplayer.cropAndScaleBitmapTo(BitmapFactory.decodeResource(getResources(), R.drawable.akibairl2), bannerWidth, bannerHeight));
        profilePicture.setImageBitmap(POIDisplayer.cropBitmapToSquare(BitmapFactory.decodeResource(getResources(), R.drawable.default_user)));

        // get user infos
        AuthProvider.getInstance().getCurrentUser(new DatabaseProvider.GetUserListener() {
            @Override
            public void onSuccess(User currUser) {
                user = currUser;
                userId = currUser.getID();
                username.setText(user.getName());
                //TODO : implement these in class User and retrieve them here
                realInfos.setText("John Doe, 21");
                country.setText("Switzerland");

            }

            @Override
            public void onDoesntExist() {
                redirectToLogin();
            }

            @Override
            public void onFailure() {
                redirectToLogin();
            }
        });
    }

    /**
     * Prompt the User with a sign in
     */
    public void redirectToLogin() {
        AuthenticationChecks.checkAuthState(this);
    }

    /**
     * Triggered by the Places action button
     * @param view caller view
     */
    public void startUserPlacesActivity(View view) {
        Intent i = new Intent(this, UserPlacesActivity.class);
        i.putExtra(USER_ID_KEY, userId);
        startActivity(i);
    }

    /**
     * Triggered by the Pictures action button
     * @param view caller view
     */
    public void startUserPicturesActivity(View view) {
        Intent i = new Intent(this, UserPicturesActivity.class);
        i.putExtra(USER_ID_KEY, userId);
        startActivity(i);
    }

    /**
     * Triggered by the Friends action button
     * @param view caller view
     */
    public void startUserFriendsActivity(View view) {
        Intent i = new Intent(this, UserFriendsActivity.class);
        i.putExtra(USER_ID_KEY, userId);
        startActivity(i);
    }

    /**
     * Triggered by the Achievement action button
     * @param view caller view
     */
    public void startUserAchievementsActivity(View view) {
        Intent i = new Intent(this, UserAchievementsActivity.class);
        i.putExtra(USER_ID_KEY, userId);
        startActivity(i);
    }
}
