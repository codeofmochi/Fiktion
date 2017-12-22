package ch.epfl.sweng.fiktion.models.posts;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.security.NoSuchAlgorithmException;
import java.util.Date;

import ch.epfl.sweng.fiktion.R;
import ch.epfl.sweng.fiktion.providers.PhotoProvider;
import ch.epfl.sweng.fiktion.utils.HashUtils;
import ch.epfl.sweng.fiktion.views.POIPageActivity;

/**
 * A post that represents the upload of a photo for a poi
 *
 * @author pedro
 */
public class PhotoUploadPost extends Post {
    private final String photoName;
    private final String poiName;

    /**
     * creates a poi photo upload post
     *
     * @param photoName the id of the photo
     * @param poiName   the name of the poi
     * @param date      the date the photo was uploaded
     * @throws NoSuchAlgorithmException
     */
    public PhotoUploadPost(String photoName, String poiName, Date date) throws NoSuchAlgorithmException {
        this(photoName, poiName, date, HashUtils.sha256(photoName + poiName + date.getTime()));
    }

    /**
     * creates a poi photo upload post
     *
     * @param photoName the id of the photo
     * @param poiName   the name of the poi
     * @param date      the date the photo was uploaded
     * @param postId    the id of the post
     */
    public PhotoUploadPost(String photoName, String poiName, Date date, String postId) {
        super(PostType.PHOTO_UPLOAD, postId, date);
        this.photoName = photoName;
        this.poiName = poiName;
    }

    /**
     * get the id of the photo
     *
     * @return the id of the photo
     */
    public String getPhotoName() {
        return photoName;
    }

    /**
     * get the name of the poi
     *
     * @return the name of the poi
     */
    public String getPOIName() {
        return poiName;
    }

    @Override
    public View display(final Context ctx, String username) {
        final LinearLayout inner = new LinearLayout(ctx);
        inner.setOrientation(LinearLayout.VERTICAL);

        // header comment
        TextView header = new TextView(ctx);
        header.setTextColor(ctx.getResources().getColor(R.color.darkGray));
        String text = username + " took a picture in " + poiName + " : ";
        header.setText(text);
        header.setCompoundDrawablesWithIntrinsicBounds(R.drawable.camera_icon_20, 0, 0, 0);
        header.setCompoundDrawablePadding(5);
        inner.addView(header);

        // picture
        PhotoProvider.getInstance().downloadPOIBitmap(poiName, photoName, new PhotoProvider.DownloadBitmapListener() {
            @Override
            public void onFailure() { /* nothing */ }

            @Override
            public void onNewValue(Bitmap value) {
                ImageView img = new ImageView(ctx);
                img.setImageBitmap(value);
                img.setAdjustViewBounds(true);
                img.setCropToPadding(true);
                img.setPadding(10, 10, 10, 10);
                inner.addView(img);
            }
        });

        inner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ctx, POIPageActivity.class);
                i.putExtra("POI_NAME", poiName);
                ctx.startActivity(i);
            }
        });

        return super.display(inner, ctx);
    }
}
