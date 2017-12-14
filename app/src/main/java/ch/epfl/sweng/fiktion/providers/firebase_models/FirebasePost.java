package ch.epfl.sweng.fiktion.providers.firebase_models;

import java.util.Date;

import ch.epfl.sweng.fiktion.models.posts.Post;
import ch.epfl.sweng.fiktion.models.posts.PostType;

/**
 * Created by painguin on 14.12.17.
 */

public abstract class FirebasePost {
    protected PostType type;
    protected String id = "";
    protected long milliseconds = 0;

    public FirebasePost(Post post) {
        this.type = post.getType();
        this.id = post.getId();
        this.milliseconds = post.getDate().getTime();
    }

    public FirebasePost() {
    }

    public abstract Post toPost();
}
