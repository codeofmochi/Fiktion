package ch.epfl.sweng.fiktion.models.posts;

import java.security.NoSuchAlgorithmException;
import java.util.Date;

import ch.epfl.sweng.fiktion.utils.HashUtils;

/**
 * A post that represents the upload of a photo for a poi
 *
 * @author pedro
 */
public class PhotoUploadPost extends Post {
    private final String photoId;
    private final String poiName;

    /**
     * creates a poi photo upload post
     *
     * @param photoId the id of the photo
     * @param poiName the name of the poi
     * @param date    the date the photo was uploaded
     * @throws NoSuchAlgorithmException
     */
    public PhotoUploadPost(String photoId, String poiName, Date date) throws NoSuchAlgorithmException {
        super(PostType.PHOTO_UPLOAD, HashUtils.sha256(photoId + poiName + date.getTime()), date);
        this.photoId = photoId;
        this.poiName = poiName;
    }
}
