package ch.epfl.sweng.fiktion.views;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
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

    private User user;

    private TextView username, realInfos, country;

    private ImageView profilePicture, profileBanner;

    private final int SIGNIN_REQUEST = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        includeLayout = R.layout.activity_profile;
        super.onCreate(savedInstanceState);

        // check if user is connected and has a valid account
        AuthenticationChecks.checkAuthState(this);
        // check if user's account is verified, otherwise prompt verification and/or refresh
        if (!AuthProvider.getInstance().isEmailVerified()) {
            return;
        }

        //create user infos fields
        username = (TextView) findViewById(R.id.username);
        realInfos = (TextView) findViewById(R.id.userRealInfos);
        country = (TextView) findViewById(R.id.userCountry);
        //create user profile images fields
        profilePicture = (ImageView) findViewById(R.id.userProfilePicture);
        profileBanner = (ImageView) findViewById(R.id.userBanner);

        // set default images
        profileBanner.setImageBitmap(POIDisplayer.cropAndScaleBitmapTo(BitmapFactory.decodeResource(getResources(), R.drawable.default_image), 900, 370));
        profilePicture.setImageBitmap(POIDisplayer.cropBitmapToSquare(BitmapFactory.decodeResource(getResources(), R.drawable.default_user)));

        // get user infos
        AuthProvider.getInstance().getCurrentUser(new DatabaseProvider.GetUserListener() {
            @Override
            public void onSuccess(User currUser) {
                user = currUser;
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
        Intent i = new Intent(this, SignInActivity.class);
        startActivityForResult(i, SIGNIN_REQUEST);
    }
}
