package ch.epfl.sweng.fiktion.providers.firebase_models;

import java.util.Date;

import ch.epfl.sweng.fiktion.models.posts.AddPOIPost;
import ch.epfl.sweng.fiktion.models.posts.Post;

import static ch.epfl.sweng.fiktion.providers.FirebaseDatabaseProvider.decode;
import static ch.epfl.sweng.fiktion.providers.FirebaseDatabaseProvider.encode;

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
        this.poiName = encode(post.getPOIName());
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
        return new AddPOIPost(decode(poiName), new Date(milliseconds), decode(id));
    }
}
