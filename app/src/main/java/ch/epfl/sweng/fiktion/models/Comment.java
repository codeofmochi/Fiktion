package ch.epfl.sweng.fiktion.models;

import java.util.Date;

/**
 * Created by Justinas on 23/11/2017.
 */

public class Comment {
    private final String text;
    private final String authorId;
    private final Date date;

    /**
     * Constructor of comment
     *
     * @param text       the comment itself
     * @param authorId   the Id of the user who wrote the comment
     * @param date       date when the comment was written
     */
    public Comment(String text, String authorId, Date date) {
        this.text = text;
        this.authorId = authorId;
        this.date = date;
    }

    public String getText() {
        return text;
    }

    public String getAuthorId() {
        return authorId;
    }

    public Date getDate() {
        return date;
    }


}
