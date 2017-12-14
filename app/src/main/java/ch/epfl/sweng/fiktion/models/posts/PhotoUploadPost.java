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
        this(photoId, poiName, date, HashUtils.sha256(photoId + poiName + date.getTime()));
    }

    /**
     * creates a poi photo upload post
     *
     * @param photoId the id of the photo
     * @param poiName the name of the poi
     * @param date    the date the photo was uploaded
     * @param postId  the id of the post
     */
    public PhotoUploadPost(String photoId, String poiName, Date date, String postId) {
        super(PostType.PHOTO_UPLOAD, postId, date);
        this.photoId = photoId;
        this.poiName = poiName;
    }

    /**
     * get the id of the photo
     *
     * @return the id of the photo
     */
    public String getPhotoId() {
        return photoId;
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
