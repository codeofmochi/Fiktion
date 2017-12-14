package ch.epfl.sweng.fiktion.models.posts;

import java.util.Date;

/**
 * Created by painguin on 14.12.17.
 */

public class PhotoUploadPost extends Post {
    private final String poiName;

    public PhotoUploadPost(String photoId, String poiName, Date date) {
        super(PostType.PHOTO_UPLOAD, photoId, date);
        this.poiName = poiName;
    }
}
