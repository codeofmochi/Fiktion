package ch.epfl.sweng.fiktion.models.posts;

import java.security.NoSuchAlgorithmException;
import java.util.Date;

import ch.epfl.sweng.fiktion.utils.HashUtils;

/**
 * Post that represents the addition of a poi
 *
 * @author pedro
 */
public class AddPOIPost extends Post {
    private final String poiName;

    /**
     * creates a poi addition post
     *
     * @param poiName the name of the poi
     * @param date    the date of the poi addition
     * @throws NoSuchAlgorithmException
     */
    public AddPOIPost(String poiName, Date date) throws NoSuchAlgorithmException {
        this(poiName, date, HashUtils.sha256(poiName + date.getTime()));
    }

    /**
     * creates a poi addition post
     *
     * @param poiName the name of the poi
     * @param date    the date of the poi addition
     * @param postId  the id of the post
     */
    public AddPOIPost(String poiName, Date date, String postId) {
        super(PostType.ADD_POI, postId, date);
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