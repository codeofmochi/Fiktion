package ch.epfl.sweng.fiktion.views.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import ch.epfl.sweng.fiktion.R;
import ch.epfl.sweng.fiktion.models.PointOfInterest;

/**
 * Utility class for creating POI UI that can be reused anywhere
 * Created by alexandre on 18.11.17.
 */

public class POIDisplayer {

    /**
     * Creates a card view of a POI that can be dynamically added in any layout
     *
     * @param poi A poi which we want a card UI
     * @param ctx The context of the call, the activity where we want to include the card
     * @return A view of a POI that can be added in any layout
     */
    public static View createPoiCard(PointOfInterest poi, Context ctx) {
        // create new view for this POI
        LinearLayout v = new LinearLayout(ctx);
        v.setOrientation(LinearLayout.HORIZONTAL);

        /* styles */

        // background color
        v.setBackgroundColor(ctx.getResources().getColor(R.color.white));
        // margin
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(2, 10, 2, 10);
        v.setLayoutParams(params);
        // padding
        v.setPadding(20, 20, 20, 20);
        // shadow
        v.setElevation(2);


        /* add picture */

        ImageView img = new ImageView(ctx);
        // Get the image here
        Bitmap b = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.akibairl2);
        // Recrop to a centered square, computed from the min(width, height) of the image
        b = POIDisplayer.cropBitmapToSquare(b);
        img.setImageBitmap(b);
        // Define size
        img.setMaxHeight(250);
        img.setMaxWidth(250);
        img.setAdjustViewBounds(true);
        img.setCropToPadding(false);
        // finally add to horizontal layout
        v.addView(img);


        /* set attributes */
        LinearLayout texts = new LinearLayout(ctx);
        LinearLayout.LayoutParams textsParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textsParams.setMarginStart(20);

        // Title text
        TextView title = new TextView(ctx);
        title.setTextSize(18);
        title.setText(poi.name());
        title.setTypeface(null, Typeface.BOLD);
        // add title to texts
        texts.addView(title);

        // put texts in horizontal layout
        v.addView(texts);

        return v;
    }

    /**
     * Crops a given bitmap into its centered square, which length is computed from min(width, height)
     *
     * @param b The bitmap to crop
     * @return The bitmap but cropped and centered
     */
    public static Bitmap cropBitmapToSquare(Bitmap b) {
        // the length of the square is the smallest between the width and the height
        int length = Math.min(b.getWidth(), b.getHeight());
        // we can set the start of the crop X and Y
        int startX = (length == b.getWidth()) ? 0 : ((b.getWidth() - length) / 2);
        int startY = (length == b.getHeight()) ? 0 : ((b.getHeight() - length) / 2);
        // crop the bitmap
        return Bitmap.createBitmap(b, startX, startY, length, length);
    }
}
