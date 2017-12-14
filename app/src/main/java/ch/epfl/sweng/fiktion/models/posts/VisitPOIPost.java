package ch.epfl.sweng.fiktion.models.posts;

import java.security.NoSuchAlgorithmException;
import java.util.Date;

import ch.epfl.sweng.fiktion.utils.HashUtils;

/**
 * A post that represents the visit of a poi
 *
 * @author pedro
 */
public class VisitPOIPost extends Post {
    private final String poiName;

    /**
     * creates a poi visit post
     *
     * @param poiName the name of the poi
     * @param date    the date the poi was visited
     * @throws NoSuchAlgorithmException
     */
    public VisitPOIPost(String poiName, Date date) throws NoSuchAlgorithmException {
        this(poiName, date, HashUtils.sha256(poiName + date.getTime()));
    }

    /**
     * creates a poi visit post
     *
     * @param poiName the name of the poi
     * @param date    the date the poi was visited
     * @param postId  the id of the post
     */
    public VisitPOIPost(String poiName, Date date, String postId) {
        super(PostType.VISIT_POI, postId, date);
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
