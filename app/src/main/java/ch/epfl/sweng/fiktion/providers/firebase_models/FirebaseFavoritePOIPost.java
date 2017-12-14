package ch.epfl.sweng.fiktion.providers.firebase_models;

import java.util.Date;

import ch.epfl.sweng.fiktion.models.posts.FavoritePOIPost;
import ch.epfl.sweng.fiktion.models.posts.Post;

/**
 * A FavoritePOIPost implementation for Firebase
 *
 * @author pedro
 */
public class FirebaseFavoritePOIPost extends FirebasePost {
    public String poiName = "";

    /**
     * Constructs a Firebase FavoritePOIPost
     *
     * @param post a FavoritePOIPost
     */
    public FirebaseFavoritePOIPost(FavoritePOIPost post) {
        super(post);
        this.poiName = post.getPOIName();
    }

    /**
     * Default constructor for calls to DataSnapshot.getValue(FirebaseFavoritePOIPost.class)
     */
    public FirebaseFavoritePOIPost() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Post toPost() {
        return new FavoritePOIPost(poiName, new Date(milliseconds), id);
    }
}
