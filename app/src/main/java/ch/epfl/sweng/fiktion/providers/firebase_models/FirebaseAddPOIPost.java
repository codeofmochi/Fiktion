package ch.epfl.sweng.fiktion.providers.firebase_models;

import java.util.Date;

import ch.epfl.sweng.fiktion.models.posts.AddPOIPost;
import ch.epfl.sweng.fiktion.models.posts.Post;
import ch.epfl.sweng.fiktion.models.posts.PostType;

/**
 * Created by painguin on 14.12.17.
 */

public class FirebaseAddPOIPost extends FirebasePost {
    public String poiName = "";

    public FirebaseAddPOIPost(AddPOIPost post) {
        super(post);
        this.poiName = post.getPOIName();
    }

    public FirebaseAddPOIPost() {
    }

    @Override
    public Post toPost() {
        return new AddPOIPost(poiName, new Date(milliseconds), id);
    }
}
