package ch.epfl.sweng.fiktion.models;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import ch.epfl.sweng.fiktion.utils.CollectionsUtils;

/**
 * Created by Justinas on 23/11/2017.
 */

public class Comment {
    private final String id;
    private final String text;
    private final String authorId;
    private final Date date;
    private int rating;

    /**
     * Constructor of comment
     *
     * @param text     the comment itself
     * @param authorId the Id of the user who wrote the comment
     * @param date     date when the comment was written
     * @param rating   the rating of the comment
     */
    public Comment(String text, String authorId, Date date, int rating) throws NoSuchAlgorithmException {
        this.text = text;
        this.authorId = authorId;
        this.date = date;
        this.rating = rating;

        // create a hash for the id
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest((text + authorId + date.getTime()).getBytes());
        id = CollectionsUtils.bytesToHexString(hash);
    }

    /**
     * Constructor of comment
     *
     * @param id       the Id of the comment
     * @param text     the comment itself
     * @param authorId the Id of the user who wrote the comment
     * @param date     date when the comment was written
     * @param rating   the rating of the comment
     */
    public Comment(String id, String text, String authorId, Date date, int rating) {
        this.id = id;
        this.text = text;
        this.authorId = authorId;
        this.date = date;
        this.rating = rating;
    }

    public String getId() {
        return id;
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

    public int getRating() {
        return rating;
    }
}
