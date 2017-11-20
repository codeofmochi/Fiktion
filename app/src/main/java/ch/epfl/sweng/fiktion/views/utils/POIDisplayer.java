package ch.epfl.sweng.fiktion.views.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Iterator;
import java.util.Set;

import ch.epfl.sweng.fiktion.R;
import ch.epfl.sweng.fiktion.models.PointOfInterest;
import ch.epfl.sweng.fiktion.views.POIPageActivity;

/**
 * Utility class for creating POI UI that can be reused anywhere
 * Created by alexandre on 18.11.17.
 */

public class POIDisplayer {
    @SuppressWarnings("FieldCanBeLocal") // might be used elsewhere
    private static int IMAGE_SIZE = 250;

    /**
     * Creates a card view of a POI that can be dynamically added in any layout
     *
     * @param poi A poi which we want a card UI
     * @param ctx The context of the call, the activity where we want to include the card
     * @return A view of a POI that can be added in any layout
     */
    public static View createPoiCard(final PointOfInterest poi, final Context ctx) {
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
        // Scale it to avoid heavy computations
        b = POIDisplayer.scaleBitmap(b, IMAGE_SIZE);
        // crop to a centered square, computed from the min(width, height) of the image
        b = POIDisplayer.cropBitmapToSquare(b);
        img.setImageBitmap(b);
        // Define size
        img.setMaxHeight(IMAGE_SIZE);
        img.setMaxWidth(IMAGE_SIZE);
        img.setAdjustViewBounds(true);
        img.setCropToPadding(false);
        // finally add to horizontal layout
        v.addView(img);


        /* set attributes */
        LinearLayout texts = new LinearLayout(ctx);
        texts.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams textsParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textsParams.setMarginStart(20);
        texts.setLayoutParams(textsParams);

        // Title text
        TextView title = new TextView(ctx);
        title.setTextSize(18);
        title.setText(poi.name());
        title.setTypeface(null, Typeface.BOLD);
        // add title to texts
        texts.addView(title);

        // City and country text
        TextView cityCountry = new TextView(ctx);
        cityCountry.setTextSize(14);
        cityCountry.setText(poi.city() + ", " + poi.country());
        texts.addView(cityCountry);

        // List of fictions
        TextView fictions = new TextView(ctx);
        fictions.setTextSize(13);
        fictions.setPadding(0, 10, 0, 10);
        // make a string of up to 5 fictions
        Spannable f = POIDisplayer.makeFictionsString(poi.fictions(), 5, ctx);
        fictions.setText(f);
        texts.addView(fictions);

        // Rating
        TextView rating = new TextView(ctx);
        rating.setTextSize(13);
        rating.setPadding(0, 10, 0, 10);
        String r = poi.rating() + " upvotes";
        rating.setText(r);
        texts.addView(rating);

        // put texts in horizontal layout
        v.addView(texts);

        // register onclick event
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // open the correct POI page on click
                Intent i = new Intent(ctx, POIPageActivity.class);
                i.putExtra("POI_NAME", poi.name());
                ctx.startActivity(i);
            }
        });

        // finally return the whole view
        return v;
    }

    /**
     * Scales a bitmap given its min(width, length)
     *
     * @param b         The bitmap to scale
     * @param imageSize The length of the shortest size min(width, height)
     * @return a bitmap which shortest side is scaled to imageSize
     */
    public static Bitmap scaleBitmap(Bitmap b, int imageSize) {
        int min = Math.min(b.getWidth(), b.getHeight());
        int x = (min == b.getWidth()) ? imageSize : b.getWidth() * imageSize / b.getHeight();
        int y = (min == b.getHeight()) ? imageSize : b.getHeight() * imageSize / b.getWidth();
        return Bitmap.createScaledBitmap(b, x, y, false);
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

    /**
     * Makes a string from a set of fictions, up to a max qty
     *
     * @param fictions A set of fictions
     * @param max      Max number of fictions to display in string
     * @return a string of up to max fictions
     */
    public static Spannable makeFictionsString(Set<String> fictions, int max, Context ctx) {
        // iterate on set and add to string if we haven't reached max yet
        StringBuilder s = new StringBuilder("Featured in ");
        int count = 0;
        Iterator it = fictions.iterator();
        while (it.hasNext() && count < max) {
            if (count != 0) s.append(", ");
            s.append(it.next());
            count++;
        }
        // get some colors in there
        Spannable span = new SpannableString(s);
        span.setSpan(new ForegroundColorSpan(ctx.getResources().getColor(R.color.colorPrimary)), 12, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return span;
    }
}
