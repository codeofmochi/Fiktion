package ch.epfl.sweng.fiktion.models.posts;

import java.util.Date;

/**
 * Created by painguin on 14.12.17.
 */

public class CommentPOIPost extends Post {
    private final String poiName;

    public CommentPOIPost(String commentId, String poiName, Date date) {
        super(PostType.COMMENT_POI, commentId, date);
        this.poiName = poiName;
    }
}
