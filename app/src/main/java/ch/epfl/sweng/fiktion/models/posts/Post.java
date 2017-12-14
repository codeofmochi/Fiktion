package ch.epfl.sweng.fiktion.models.posts;

import java.util.Date;

/**
 * A post
 *
 * @author pedro
 */
public abstract class Post {
    protected final PostType type;
    protected final String id;
    protected final Date date;



    public Post(PostType type, String id, Date date) {
        this.type = type;
        this.id = id;
        this.date = date;
    }
}
