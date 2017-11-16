package ch.epfl.sweng.fiktion.views;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import ch.epfl.sweng.fiktion.R;
import ch.epfl.sweng.fiktion.android.AndroidPermissions;
import ch.epfl.sweng.fiktion.android.AndroidServices;
import ch.epfl.sweng.fiktion.models.PointOfInterest;
import ch.epfl.sweng.fiktion.providers.DatabaseProvider;
import ch.epfl.sweng.fiktion.providers.Providers;
import ch.epfl.sweng.fiktion.views.parents.MenuDrawerActivity;

public class POIPageActivity extends MenuDrawerActivity implements OnMapReadyCallback {

    public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ViewHolder> {
        private String[] data;

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView text;

            public ViewHolder(TextView v) {
                super(v);
                text = v;
            }
        }

        public ReviewsAdapter(String[] data) {
            this.data = data;
        }

        @Override
        public ReviewsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            TextView v = (TextView) LayoutInflater.from(parent.getContext()).inflate(R.layout.review_card, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.text.setText(data[position]);
        }

        @Override
        public int getItemCount() {
            return data.length;
        }
    }

    private MapView map;
    private RecyclerView reviewsView;
    private RecyclerView.Adapter reviewsAdapter;
    private RecyclerView.LayoutManager reviewsLayout;
    private Button addPictureButton;
    private ImageView image1;
    private String[] reviewsData = {
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec congue dolor at auctor scelerisque. Duis sodales eros velit, sit amet tincidunt ex pharetra ac. Pellentesque pellentesque et augue ut pellentesque. Suspendisse in lacinia nunc. Integer consequat sollicitudin ligula sed finibus.",
            "Curabitur condimentum ligula eu diam maximus porttitor. Interdum et malesuada fames ac ante ipsum primis in faucibus. Suspendisse metus urna, tincidunt sed augue ac, consectetur congue felis. Pellentesque efficitur enim et ultrices pellentesque.",
            "Curabitur quis lectus eu ex volutpat eleifend. Sed iaculis orci ut odio sodales, id lobortis est volutpat. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae",
            "Proin suscipit, mauris quis ullamcorper fringilla, mi nibh cursus felis, aliquet aliquam est ligula ut lacus. Suspendisse in lacus vitae urna ornare posuere ut nec massa. Curabitur maximus ullamcorper venenatis. Nulla pulvinar arcu a purus pulvinar rhoncus. ",
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
                selectImage();
            }
        });
        //result of picture in thumbnail
        image1 = (ImageView) findViewById(R.id.image1);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        map = (MapView) findViewById(R.id.map);
        map.onCreate(savedInstanceState);
        map.getMapAsync(this);

        // get POI name
        Intent from = getIntent();
        String poiName = from.getStringExtra("POI_NAME");

        // get POI from database
        Providers.database.getPoi(poiName, new DatabaseProvider.GetPoiListener() {
            @Override
            public void onSuccess(PointOfInterest poi) {

            }

            @Override
            public void onDoesntExist() {

            }

            @Override
            public void onFailure() {

            }
        });

        // get recycler view for reviews
        reviewsView = (RecyclerView) findViewById(R.id.reviews);
        reviewsLayout = new LinearLayoutManager(this);
        reviewsView.setLayoutManager(reviewsLayout);
        reviewsAdapter = new ReviewsAdapter(reviewsData);
        reviewsView.setAdapter(reviewsAdapter);

        // change text color
        TextView featured = (TextView) findViewById(R.id.featured);
        Spannable span = new SpannableString(featured.getText());
        span.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorPrimary)), 12, featured.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        featured.setText(span);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Akihabara marker
        LatLng mark = new LatLng(35.7022077, 139.7722703);
        googleMap.addMarker(new MarkerOptions().position(mark)
                .title("Akihabara"));
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
        //pop up box to chose how to take picture
        final CharSequence[] choice = {"Camera", "Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(POIPageActivity.this);



        if (ContextCompat.checkSelfPermission(POIPageActivity.this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            AndroidPermissions.promptCameraPermission(POIPageActivity.this);
        } else {
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
                e.printStackTrace();
            }
        }

        image1.setImageBitmap(image);
    }


    private void onCameraResult(Intent data) {

        Bitmap image = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        //(format, quality, outstream)
        image.compress(Bitmap.CompressFormat.JPEG, 90, bytes);


        //need to create the image file from the camera
        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream outstream;
        try {
            destination.createNewFile();
            outstream = new FileOutputStream(destination);
            outstream.write(bytes.toByteArray());
            outstream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        image1.setImageBitmap(image);
    }


}



