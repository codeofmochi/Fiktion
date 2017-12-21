package ch.epfl.sweng.fiktion.models.posts;

import android.content.Context;
import android.view.View;

import java.security.NoSuchAlgorithmException;
import java.util.Date;

import ch.epfl.sweng.fiktion.utils.HashUtils;

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
    public View display(Context ctx, String username) {
        return null;
    }
}
