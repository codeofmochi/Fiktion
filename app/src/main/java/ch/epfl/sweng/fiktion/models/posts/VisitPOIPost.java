package ch.epfl.sweng.fiktion.models.posts;

import java.util.Date;

/**
 * Created by painguin on 14.12.17.
 */

public class VisitPOIPost extends Post {

    public VisitPOIPost(String poiName, Date date) {
        super(PostType.VISIT_POI, poiName, date);
    }
}
