package ch.epfl.sweng.fiktion.providers.firebase_models;

import java.util.Date;

import ch.epfl.sweng.fiktion.models.posts.PhotoUploadPost;
import ch.epfl.sweng.fiktion.models.posts.Post;

/**
 * A PhotoUploadPost implementation for Firebase
 *
 * @author pedro
 */
public class FirebasePhotoUploadPost extends FirebasePost {
    public String photoId = "";
    public String poiName = "";

    /**
     * Constructs a Firebase PhotoUploadPost
     *
     * @param post a PhotoUploadPost
     */
    public FirebasePhotoUploadPost(PhotoUploadPost post) {
        super(post);
        this.photoId = post.getPhotoId();
        this.poiName = post.getPOIName();
    }

    /**
     * Default constructor for calls to DataSnapshot.getValue(FirebasePhotoUploadPost.class)
     */
    public FirebasePhotoUploadPost() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Post toPost() {
        return new PhotoUploadPost(photoId, poiName, new Date(milliseconds), id);
    }
}
