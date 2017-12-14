package ch.epfl.sweng.fiktion.providers.firebase_models;

import java.util.Date;

import ch.epfl.sweng.fiktion.models.posts.Post;
import ch.epfl.sweng.fiktion.models.posts.WishlistPOIPost;

/**
 * A WishlistPOIPost implementation for Firebase
 *
 * @author pedro
 */
public class FirebaseWishlistPOIPost extends FirebasePost {
    public String poiName = "";

    /**
     * Constructs a Firebase WishlistPOIPost
     *
     * @param post a WishlistPOIPost
     */
    public FirebaseWishlistPOIPost(WishlistPOIPost post) {
        super(post);
        this.poiName = post.getPOIName();
    }

    /**
     * Default constructor for calls to DataSnapshot.getValue(FirebaseWishlistPOIPost.class)
     */
    public FirebaseWishlistPOIPost() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Post toPost() {
        return new WishlistPOIPost(poiName, new Date(milliseconds), id);
    }
}
