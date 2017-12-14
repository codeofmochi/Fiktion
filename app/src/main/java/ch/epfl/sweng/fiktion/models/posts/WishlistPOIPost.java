package ch.epfl.sweng.fiktion.models.posts;

import java.security.NoSuchAlgorithmException;
import java.util.Date;

import ch.epfl.sweng.fiktion.utils.HashUtils;

/**
 * A post that represents the addition of a poi to the wishlist
 *
 * @author pedro
 */
public class WishlistPOIPost extends Post {
    private final String poiName;

    /**
     * creates a poi wishlist post
     *
     * @param poiName the name of the poi
     * @param date    the date the poi was added to the wishlist
     * @throws NoSuchAlgorithmException
     */
    public WishlistPOIPost(String poiName, Date date) throws NoSuchAlgorithmException {
        this(poiName, date, HashUtils.sha256(poiName + date.getTime()));
    }

    /**
     * creates a poi wishlist post
     *
     * @param poiName the name of the poi
     * @param date    the date the poi was added to the wishlist
     * @param postId  the id of the post
     */
    public WishlistPOIPost(String poiName, Date date, String postId) {
        super(PostType.WISHLIST_POI, postId, date);
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
}
