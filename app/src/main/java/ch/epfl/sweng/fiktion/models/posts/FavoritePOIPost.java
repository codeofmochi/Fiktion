package ch.epfl.sweng.fiktion.models.posts;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.security.NoSuchAlgorithmException;
import java.util.Date;

import ch.epfl.sweng.fiktion.R;
import ch.epfl.sweng.fiktion.models.PointOfInterest;
import ch.epfl.sweng.fiktion.providers.DatabaseProvider;
import ch.epfl.sweng.fiktion.utils.HashUtils;
import ch.epfl.sweng.fiktion.views.utils.POIDisplayer;

/**
 * A post that represents the addition of a poi to the favorites
 *
 * @author pedro
 */
public class FavoritePOIPost extends Post {
    private final String poiName;

    /**
     * creates a poi favorite post
     *
     * @param poiName the name of the poi
     * @param date    the date the poi was added to the favorites
     * @throws NoSuchAlgorithmException
     */
    public FavoritePOIPost(String poiName, Date date) throws NoSuchAlgorithmException {
        this(poiName, date, HashUtils.sha256(poiName + date.getTime()));
    }

    /**
     * creates a poi favorite post
     *
     * @param poiName the name of the poi
     * @param date    the date the poi was added to the favorites
     * @param postId  the id of the post
     */
    public FavoritePOIPost(String poiName, Date date, String postId) {
        super(PostType.FAVORITE_POI, postId, date);
        this.poiName = poiName;
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
        String text = username + " added this place to his favorites :";
        header.setText(text);
        header.setCompoundDrawablesWithIntrinsicBounds(R.drawable.star_icon_20, 0, 0, 0);
        header.setCompoundDrawablePadding(5);
        inner.addView(header);

        // main content
        DatabaseProvider.getInstance().getPOI(poiName, new DatabaseProvider.GetPOIListener() {
            @Override
            public void onNewValue(PointOfInterest poi) {
                View card = POIDisplayer.createPoiCard(poi, ctx);
                // margin
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                card.setLayoutParams(params);
                // padding
                card.setPadding(10, 10, 10, 10);
                // shadow
                card.setElevation(0);
                card.setLayoutParams(params);
                inner.addView(card);
            }

            @Override
            public void onDoesntExist() { /* nothing */ }

            @Override
            public void onFailure() { /* nothing */ }

            @Override
            public void onModifiedValue(PointOfInterest value) { /* nothing */ }
        });

        // insert in parent layout
        return super.display(inner, ctx);
    }
}
