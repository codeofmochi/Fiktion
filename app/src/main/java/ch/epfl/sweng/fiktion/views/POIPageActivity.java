package ch.epfl.sweng.fiktion.views;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Set;

import ch.epfl.sweng.fiktion.R;
import ch.epfl.sweng.fiktion.android.AndroidPermissions;
import ch.epfl.sweng.fiktion.android.AndroidServices;
import ch.epfl.sweng.fiktion.models.Comment;
import ch.epfl.sweng.fiktion.models.PointOfInterest;
import ch.epfl.sweng.fiktion.models.Position;
import ch.epfl.sweng.fiktion.models.User;
import ch.epfl.sweng.fiktion.providers.AuthProvider;
import ch.epfl.sweng.fiktion.providers.DatabaseProvider;
import ch.epfl.sweng.fiktion.providers.PhotoProvider;
import ch.epfl.sweng.fiktion.utils.Config;
import ch.epfl.sweng.fiktion.views.parents.MenuDrawerActivity;
import ch.epfl.sweng.fiktion.views.utils.ActivityCodes;
import ch.epfl.sweng.fiktion.views.utils.AuthenticationChecks;
import ch.epfl.sweng.fiktion.views.utils.CommentsDisplayer;
import ch.epfl.sweng.fiktion.views.utils.POIDisplayer;

import static ch.epfl.sweng.fiktion.providers.PhotoProvider.ALL_PHOTOS;

public class POIPageActivity extends MenuDrawerActivity implements OnMapReadyCallback {

    private final int MAXIMUM_SIZE = 1000;
    public static final String POI_NAME = "POI_NAME";
    public static final String USER_ID = "USER_ID";
    private final int SEARCH_RADIUS = 20;

