package ch.epfl.sweng.fiktion.providers.firebase_models;

import java.util.Date;

import ch.epfl.sweng.fiktion.models.posts.Post;
import ch.epfl.sweng.fiktion.models.posts.WishlistPOIPost;

/**
 * Created by painguin on 14.12.17.
 */

public class FirebaseWishlistPOIPost extends FirebasePost {
    public String poiName = "";

    public FirebaseWishlistPOIPost(WishlistPOIPost post) {
        super(post);
        this.poiName = post.getPOIName();
    }

    public FirebaseWishlistPOIPost() {
    }

    @Override
    public Post toPost() {
        return new WishlistPOIPost(poiName, new Date(milliseconds), id);
    }
}
