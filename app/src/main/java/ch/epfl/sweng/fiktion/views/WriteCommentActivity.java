package ch.epfl.sweng.fiktion.views;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.security.NoSuchAlgorithmException;
import java.util.Calendar;

import ch.epfl.sweng.fiktion.R;
import ch.epfl.sweng.fiktion.models.Comment;
import ch.epfl.sweng.fiktion.models.posts.CommentPOIPost;
import ch.epfl.sweng.fiktion.models.posts.Post;
import ch.epfl.sweng.fiktion.providers.DatabaseProvider;
import ch.epfl.sweng.fiktion.views.parents.MenuDrawerActivity;
import ch.epfl.sweng.fiktion.views.utils.POIDisplayer;
import ch.epfl.sweng.fiktion.views.utils.PhotoController;

/**
 * Activity which writes the comments, called from the POIPageActivity
 * Needs to receive the name of the point of interest and the userId
 * Button verifies if the field is empty
 * After successfully uploading the comment, it closes this Activity
 */

public class WriteCommentActivity extends MenuDrawerActivity {

    private String poiName;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // pass layout to parent
        this.includeLayout = R.layout.activity_write_comment;
        super.onCreate(savedInstanceState);

        Intent i = getIntent();
        poiName = i.getStringExtra(POIPageActivity.POI_NAME);
        userId = i.getStringExtra(POIPageActivity.USER_ID);

        // set title
        ((TextView) findViewById(R.id.poiTitle)).setText(poiName);
        // set bg
        findViewById(R.id.menu_scroll).setBackgroundColor(getResources().getColor(R.color.bgLightGray));

        // get image
        final ImageView img = (ImageView) findViewById(R.id.mainImage);
        final int imgWidth = 900;
        final int imgHeight = 400;
        img.setImageBitmap(POIDisplayer.cropAndScaleBitmapTo(BitmapFactory.decodeResource(getResources(), R.drawable.default_image), imgWidth, imgHeight));
        PhotoController.getPOIBitmaps(this, poiName, 1, new PhotoController.GetBitmapListener() {
            @Override
            public void onNewValue(Bitmap b) {
                img.setImageBitmap(POIDisplayer.cropAndScaleBitmapTo(b, imgWidth, imgHeight));
            }

            @Override
            public void onFailure() { /* nothing */ }
        });
    }

    public void uploadComment(View view) {

        String text = ((EditText) findViewById(R.id.comment)).getText().toString();
        if (text.isEmpty()) {
            ((EditText) findViewById(R.id.comment)).setError("You can't add an empty comment");
        } else {
            try {
                final Comment review = new Comment(text, userId, java.util.Calendar.getInstance().getTime(), 0);
                DatabaseProvider.getInstance().addComment(review, poiName, new DatabaseProvider.AddCommentListener() {
                    @Override
                    public void onSuccess() {
                        finish();

                        // add a post of the new comment
                        try {
                            Post post = new CommentPOIPost(review.getId(), poiName, Calendar.getInstance().getTime());
                            DatabaseProvider.getInstance().addUserPost(userId, post, new DatabaseProvider.AddPostListener() {
                                @Override
                                public void onFailure() {
                                }

                                @Override
                                public void onSuccess() {
                                }
                            });
                        } catch (NoSuchAlgorithmException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure() {
                    }
                });
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
    }
}
