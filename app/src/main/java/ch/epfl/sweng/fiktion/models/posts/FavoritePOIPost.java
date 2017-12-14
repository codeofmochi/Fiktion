package ch.epfl.sweng.fiktion.models.posts;

import java.security.NoSuchAlgorithmException;
import java.util.Date;

import ch.epfl.sweng.fiktion.utils.HashUtils;

/**
 * A post that represents the addition of a poi to the favorites
 *
 * @author pedro
 */
public class FavoritePOIPost extends Post {
    private final String poiName;

    /**
     * creates a poi favorite post
     *
     * @param poiName the name of the poi
     * @param date    the date the poi was added to the favorites
     * @throws NoSuchAlgorithmException
     */
    public FavoritePOIPost(String poiName, Date date) throws NoSuchAlgorithmException {
        super(PostType.FAVORITE_POI, HashUtils.sha256(poiName + date.getTime()), date);
        this.poiName = poiName;
    }
}
