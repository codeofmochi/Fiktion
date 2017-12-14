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
        super(PostType.ADD_POI, HashUtils.sha256(poiName + date.getTime()), date);
        this.poiName = poiName;
    }
}
