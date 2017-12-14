package ch.epfl.sweng.fiktion.providers.firebase_models;

import java.util.Date;

import ch.epfl.sweng.fiktion.models.posts.FavoritePOIPost;
import ch.epfl.sweng.fiktion.models.posts.Post;

/**
 * Created by painguin on 14.12.17.
 */

public class FirebaseFavoritePOIPost extends FirebasePost {
    public String poiName = "";

    public FirebaseFavoritePOIPost(FavoritePOIPost post) {
        super(post);
        this.poiName = post.getPOIName();
    }

    public FirebaseFavoritePOIPost() {
    }

    @Override
    public Post toPost() {
        return new FavoritePOIPost(poiName, new Date(milliseconds), id);
    }
}
