package ch.epfl.sweng.fiktion.views;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import ch.epfl.sweng.fiktion.R;
import ch.epfl.sweng.fiktion.android.AndroidPermissions;
import ch.epfl.sweng.fiktion.android.AndroidServices;
import ch.epfl.sweng.fiktion.controllers.UserController;
import ch.epfl.sweng.fiktion.models.User;
import ch.epfl.sweng.fiktion.models.posts.Post;
import ch.epfl.sweng.fiktion.providers.AuthProvider;
import ch.epfl.sweng.fiktion.providers.DatabaseProvider;
import ch.epfl.sweng.fiktion.providers.PhotoProvider;
import ch.epfl.sweng.fiktion.utils.Config;
import ch.epfl.sweng.fiktion.views.parents.MenuDrawerActivity;
import ch.epfl.sweng.fiktion.views.utils.ActivityCodes;
import ch.epfl.sweng.fiktion.views.utils.AuthenticationChecks;
import ch.epfl.sweng.fiktion.views.utils.POIDisplayer;

/**
 * Profile activity class
 */
public class ProfileActivity extends MenuDrawerActivity {

    // keys for extra data
    public static String USER_ID_KEY = "USER_ID";
    public static String PROFILE_ACTION_KEY = "PROFILE_ACTION";

    // define possible actions
    public enum Action {
        MY_PROFILE,
        ANOTHER_PROFILE
    }

    public static String PROFILE_ACTION_ME = "PROFILE_ACTION_ME";
    public static String PROFILE_ACTION_ANOTHER = "PROFILE_ACTION_ANOTHER";

    // define current action
    private Action state;
    private String stateFlag;

    // load own user and eventually the correct profile
    private User user, me;
    private UserController userCtrl;
    // load profile id and user's own id
    private String userId, myUserId;
    // views
    private TextView username, realInfos, country;
    private ImageView profilePicture, profileBanner;
    private ImageButton action;
    // image display const
    private int bannerWidth = 500;
    private int bannerHeight = 270;
    // this activity's context
    private Activity ctx = this;
    private Snackbar loading;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // pass layout to parent
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

        // show loading snackbar
        loading = Snackbar.make(profileBanner, R.string.loading_text, Snackbar.LENGTH_INDEFINITE);
        loading.show();

        // set default images
        profileBanner.setImageBitmap(POIDisplayer.cropAndScaleBitmapTo(BitmapFactory.decodeResource(getResources(), R.drawable.akibairl2), bannerWidth, bannerHeight));
        profilePicture.setImageBitmap(POIDisplayer.cropBitmapToSquare(BitmapFactory.decodeResource(getResources(), R.drawable.default_user)));

        // get userID from intent
        Intent from = getIntent();
        userId = from.getStringExtra(USER_ID_KEY);

