package ch.epfl.sweng.fiktion.providers.firebase_models;

import java.util.Date;

import ch.epfl.sweng.fiktion.models.posts.CommentPOIPost;
import ch.epfl.sweng.fiktion.models.posts.Post;

import static ch.epfl.sweng.fiktion.providers.FirebaseDatabaseProvider.decode;
import static ch.epfl.sweng.fiktion.providers.FirebaseDatabaseProvider.encode;

/**
 * Created by painguin on 14.12.17.
 */

public class FirebaseCommentPOIPost extends FirebasePost {
    public String commentId = "";
    public String poiName = "";

    public FirebaseCommentPOIPost(CommentPOIPost post) {
        super(post);
        this.commentId = encode(post.getCommentId());
        this.poiName = encode(post.getPOIName());
    }

    public FirebaseCommentPOIPost() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Post toPost() {
        return new CommentPOIPost(decode(commentId), decode(poiName), new Date(milliseconds), decode(id));
    }
}
