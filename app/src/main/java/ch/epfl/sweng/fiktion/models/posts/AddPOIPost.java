package ch.epfl.sweng.fiktion.models.posts;

import java.util.Date;

/**
 * Created by painguin on 14.12.17.
 */

public class AddPOIPost extends Post {

    public AddPOIPost(String poiName, Date date) {
        super(PostType.ADD_POI, poiName, date);
    }
}
