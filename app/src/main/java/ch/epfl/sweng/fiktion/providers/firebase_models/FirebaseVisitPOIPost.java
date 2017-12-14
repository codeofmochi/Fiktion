package ch.epfl.sweng.fiktion.providers.firebase_models;

import java.util.Date;

import ch.epfl.sweng.fiktion.models.posts.Post;
import ch.epfl.sweng.fiktion.models.posts.VisitPOIPost;

/**
 * A VisitPOIPost implementation for Firebase
 *
 * @author pedro
 */
public class FirebaseVisitPOIPost extends FirebasePost {
    public String poiName = "";

    /**
     * Constructs a Firebase VisitPOIPost
     *
     * @param post a VisitPOIPost
     */
    public FirebaseVisitPOIPost(VisitPOIPost post) {
        super(post);
        this.poiName = post.getPOIName();
    }

    /**
     * Default constructor for calls to DataSnapshot.getValue(FirebaseVisitPOIPost.class)
     */
    public FirebaseVisitPOIPost() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Post toPost() {
        return new VisitPOIPost(poiName, new Date(milliseconds), id);
    }
}
