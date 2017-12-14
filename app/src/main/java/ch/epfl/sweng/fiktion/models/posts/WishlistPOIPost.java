package ch.epfl.sweng.fiktion.models.posts;

import java.util.Date;

/**
 * Created by painguin on 14.12.17.
 */

public class WishlistPOIPost extends Post {

    public WishlistPOIPost(String poiName, Date date) {
        super(PostType.WISHLIST_POI, poiName, date);
    }
}
