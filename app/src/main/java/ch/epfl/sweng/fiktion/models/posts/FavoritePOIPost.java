package ch.epfl.sweng.fiktion.models.posts;

import java.util.Date;

/**
 * Created by painguin on 14.12.17.
 */

public class FavoritePOIPost extends Post {

    public FavoritePOIPost(String poiName, Date date) {
        super(PostType.FAVORITE_POI, poiName, date);
    }
}
