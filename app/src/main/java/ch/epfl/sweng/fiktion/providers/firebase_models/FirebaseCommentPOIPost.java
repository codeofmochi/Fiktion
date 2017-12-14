package ch.epfl.sweng.fiktion.providers.firebase_models;

import java.util.Date;

import ch.epfl.sweng.fiktion.models.posts.CommentPOIPost;
import ch.epfl.sweng.fiktion.models.posts.Post;

/**
 * Created by painguin on 14.12.17.
 */

public class FirebaseCommentPOIPost extends FirebasePost {
    public String commentId = "";
    public String poiName = "";

    public FirebaseCommentPOIPost(CommentPOIPost post) {
        super(post);
        this.commentId = post.getCommentId();
        this.poiName = post.getPOIName();
    }

    public FirebaseCommentPOIPost() {
    }

    @Override
    public Post toPost() {
        return new CommentPOIPost(commentId, poiName, new Date(milliseconds), id);
    }
}
