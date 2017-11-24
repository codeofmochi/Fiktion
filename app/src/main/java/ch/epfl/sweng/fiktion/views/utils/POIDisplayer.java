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
import ch.epfl.sweng.fiktion.providers.PhotoProvider;
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
    @SuppressWarnings("SetTextI18n")
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

        final ImageView img = new ImageView(ctx);
        // Load a default picture
        Bitmap b = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.default_image);
        processAndPutImage(img, b);
        // Try to load a picture for this poi from DB
        PhotoProvider.getInstance().downloadPOIBitmaps(
                poi.name(),
                1,
                new PhotoProvider.DownloadBitmapListener() {
                    @Override
                    public void onNewPhoto(Bitmap b) {
                        processAndPutImage(img, b);
                    }

                    @Override
                    public void onFailure() {
                        // give up and do nothing
                    }
                });
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
     * Transforms a bitmap into appropriate size and replace the ImageView's content
     *
     * @param img An ImageView in which we want to put the picture
     * @param b   An image that we want to resize and display
     */
    private static void processAndPutImage(ImageView img, Bitmap b) {
        // Scale the image to avoid heavy computations
        Bitmap res = POIDisplayer.scaleBitmap(b, IMAGE_SIZE);
        // crop to a centered square, computed from the min(width, height) of the image
        res = POIDisplayer.cropBitmapToSquare(res);
        // Put the picture in the ImageView
        img.setImageBitmap(res);
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
     * Takes a bitmap of any size which will be rescaled and cropped to fit to (width x height)
     *
     * @param b      The bitmap to be rescaled
     * @param width  The width of the final bitmap
     * @param height The height of the final bitmap
     * @return the modified bitmap
     */
    public static Bitmap cropAndScaleBitmapTo(Bitmap b, int width, int height) {
        Bitmap rescaled = scaleBitmap(b, height);
        if (b.getWidth() > b.getHeight() && rescaled.getWidth() >= width) {
            // horizontal and width is big enough : rescale height and then crop width
            return cropWidth(rescaled, width);
        } else {
            // in all other cases, rescale width and then crop height
            return cropHeight(scaleWidthTo(b, width), height);
        }
    }

    /**
     * Takes a bitmap and rescale it so that its length match the one given
     *
     * @param b     a bitmap to rescale
     * @param width the width of the final bitmap
     * @return the rescaled bitmap
     */
    public static Bitmap scaleWidthTo(Bitmap b, int width) {
        float ratio = 1f * width / b.getWidth();
        int newHeight = (int) Math.floor(ratio * b.getHeight());
        return Bitmap.createScaledBitmap(b, width, newHeight, false);
    }

    /**
     * Takes a bitmap and crop its width if the given width is smaller than the bitmap's width
     *
     * @param b     the bitmap which we want to crop the width
     * @param width the width we want the crop to be
     * @return the bitmap with cropped width
     */
    public static Bitmap cropWidth(Bitmap b, int width) {
        // bitmap is smaller than width : nothing to change
        if (b.getWidth() <= width) return b;
        else {
            // else crop the width
            int startX = (b.getWidth() - width) / 2;
            return Bitmap.createBitmap(b, startX, 0, width, b.getHeight());
        }
    }

    /**
     * Takes a bitmap and crop its height if the given height is smaller than the bitmap's height
     *
     * @param b      the bitmap which we want to crop the height
     * @param height the height we want the crop to be
     * @return the bitmap with cropped height
     */
    public static Bitmap cropHeight(Bitmap b, int height) {
        // bitmap is smaller than height : nothing to change
        if (b.getHeight() <= height) return b;
        else {
            // else crop the height
            int startY = (b.getHeight() - height) / 2;
            return Bitmap.createBitmap(b, 0, startY, b.getWidth(), height);
        }
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
