package ch.epfl.sweng.fiktion.providers.firebase_models;

import java.util.Date;

import ch.epfl.sweng.fiktion.models.posts.AddPOIPost;
import ch.epfl.sweng.fiktion.models.posts.Post;

/**
 * A AddPOIPost implementation for Firebase
 *
 * @author pedro
 */
public class FirebaseAddPOIPost extends FirebasePost {
    public String poiName = "";

    /**
     * Constructs a Firebase AddPOIPost
     *
     * @param post a AddPOIPost
     */
    public FirebaseAddPOIPost(AddPOIPost post) {
        super(post);
        this.poiName = post.getPOIName();
    }

    /**
     * Default constructor for calls to DataSnapshot.getValue(FirebaseAddPOIPost.class)
     */
    public FirebaseAddPOIPost() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Post toPost() {
        return new AddPOIPost(poiName, new Date(milliseconds), id);
    }
}
