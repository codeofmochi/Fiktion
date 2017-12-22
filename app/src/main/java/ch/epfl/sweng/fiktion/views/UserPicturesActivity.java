package ch.epfl.sweng.fiktion.views;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.sweng.fiktion.R;
import ch.epfl.sweng.fiktion.models.posts.PhotoUploadPost;
import ch.epfl.sweng.fiktion.models.posts.Post;
import ch.epfl.sweng.fiktion.models.posts.PostType;
import ch.epfl.sweng.fiktion.providers.DatabaseProvider;
import ch.epfl.sweng.fiktion.providers.PhotoProvider;
import ch.epfl.sweng.fiktion.views.utils.POIDisplayer;

/**
 * User pictures activity class
 */
public class UserPicturesActivity extends AppCompatActivity {

    /**
     * Image adapter for grid view
     */
    public class ImageAdapter extends BaseAdapter {
        private Context mContext;
        private List<Bitmap> images;

        public ImageAdapter(Context c, List<Bitmap> images) {
            mContext = c;
            this.images = images;
        }

        public int getCount() {
            return images.size();
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {
                // if it's not recycled, initialize some attributes
                imageView = new ImageView(mContext);
                imageView.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                imageView.setPadding(5, 10, 5, 0);
            } else {
                imageView = (ImageView) convertView;
            }

            Bitmap pic = images.get(position);
            pic = POIDisplayer.cropBitmapToSquare(pic);
            pic = POIDisplayer.scaleBitmap(pic, 500);
            imageView.setAdjustViewBounds(true);
            imageView.setImageBitmap(pic);
            return imageView;
        }
    }

    String userId;
    ProfileActivity.Action state;
    List<Bitmap> photos;
    GridView photosGrid;
    ImageAdapter adapter;
    Context ctx = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_pictures);

        // get extra data
        Intent from = getIntent();
        // get user id
        userId = from.getStringExtra(ProfileActivity.USER_ID_KEY);
        if (userId == null) return;
        // get state
        String stateString = from.getStringExtra(ProfileActivity.PROFILE_ACTION_KEY);
        if (stateString == null) return;
        if (stateString.equals(ProfileActivity.PROFILE_ACTION_ME))
            state = ProfileActivity.Action.MY_PROFILE;
        else state = ProfileActivity.Action.ANOTHER_PROFILE;

        // init array list of photos
        photos = new ArrayList<>();
        adapter = new ImageAdapter(this, photos);

        // get photos grid
        photosGrid = (GridView) findViewById(R.id.photos_grid);
        photosGrid.setAdapter(adapter);
        photosGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                FullscreenPictureActivity.showBitmapInFullscreen(ctx, photos.get(position));
            }
        });

        // get the pictures
        DatabaseProvider.getInstance().getUserPosts(userId, new DatabaseProvider.GetPostListener() {
            @Override
            public void onFailure() { /* nothing */ }

            @Override
            public void onNewValue(Post post) {
                if (post.getType() == PostType.PHOTO_UPLOAD) {
                    PhotoUploadPost pic = (PhotoUploadPost) post;

                    // hide default message
                    findViewById(R.id.default_empty_text).setVisibility(View.GONE);

                    // fetch picture
                    PhotoProvider.getInstance().downloadPOIBitmap(pic.getPOIName(), pic.getPhotoName(), new PhotoProvider.DownloadBitmapListener() {
                        @Override
                        public void onFailure() { /* show nothing */ }

                        @Override
                        public void onNewValue(Bitmap bitmap) {
                            photos.add(bitmap);
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });
    }
}
