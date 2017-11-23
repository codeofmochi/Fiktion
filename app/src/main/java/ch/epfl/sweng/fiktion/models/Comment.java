package ch.epfl.sweng.fiktion.models;

import java.util.Date;

/**
 * Created by Justinas on 23/11/2017.
 */

public class Comment {
    private final String text;
    private final User author;
    private final Date date;

    /**
     * Constructor of comment
     *
     * @param text          the comment itself
     * @param author        the User who wrote the comment
     * @param date          date when the comment was written
     */
    public Comment(String text, User author, Date date) {
        this.text = text;
        this.author = author;
        this.date = date;
    }

    public String getText(){ return text; }
    public User getAuthor(){ return author; }
    public Date getDate(){  return date; }


}
