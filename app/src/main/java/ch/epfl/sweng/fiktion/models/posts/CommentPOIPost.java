package ch.epfl.sweng.fiktion.models.posts;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.security.NoSuchAlgorithmException;
import java.util.Date;

import ch.epfl.sweng.fiktion.R;
import ch.epfl.sweng.fiktion.models.Comment;
import ch.epfl.sweng.fiktion.providers.DatabaseProvider;
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
    public View display(final Context ctx, String username) {
        final LinearLayout inner = new LinearLayout(ctx);
        inner.setOrientation(LinearLayout.VERTICAL);

        // header comment
        TextView header = new TextView(ctx);
        header.setTextColor(ctx.getResources().getColor(R.color.darkGray));
        header.setText(username + " added this place :");
        header.setCompoundDrawablesWithIntrinsicBounds(R.drawable.rate_review_icon_20, 0, 0, 0);
        header.setCompoundDrawablePadding(5);
        inner.addView(header);

        // comment text
        DatabaseProvider.getInstance().getComment(commentId, new DatabaseProvider.GetCommentListener() {
            @Override
            public void onNewValue(Comment value) {
                TextView t = new TextView(ctx);
                t.setText(value.getText());
                t.setPadding(10, 10, 10, 10);
                inner.addView(t);
            }

            @Override
            public void onDoesntExist() { /* nothing*/ }

            @Override
            public void onFailure() { /* nothing*/ }

            @Override
            public void onModifiedValue(Comment value) { /* nothing*/ }
        });

        return super.display(inner, ctx);
    }
}
