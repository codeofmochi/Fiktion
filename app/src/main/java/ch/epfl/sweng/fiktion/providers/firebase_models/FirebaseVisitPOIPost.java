package ch.epfl.sweng.fiktion.providers.firebase_models;

import java.util.Date;

import ch.epfl.sweng.fiktion.models.posts.Post;
import ch.epfl.sweng.fiktion.models.posts.VisitPOIPost;

/**
 * Created by painguin on 14.12.17.
 */

public class FirebaseVisitPOIPost extends FirebasePost {
    public String poiName = "";

    public FirebaseVisitPOIPost(VisitPOIPost post) {
        super(post);
        this.poiName = post.getPOIName();
    }

    public FirebaseVisitPOIPost() {
    }

    @Override
    public Post toPost() {
        return new VisitPOIPost(poiName, new Date(milliseconds), id);
    }
}
