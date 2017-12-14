package ch.epfl.sweng.fiktion.models.posts;

import java.security.NoSuchAlgorithmException;
import java.util.Date;

import ch.epfl.sweng.fiktion.utils.HashUtils;

/**
 * A post that represents the addition of a comment to a poi
 *
 * @author pedro
 */
public class CommentPOIPost extends Post {
    private final String commentId;
    private final String poiName;

    /**
     * creates a poi comment post
     *
     * @param commentId the comment id
     * @param poiName   the name of the poi
     * @param date      the date of the comment
     * @throws NoSuchAlgorithmException
     */
    public CommentPOIPost(String commentId, String poiName, Date date) throws NoSuchAlgorithmException {
        super(PostType.COMMENT_POI, HashUtils.sha256(commentId + poiName + date.getTime()), date);
        this.commentId = commentId;
        this.poiName = poiName;
    }
}
