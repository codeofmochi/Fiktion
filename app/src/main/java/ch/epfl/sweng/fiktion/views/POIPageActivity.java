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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.ArrayList;
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
import ch.epfl.sweng.fiktion.views.utils.POIDisplayer;

import static ch.epfl.sweng.fiktion.providers.PhotoProvider.ALL_PHOTOS;

public class POIPageActivity extends MenuDrawerActivity implements OnMapReadyCallback {

    private final int MAXIMUM_SIZE = 1000;
    public static final String POI_NAME = "POI_NAME";
    public static final String USER_NAME = "USER_NAME";
    private final int SEARCH_RADIUS = 20;

    public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ViewHolder> {
        private ArrayList<String> data;

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView text;

            public ViewHolder(TextView v) {
                super(v);
                text = v;
            }
        }

        public ReviewsAdapter(ArrayList<String> data) {
            this.data = data;
        }

        @Override
        public ReviewsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            TextView v = (TextView) LayoutInflater.from(parent.getContext()).inflate(R.layout.review_card, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.text.setText(data.get(position));
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }

    private String poiName;
    private final Context ctx = this;
    private LinearLayout nearbyPoisList;
    private TextView noNearbyPois;
    private PointOfInterest poi;
    private User user;
    private Button upvoteButton;
    private boolean upvoted = false;
    private ProgressBar uploadProgressBar;
    private LinearLayout imageLayout;
    private ImageView noImages;
    private ImageView mainImage;
    private MapView map;
    private RecyclerView.Adapter reviewsAdapter;
    private ArrayList<String> reviewsData = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // give layout to parent menu class
        includeLayout = R.layout.activity_poipage;
        super.onCreate(savedInstanceState);

        //picture button
        Button addPictureButton = (Button) findViewById(R.id.addPictureButton);
        addPictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });

        // upvote button
        upvoteButton = (Button) findViewById(R.id.upvoteButton);
        upvoteButton.setEnabled(false);

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

        // get POI name
        Intent from = getIntent();
        poiName = from.getStringExtra("POI_NAME");

        ((TextView) findViewById(R.id.title)).setText(poiName);

        AuthProvider.getInstance().getCurrentUser(new DatabaseProvider.GetUserListener() {
            @Override
            public void onSuccess(User user) {
                setUser(user);
                upvoteButton.setEnabled(true);
                if (user.getUpvoted().contains(poiName)) {
                    upvoted = true;
                    upvoteButton.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                } else {
                    upvoteButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                }
            }

            @Override
            public void onDoesntExist() {

            }

            @Override
            public void onFailure() {
            }
        });

        // get POI from database
        DatabaseProvider.getInstance().getPoi(poiName, new DatabaseProvider.GetPoiListener() {
            @Override
            public void onSuccess(PointOfInterest poi) {
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
            public void onModified(PointOfInterest poi) {
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

        // get recycler view for reviews
        RecyclerView reviewsView = (RecyclerView) findViewById(R.id.reviews);
        RecyclerView.LayoutManager reviewsLayout = new LinearLayoutManager(this);
        reviewsView.setLayoutManager(reviewsLayout);
        reviewsAdapter = new ReviewsAdapter(reviewsData);
        reviewsView.setAdapter(reviewsAdapter);


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
        if (user != null && poi != null) {
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

    public void downloadComments() {
        DatabaseProvider.getInstance().getComments(poi.name(), new DatabaseProvider.GetCommentsListener(){

            @Override
            public void onNewValue(Comment comment) {
                reviewsData.add(comment.getText() + System.lineSeparator() + "Written by: " + comment.getAuthorId());
                reviewsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure() {
            }
        });
    }
    public void downloadPhotos() {

        final ImageView mainImage = (ImageView) findViewById(R.id.mainImage);

        // set the mainImage as the first photo of the poi
        PhotoProvider.getInstance().downloadPOIBitmaps(poi.name(), 1, new PhotoProvider.DownloadBitmapListener() {
            @Override
            public void onNewPhoto(Bitmap b) {
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
            public void onNewPhoto(Bitmap b) {

                // create a new ImageView which will hold the photo
                ImageView imgView = new ImageView(getApplicationContext());

                // set the content and the parameters
                imgView.setImageBitmap(b);
                imgView.setAdjustViewBounds(true);
                LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
                params.setMarginStart(10);
                params.setMarginEnd(10);
                imgView.setLayoutParams(params);
                imgView.setContentDescription("a photo of " + poi.name());

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

    public void displayNearPois() {
        // find nearby pois
        DatabaseProvider.getInstance().findNearPois(poi.position(), SEARCH_RADIUS, new DatabaseProvider.FindNearPoisListener() {
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

    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;

    // string to pass to onRequestPerm to know if camera or gallery was chosen
    String userChoice;

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


    //method to open a pop up window
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
        startActivityForResult(intent, REQUEST_CAMERA);
    }


    //gallery intent
    private void intentGallery() {
        Intent gallery = new Intent();
        gallery.setType("image/*");
        gallery.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(gallery, "Select File"), SELECT_FILE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE) {
                onGalleryResult(data);
            } else if (requestCode == REQUEST_CAMERA) {
                onCameraResult(data);
            }
        }
    }

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

    private void onCameraResult(Intent data) {

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

    public void startWriteCommentActivity(View view) {
        Intent i = new Intent(ctx, WriteCommentActivity.class);
        i.putExtra(POI_NAME, poiName);
        i.putExtra(USER_NAME, user.getName());
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
                    case R.id.favorite:
                        // do stuff on click favorite
                        return true;
                    case R.id.wishlist:
                        // do stuff on click wishlist
                        return true;
                    case R.id.edit:
                        // do stuff on click edit
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



