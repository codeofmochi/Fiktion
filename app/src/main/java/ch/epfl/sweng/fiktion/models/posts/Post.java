package ch.epfl.sweng.fiktion.models.posts;

import java.util.Date;

/**
 * A post
 *
 * @author pedro
 */
public abstract class Post {
    private final PostType type;
    private final String id;
    private final Date date;

    /**
     * constructs a post
     *
     * @param type the post type
     * @param id   the id of the post
     * @param date the date of the post
     */
    public Post(PostType type, String id, Date date) {
        this.type = type;
        this.id = id;
        this.date = date;
    }

    /**
     * get the post type
     *
     * @return the type of the post
     */
    public PostType getType() {
        return type;
    }

    /**
     * get the id of the post
     *
     * @return the id of the post
     */
    public String getId() {
        return id;
    }

    /**
     * get the date of the post
     *
     * @return the date of the post
     */
    public Date getDate() {
        return new Date(date.getTime());
    }

    /**
     * equality if parameter is a post and has the same field values
     *
     * @param that the compared object
     * @return true if they are equal
     */
    @Override
    public boolean equals(Object that) {
        return that instanceof Post && type.equals(((Post) that).getType()) &&
                id.equals(((Post) that).getId()) && date.equals(((Post) that).getDate());
    }
}
