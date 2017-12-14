package ch.epfl.sweng.fiktion.providers.firebase_models;

import java.util.Date;

import ch.epfl.sweng.fiktion.models.posts.PhotoUploadPost;
import ch.epfl.sweng.fiktion.models.posts.Post;

/**
 * Created by painguin on 14.12.17.
 */

public class FirebasePhotoUploadPost extends FirebasePost {
    public String photoId = "";
    public String poiName = "";

    public FirebasePhotoUploadPost(PhotoUploadPost post) {
        super(post);
        this.photoId = post.getPhotoId();
        this.poiName = post.getPOIName();
    }

    public FirebasePhotoUploadPost() {
    }

    @Override
    public Post toPost() {
        return new PhotoUploadPost(photoId, poiName, new Date(milliseconds), id);
    }
}