    private String poiName;
    private final Context ctx = this;
    private LinearLayout nearbyPoisList;
    private TextView noNearbyPois;
    private PointOfInterest poi;
    private User user;
    private Button upvoteButton;
    private Button addPictureButton;
    private boolean upvoted = false;
    private ProgressBar uploadProgressBar;
    private LinearLayout imageLayout;
    private ImageView noImages;
    private ImageView mainImage;
    private MapView map;
    private CommentsDisplayer.LoadableList reviewsList;
    private DialogInterface.OnCancelListener cancelListener = new DialogInterface.OnCancelListener() {
        @Override
        public void onCancel(DialogInterface dialog) {
            dialog.dismiss();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // give layout to parent menu class
        includeLayout = R.layout.activity_poipage;
        super.onCreate(savedInstanceState);

        //picture button
        addPictureButton = (Button) findViewById(R.id.addPictureButton);
        addPictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AuthenticationChecks.checkVerifieddAuth((Activity) ctx, cancelListener);
                // check if user's account is verified, otherwise prompt verification and/or refresh
                if (!AuthProvider.getInstance().isEmailVerified()) {
                    return;
                }
                selectImage();
            }
        });

        // upvote button
        upvoteButton = (Button) findViewById(R.id.upvoteButton);

        // Obtain the SupportMapFragment
        map = (MapView) findViewById(R.id.map);
        map.onCreate(savedInstanceState);

        mainImage = (ImageView) findViewById(R.id.mainImage);
        mainImage.setVisibility(View.GONE);
        imageLayout = (LinearLayout) findViewById(R.id.imageLayout);
        uploadProgressBar = (ProgressBar) findViewById(R.id.uploadProgressBar);

        // add a "no pictures yet" default image
        Bitmap bm = POIDisplayer.scaleBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.default_image), 400);
        noImages = new ImageView(this);
        noImages.setImageBitmap(bm);
        imageLayout.addView(noImages);

        // reviews
        reviewsList = new CommentsDisplayer.LoadableList((LinearLayout) findViewById(R.id.reviews), 5, this);

        // get POI name
        Intent from = getIntent();
        poiName = from.getStringExtra("POI_NAME");

        ((TextView) findViewById(R.id.title)).setText(poiName);

        // check if user upvoted this poi
        AuthProvider.getInstance().getCurrentUser(new DatabaseProvider.GetUserListener() {
            @Override
            public void onNewValue(User user) {
                setUser(user);
                if (user.getUpvoted().contains(poiName)) {
                    upvoted = true;
                    upvoteButton.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                } else {
                    upvoteButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                }
            }

            @Override
            public void onModifiedValue(User user) {

            }

            @Override
            public void onDoesntExist() {

            }

            @Override
            public void onFailure() {
            }
        });

        // get POI from database
        DatabaseProvider.getInstance().getPOI(poiName, new DatabaseProvider.GetPOIListener() {
            @Override
            public void onNewValue(PointOfInterest poi) {
                setPOI(poi);
                downloadPhotos();
                callMap();
                displayNearPois();
                downloadComments();
                setPOIInformation();
                // hide loading spinner
                ProgressBar spinner = (ProgressBar) findViewById(R.id.loadingSpinner);
                spinner.setVisibility(View.GONE);
            }

            @Override
            public void onModifiedValue(PointOfInterest poi) {
                setPOI(poi);
                setPOIInformation();
            }

            @Override
            public void onDoesntExist() {
                Snackbar.make(findViewById(R.id.title), R.string.data_not_found, Snackbar.LENGTH_INDEFINITE).show();
            }

            @Override
            public void onFailure() {
                Snackbar.make(findViewById(R.id.title), R.string.failed_to_fetch_data, Snackbar.LENGTH_INDEFINITE).show();
            }
        });

        // get nearby pois views
        nearbyPoisList = (LinearLayout) findViewById(R.id.nearbyPoisList);
        noNearbyPois = (TextView) findViewById(R.id.noNearbyPois);
    }

    private void setPOI(PointOfInterest poi) {
        this.poi = poi;
    }

    private void setUser(User user) {
        this.user = user;
    }

    public void vote(View view) {
        // check if user is connected and has a valid account
        AuthenticationChecks.checkVerifieddAuth((Activity) ctx, cancelListener);
        // check if user's account is verified, otherwise prompt verification and/or refresh
        // this 'if' code is required in case the user dismisses the dialog
        if (!AuthProvider.getInstance().isEmailVerified()) {
            return;
        }
        if (poi != null) {
            // disable the button
            upvoteButton.setEnabled(false);
            // set the button color to gray
            upvoteButton.setBackgroundColor(getResources().getColor(R.color.colorText));
            if (upvoted) {
                // remove the vote
                user.removeVote(poi.name(), new DatabaseProvider.ModifyUserListener() {
                    @Override
                    public void onSuccess() {
                        // downvote in the database
                        DatabaseProvider.getInstance().downvote(poi.name(), new DatabaseProvider.ModifyPOIListener() {
                            @Override
                            public void onSuccess() {
                                upvoteButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                upvoteButton.setEnabled(true);
                                upvoted = false;
                            }

                            @Override
                            public void onDoesntExist() {
                                upvoteButton.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                                upvoteButton.setEnabled(true);
                            }

                            @Override
                            public void onFailure() {
                                upvoteButton.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                                upvoteButton.setEnabled(true);
                            }
                        });
                    }

                    @Override
                    public void onDoesntExist() {
                        upvoteButton.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                        upvoteButton.setEnabled(true);
                    }

                    @Override
                    public void onFailure() {
                        upvoteButton.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                        upvoteButton.setEnabled(true);
                    }
                });
            } else {
                // upvote
                user.upVote(poi.name(), new DatabaseProvider.ModifyUserListener() {
                    @Override
                    public void onSuccess() {
                        DatabaseProvider.getInstance().upvote(poi.name(), new DatabaseProvider.ModifyPOIListener() {
                            @Override
                            public void onSuccess() {
                                upvoteButton.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                                upvoteButton.setEnabled(true);
                                upvoted = true;
                            }

                            @Override
                            public void onDoesntExist() {
                                upvoteButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                upvoteButton.setEnabled(true);
                            }

                            @Override
                            public void onFailure() {
                                upvoteButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                upvoteButton.setEnabled(true);
                            }
                        });
                    }

                    @Override
                    public void onDoesntExist() {
                        upvoteButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                        upvoteButton.setEnabled(true);
                    }

                    @Override
                    public void onFailure() {
                        upvoteButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                        upvoteButton.setEnabled(true);
                    }
                });
            }
        }
    }

    private void callMap() {
        // get notified when the map is ready to be used
        map.getMapAsync(this);
    }

    @SuppressWarnings("SetTextI18n")
    private void setPOIInformation() {

        // show the fictions the poi appears in
        Set<String> fictions = poi.fictions();
        TextView featured = (TextView) findViewById(R.id.featured);
        StringBuilder sb = new StringBuilder();
        sb.append("Featured in ");
        if (fictions.isEmpty())
            sb.append("nothing");
        else {
            for (String fiction : fictions) {
                sb.append(fiction + ", ");
            }
            sb.delete(sb.length() - 2, sb.length());
        }
        featured.setText(sb.toString());

        // change text color
        Spannable span = new SpannableString(featured.getText());
        span.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorPrimary)), 12, featured.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        featured.setText(span);

        TextView description = (TextView) findViewById(R.id.description);
        description.setText(poi.description());

        TextView cityCountry = (TextView) findViewById(R.id.cityCountry);
        cityCountry.setText(poi.city() + ", " + poi.country());

        TextView upvotes = (TextView) findViewById(R.id.upvotes);
        upvotes.setText(poi.rating() + " upvotes");
    }

    private void downloadComments() {
        DatabaseProvider.getInstance().getPOIComments(poi.name(), new DatabaseProvider.GetCommentsListener() {

            @Override
            public void onNewValue(Comment comment) {
                // add to reviewsList
                reviewsList.add(comment);
            }

            @Override
            public void onModifiedValue(Comment comment) {
                reviewsList.updateRating(comment.getId(), comment.getRating());
            }

            @Override
            public void onFailure() {
            }
        });
    }

    private void downloadPhotos() {

        final ImageView mainImage = (ImageView) findViewById(R.id.mainImage);

        // set the mainImage as the first photo of the poi
        PhotoProvider.getInstance().downloadPOIBitmaps(poi.name(), 1, new PhotoProvider.DownloadBitmapListener() {
            @Override
            public void onNewValue(Bitmap b) {
                Bitmap resized = POIDisplayer.cropAndScaleBitmapTo(b, 900, 600);
                mainImage.setImageBitmap(resized);
                mainImage.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure() {
            }
        });

        // download the photos of the poi
        PhotoProvider.getInstance().downloadPOIBitmaps(poi.name(), ALL_PHOTOS, new PhotoProvider.DownloadBitmapListener() {
            @Override
            public void onNewValue(final Bitmap b) {

                // create a new ImageView which will hold the photo
                final ImageView imgView = new ImageView(getApplicationContext());

                // set the content and the parameters
                imgView.setImageBitmap(b);
                imgView.setAdjustViewBounds(true);
                LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
                params.setMarginStart(10);
                params.setMarginEnd(10);
                imgView.setLayoutParams(params);
                imgView.setContentDescription("a photo of " + poi.name());
                //renders each image clickable. Calls FullscreenPictureActivity
                imgView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String fileName = "image";
                        try {
                            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                            b.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                            FileOutputStream fo = openFileOutput(fileName, Context.MODE_PRIVATE);
                            fo.write(bytes.toByteArray());
                            //close file
                            fo.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                            fileName = null;
                        }
                        Intent intent = new Intent(ctx, FullscreenPictureActivity.class);
                        intent.putExtra("Photo", fileName);
                        startActivity(intent);
                    }
                });

                // add the ImageView to the pictures
                imageLayout.addView(imgView);

                // remove "no pictures yet"
                if (noImages.getVisibility() == View.VISIBLE) {
                    noImages.setVisibility(View.GONE);
                    imageLayout.removeView(noImages);
                }
            }

            @Override
            public void onFailure() {

            }
        });
    }


    private void displayNearPois() {
        // find nearby pois
        DatabaseProvider.getInstance().findNearPOIs(poi.position(), SEARCH_RADIUS, new DatabaseProvider.FindNearPOIsListener() {
            @Override
            public void onNewValue(PointOfInterest p) {
                View v = POIDisplayer.createPoiCard(p, ctx);
                if (!poi.name().equals(p.name())) {
                    nearbyPoisList.addView(v);
                    noNearbyPois.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onFailure() {
                // do nothing
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // add a marker to the poi position
        Position pos = poi.position();
        LatLng mark = new LatLng(pos.latitude(), pos.longitude());
        googleMap.addMarker(new MarkerOptions().position(mark)
                .title(poi.name()));

        // move to the position and zoom
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(mark));
        googleMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        map.onResume();
    }


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
    private void selectImage() {

        if (poi == null) {
            Toast.makeText(this, "Point of interest information isn't loaded", Toast.LENGTH_SHORT).show();
            return;
        }

        //pop up box to chose how to take picture
        final CharSequence[] choice = {"Camera", "Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(POIPageActivity.this);


        if (!Config.TEST_MODE && ContextCompat.checkSelfPermission(POIPageActivity.this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            AndroidPermissions.promptCameraPermission(POIPageActivity.this);
        } else {
            if (!Config.TEST_MODE)
                // check camera enable and ask otherwise
                AndroidServices.promptCameraEnable(POIPageActivity.this);

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

        upload(image);
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

        upload(image);
    }

    private void upload(Bitmap bitmap) {

        Bitmap uploadBitmap = bitmap.getHeight() <= MAXIMUM_SIZE && bitmap.getWidth() <= MAXIMUM_SIZE ?
                bitmap : POIDisplayer.scaleBitmap(bitmap, MAXIMUM_SIZE);

        // upload the photo to the cloud
        // show the progress with the progressbar
        uploadProgressBar.setVisibility(View.VISIBLE);
        PhotoProvider.getInstance().uploadPOIBitmap(uploadBitmap, poi.name(), new PhotoProvider.UploadPhotoListener() {
            @Override
            public void onSuccess() {
                uploadProgressBar.setVisibility(View.INVISIBLE);
                uploadProgressBar.setProgress(0);
            }

            @Override
            public void onFailure() {
                uploadProgressBar.setVisibility(View.INVISIBLE);
                uploadProgressBar.setProgress(0);
            }

            @Override
            public void updateProgress(double progress) {
                uploadProgressBar.setProgress((int) progress);
            }
        });
    }

    //Button with id = AddReviewButton calls this, opens the WriteCommentActivity
    public void startWriteCommentActivity(View view) {
        // check if user is connected and has a valid account
        AuthenticationChecks.checkVerifieddAuth((Activity) ctx, cancelListener);
        // check if user's account is verified, otherwise prompt verification and/or refresh
        // this 'if' code is required in case the user dismisses the dialog
        if (!AuthProvider.getInstance().isEmailVerified()) {
            return;
        }
        Intent i = new Intent(ctx, WriteCommentActivity.class);
        i.putExtra(POI_NAME, poiName);
        i.putExtra(USER_ID, user.getID());
        startActivity(i);
    }


    /**
     * Triggered by more menu button
     *
     * @param v the caller view
     */
    public void showMoreMenu(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.inflate(R.menu.poi_more_actions);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.favourite:
                        // check if user is connected and has a valid account
                        AuthenticationChecks.checkVerifieddAuth((Activity) ctx, cancelListener);
                        // check if user's account is verified, otherwise prompt verification and/or refresh
                        // this 'if' code is required in case the user dismisses the dialog
                        if (!AuthProvider.getInstance().isEmailVerified()) {
                            return true;
                        }
                        user.addFavourite(poiName, new DatabaseProvider.ModifyUserListener() {
                            @Override
                            public void onSuccess() {
                                Toast.makeText(ctx, poiName + " was added to favourites!", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onDoesntExist() {
                                Toast.makeText(ctx, "User does not exist in database!", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure() {
                                Toast.makeText(ctx, "Failed to add to favourites", Toast.LENGTH_SHORT).show();
                            }
                        });
                        return true;
                    case R.id.wishlist:
                        // check if user is connected and has a valid account
                        AuthenticationChecks.checkVerifieddAuth((Activity) ctx, cancelListener);
                        // check if user's account is verified, otherwise prompt verification and/or refresh
                        // this 'if' code is required in case the user dismisses the dialog
                        if (!AuthProvider.getInstance().isEmailVerified()) {
                            return true;
                        }
                        user.addToWishlist(poiName, new DatabaseProvider.ModifyUserListener() {
                            @Override
                            public void onSuccess() {
                                Toast.makeText(ctx, poiName + " was added to the wishlist!", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onDoesntExist() {
                                Toast.makeText(ctx, "User does not exist in database!", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure() {
                                Toast.makeText(ctx, "Failed to add to wishlist", Toast.LENGTH_SHORT).show();
                            }
                        });
                        return true;
                    case R.id.edit:
                        // check if user is connected and has a valid account
                        AuthenticationChecks.checkVerifieddAuth((Activity) ctx, cancelListener);
                        // check if user's account is verified, otherwise prompt verification and/or refresh
                        // this 'if' code is required in case the user dismisses the dialog
                        if (!AuthProvider.getInstance().isEmailVerified()) {
                            return true;
                        }
                        Intent i = new Intent(ctx, AddPOIActivity.class);
                        i.putExtra("EDIT_POI_NAME", poiName);
                        startActivity(i);
                        return true;
                    default:
                        return false;
                }
            }
        });
        popup.show();

    }


}



