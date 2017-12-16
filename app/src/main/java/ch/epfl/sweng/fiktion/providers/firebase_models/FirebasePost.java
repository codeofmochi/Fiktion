package ch.epfl.sweng.fiktion.providers.firebase_models;

import java.util.Date;

import ch.epfl.sweng.fiktion.models.posts.Post;
import ch.epfl.sweng.fiktion.models.posts.PostType;

import static ch.epfl.sweng.fiktion.providers.FirebaseDatabaseProvider.encode;

/**
 * A post implementation for Firebase
 *
 * @author pedro
 */
public abstract class FirebasePost {
    protected PostType type;
    protected String id = "";
    protected long milliseconds = 0;

    /**
     * Constructs a Firebase post
     *
     * @param post a post
     */
    public FirebasePost(Post post) {
        this.type = post.getType();
        this.id = encode(post.getId());
        this.milliseconds = post.getDate().getTime();
    }

    /**
     * Default constructor for calls to DataSnapshot.getValue(FirebasePost.class)
     */
    public FirebasePost() {
    }

    /**
     * Returns the real version Post
     *
     * @return the post
     */
    public abstract Post toPost();
}