        // get my user infos
        AuthProvider.getInstance().getCurrentUser(new DatabaseProvider.GetUserListener() {
            @Override
            public void onNewValue(User currUser) {
                selectAction(currUser);
            }

            @Override
            public void onModifiedValue(User currUser) {
                selectAction(currUser);
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
                } else {
                    // user may not be auth'd, but proceed here to show another user's profile without the need of being logged in
                    showAnotherProfile();
                }
            }
        });
    }

    /**
     * Selects the correct profile action
     */
    private void selectAction(User value) {
        // set my infos
        me = value;
        userCtrl = new UserController(me);
        myUserId = value.getID();

        // assume my profile if no userId set or if it is my own id
        if (userId == null || userId.isEmpty() || userId.equals(myUserId)) {
            user = value;
            // don't forget to set userId in case it was actually empty
            userId = myUserId;
            showMyProfile();
        } else {
            // user is logged in but it is not his profile
            showAnotherProfile();
        }
        downloadUserPictures();
    }

    /**
     * Show my own profile
     */
    private void showMyProfile() {
        // display profile
        this.state = Action.MY_PROFILE;
        this.stateFlag = PROFILE_ACTION_ME;
        // set action button
        action.setImageDrawable(getResources().getDrawable(R.drawable.pencil_icon_24));
        action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // go to settings
                Intent i = new Intent(ctx, SettingsActivity.class);
                startActivity(i);
            }
        });
        // show action button
        action.setVisibility(View.VISIBLE);

        updateInfos();
    }

    private void showAnotherProfile() {
        // display another user's profile
        this.state = Action.ANOTHER_PROFILE;
        this.stateFlag = PROFILE_ACTION_ANOTHER;

        // set action button
        action.setImageDrawable(getResources().getDrawable(R.drawable.person_add_icon_24));
        if (AuthProvider.getInstance().isConnected()) {
            // if user is logged in, check if in friend list, if not allow friend request
            if (!me.getFriendlist().contains(userId)) {
                // show action button
                action.setVisibility(View.VISIBLE);
                action.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // trigger user controller to send friend request
                        userCtrl.sendFriendRequest(userId, new UserController.RequestListener() {
                            @Override
                            public void onSuccess() {
                                Snackbar.make(profileBanner, R.string.friend_request_sent, Snackbar.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onDoesntExist() {
                                Snackbar.make(profileBanner, R.string.user_not_found, Snackbar.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure() {
                                Snackbar.make(profileBanner, R.string.request_failed, Snackbar.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onAlreadyFriend() {
                                Snackbar.make(profileBanner, R.string.already_in_friend_list, Snackbar.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onNewFriend() {
                                Snackbar.make(profileBanner, R.string.friend_added, Snackbar.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            } else {
                // hide the button if user reloaded with user in friendlist
                action.setVisibility(View.GONE);
            }
        } else {
            // if user not connected, prompt with dialog to allow login
            action.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AuthenticationChecks.checkVerifieddAuth(ctx, new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            dialog.dismiss();
                        }
                    });
                }
            });
        }

        // get profile from DB
        DatabaseProvider.getInstance().getUserById(userId, new DatabaseProvider.GetUserListener() {
            @Override
            public void onNewValue(User u) {
                // assign user
                user = u;
                // display profile
                updateInfos();
            }

            @Override
            public void onModifiedValue(User u) {
                // reassign user
                user = u;
                // redisplay profile
                showAnotherProfile();
            }

            @Override
            public void onDoesntExist() {
                Snackbar.make(profileBanner, R.string.user_not_found, Snackbar.LENGTH_INDEFINITE).show();
            }

            @Override
            public void onFailure() {
                Snackbar.make(profileBanner, R.string.failed_to_fetch_data, Snackbar.LENGTH_INDEFINITE).show();
            }
        });
    }

    /**
     * Update visible infos
     */
    @SuppressLint("SetTextI18n") // sample content that will be dynamically modified later
    private void updateInfos() {
        // hide loading
        loading.dismiss();
        // display infos
        username.setText(user.getName());
        //TODO : implement these in class User and retrieve them here
        realInfos.setText("John Doe, 21");
        country.setText("Switzerland");

        // set pictures onClick actions
        setPictureOnClickListener(PhotoProvider.UserPhotoType.PROFILE);
        setPictureOnClickListener(PhotoProvider.UserPhotoType.BANNER);

        // get user history
        if (isVisibleFromPrivacy()) {
            DatabaseProvider.getInstance().getUserPosts(userId, new DatabaseProvider.GetPostListener() {
                @Override
                public void onFailure() {
                    Snackbar.make(profileBanner, R.string.request_failed, Snackbar.LENGTH_INDEFINITE).show();
                }

                @Override
                public void onNewValue(Post value) {
                    // add post to linear layout
                    // TODO
                }
            });
        }
    }

    /**
     * Check if profile is visible given the privacy settings, the action and the friends list
     * Has to be called after state is set
     */
    private boolean isVisibleFromPrivacy() {
        boolean res;
        // safe check : if no user, don't allow
        if (user == null || me == null || state == null) {
            res = false;
        }
        // check if my profile
        else if (state == Action.MY_PROFILE) {
            res = true;
        }
        // check privacy setting
        else if (user.isPublicProfile()) {
            res = true;
        }
        // check if in my friend list
        else if (me.getFriendlist().contains(userId)) {
            res = true;
        }
        // trap case : refuse
        else {
            res = false;
        }

        // show snackbar if profile is not visible
        if (!res) {
            Snackbar.make(profileBanner, R.string.profile_is_private, Snackbar.LENGTH_INDEFINITE).show();
        }
        return res;
    }

    /**
     * download the user profile and banner picture
     */
    private void downloadUserPictures() {
        downloadUserPicture(PhotoProvider.UserPhotoType.PROFILE);
        downloadUserPicture(PhotoProvider.UserPhotoType.BANNER);
    }

    /**
     * Download a user picture
     *
     * @param photoType the type of picture to download
     */
    private void downloadUserPicture(final PhotoProvider.UserPhotoType photoType) {
        if (userId != null) {
            // download the user picture
            PhotoProvider.getInstance().downloadUserBitmap(userId, photoType, new PhotoProvider.DownloadBitmapListener() {
                @Override
                public void onNewValue(final Bitmap bitmap) {
                    switch (photoType) {
                        case PROFILE:
                            profilePicture.setImageBitmap(POIDisplayer.cropBitmapToSquare(bitmap));
                            break;
                        case BANNER:
                            profileBanner.setImageBitmap(POIDisplayer.cropAndScaleBitmapTo(bitmap, bannerWidth, bannerHeight));
                            break;
                        default:
                            return;
                    }
                    setPictureOnClickListener(photoType);
                }

                @Override
                public void onFailure() {
                }
            });
        }
    }

    private void setPictureOnClickListener(final PhotoProvider.UserPhotoType photoType) {
        final ImageView imgView;
        switch (photoType) {
            case PROFILE:
                imgView = profilePicture;
                break;
            case BANNER:
                imgView = profileBanner;
                break;
            default:
                return;
        }
        if (imgView != null) {
            imgView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    switch (state) {
                        case ANOTHER_PROFILE:
                            FullscreenPictureActivity.showBitmapInFullscreen(ctx, ((BitmapDrawable) imgView.getDrawable()).getBitmap());
                            break;

                        case MY_PROFILE:
                            PopupMenu popup = new PopupMenu(ctx, imgView, Gravity.END);
                            popup.inflate(R.menu.profile_picture_actions);
                            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem item) {
                                    switch (item.getItemId()) {
                                        case R.id.change_picture:
                                            requestPhotoType = photoType;
                                            requestPictureModification();
                                            break;

                                        case R.id.fullscreen_picture:
                                            FullscreenPictureActivity.showBitmapInFullscreen(ctx, ((BitmapDrawable) imgView.getDrawable()).getBitmap());
                                            break;
                                    }
                                    return true;
                                }
                            });
                            popup.show();
                    }

                }
            });
        }
    }

    PhotoProvider.UserPhotoType requestPhotoType;

    //photo and gallery

    // string to pass to onRequestPerm to know if camera or gallery was chosen
    private String userChoice;

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case AndroidPermissions.MY_PERMISSIONS_REQUEST_READ_EXT_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    AndroidServices.promptCameraEnable(this);
                    if (userChoice.equals("Camera"))
                        intentCamera();
                    else if (userChoice.equals("Gallery"))
                        intentGallery();
                } else {
                    // permission denied
                }
        }
    }

    //method to open a pop up window for Image
    private void requestPictureModification() {

        //pop up box to chose how to take picture
        final CharSequence[] choice = {"Camera", "Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);


        if (!Config.TEST_MODE && ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            AndroidPermissions.promptCameraPermission(this);
        } else {
            if (!Config.TEST_MODE)
                // check camera enable and ask otherwise
                AndroidServices.promptCameraEnable(this);

            builder.setItems(choice, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int item) {
                    if (choice[item].equals("Camera")) {
                        userChoice = "TakePhoto";
                        intentCamera();
                    } else if (choice[item].equals("Gallery")) {
                        userChoice = "Gallery";
                        intentGallery();
                    } else if (choice[item].equals("Cancel")) {
                        dialog.dismiss();
                    }

                }
            });
            builder.show();
        }

    }

    //camera intent
    private void intentCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, ActivityCodes.REQUEST_CAMERA);
    }


    //gallery intent
    private void intentGallery() {
        Intent gallery = new Intent();
        gallery.setType("image/*");
        gallery.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(gallery, "Select File"), ActivityCodes.SELECT_FILE);
    }


    //method for what do to when we got the camera/gallery result
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == ActivityCodes.SELECT_FILE) {
                onGalleryResult(data);
            } else if (requestCode == ActivityCodes.REQUEST_CAMERA) {
                onCameraResult(data);
            } else if (requestCode == ActivityCodes.SIGNIN_REQUEST) {
                recreate();
            }

        }
    }

    //gallery result which uploads the image
    private void onGalleryResult(Intent data) {
        Bitmap image = null;
        if (data != null) {
            try {
                image = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                return;
            }
        }
        uploadUserPicture(image);
    }

    //camera result, creates the ImageFile and uploads it
    private void onCameraResult(Intent data) {

        if (data == null) {
            return;
        }
        Bitmap image = (Bitmap) data.getExtras().get("data");
        if (image == null) {
            return;
        }
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        //(format, quality, outstream)
        image.compress(Bitmap.CompressFormat.JPEG, 100, bytes);


        //need to create the image file from the camera
        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream outstream;
        try {
            destination.createNewFile();
            outstream = new FileOutputStream(destination);
            outstream.write(bytes.toByteArray());
            outstream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        uploadUserPicture(image);
    }

    private void uploadUserPicture(final Bitmap b) {
        PhotoProvider.getInstance().uploadUserBitmap(b, userId, requestPhotoType, new PhotoProvider.UploadPhotoListener() {
            @Override
            public void onSuccess() {
                switch (requestPhotoType) {
                    case PROFILE:
                        profilePicture.setImageBitmap(POIDisplayer.cropBitmapToSquare(b));
                        break;
                    case BANNER:
                        profileBanner.setImageBitmap(POIDisplayer.cropAndScaleBitmapTo(b, bannerWidth, bannerHeight));
                        break;
                    default:
                }
            }

            @Override
            public void updateProgress(double progress) {
            }

            @Override
            public void onFailure() {
                switch (requestPhotoType) {
                    case PROFILE:
                        Snackbar.make(profilePicture, "the photo failed to upload", Snackbar.LENGTH_SHORT);
                        break;
                    case BANNER:
                        Snackbar.make(profileBanner, "the photo failed to upload", Snackbar.LENGTH_SHORT);
                        break;
                    default:
                }
            }
        });
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
        // if not loaded, don't do anything
        if (this.state == null || !isVisibleFromPrivacy()) return;
        Intent i = new Intent(this, UserPlacesActivity.class);
        i.putExtra(USER_ID_KEY, userId);
        i.putExtra(PROFILE_ACTION_KEY, stateFlag);
        startActivity(i);
    }

    /**
     * Triggered by the Pictures action button
     *
     * @param view caller view
     */
    public void startUserPicturesActivity(View view) {
        // if not loaded, don't do anything
        if (this.state == null || !isVisibleFromPrivacy()) return;
        Intent i = new Intent(this, UserPicturesActivity.class);
        i.putExtra(USER_ID_KEY, userId);
        i.putExtra(PROFILE_ACTION_KEY, stateFlag);
        startActivity(i);
    }

    /**
     * Triggered by the Friends action button
     *
     * @param view caller view
     */
    public void startUserFriendsActivity(View view) {
        // if not loaded, don't do anything
        if (this.state == null || !isVisibleFromPrivacy()) return;
        Intent i = new Intent(this, UserFriendsActivity.class);
        i.putExtra(USER_ID_KEY, userId);
        i.putExtra(PROFILE_ACTION_KEY, stateFlag);
        startActivity(i);
    }

    /**
     * Triggered by the Achievement action button
     *
     * @param view caller view
     */
    public void startUserAchievementsActivity(View view) {
        // if not loaded, don't do anything
        if (this.state == null || !isVisibleFromPrivacy()) return;
        Intent i = new Intent(this, UserAchievementsActivity.class);
        i.putExtra(USER_ID_KEY, userId);
        i.putExtra(PROFILE_ACTION_KEY, stateFlag);
        startActivity(i);
    }

    /**
     * Activity state getter
     *
     * @return the current state of the activity
     */
    public Action getState() {
        return this.state;
    }
}
