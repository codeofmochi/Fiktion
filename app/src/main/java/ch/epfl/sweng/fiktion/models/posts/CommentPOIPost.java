package ch.epfl.sweng.fiktion.models.posts;

import android.content.Context;
import android.view.View;

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
        this(commentId, poiName, date, HashUtils.sha256(commentId + poiName + date.getTime()));
    }

    /**
     * creates a poi comment post
     *
     * @param commentId the comment id
     * @param poiName   the name of the poi
     * @param date      the date of the comment
     * @param postId    the id of the post
     */
    public CommentPOIPost(String commentId, String poiName, Date date, String postId) {
        super(PostType.COMMENT_POI, postId, date);
        this.commentId = commentId;
        this.poiName = poiName;
    }

    /**
     * get the id of the comment
     *
     * @return the id of the comment
     */
    public String getCommentId() {
        return commentId;
    }

    /**
     * get the name of the poi
     *
     * @return the name of the poi
     */
    public String getPOIName() {
        return poiName;
    }

    @Override
    public View display(Context ctx, String username) {
        return null;
    }
}
