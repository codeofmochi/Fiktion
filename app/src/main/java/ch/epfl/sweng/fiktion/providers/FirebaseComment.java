package ch.epfl.sweng.fiktion.providers;

import java.util.Date;

import ch.epfl.sweng.fiktion.models.Comment;

import static ch.epfl.sweng.fiktion.providers.FirebaseDatabaseProvider.decode;
import static ch.epfl.sweng.fiktion.providers.FirebaseDatabaseProvider.encode;

/**
 * A comment implementation for Firebase
 *
 * @author pedro
 */
public class FirebaseComment {
    public String text = "";
    public String authorId = "";
    public long milliseconds = 0;
    public int rating = 0;

    /**
     * Default constructor for calls to DataSnapshot.getValue(FirebaseComment.class)
     */
    public FirebaseComment() {
    }

    /**
     * Constructs a Firebase comment
     *
     * @param comment a comment
     */
    public FirebaseComment(Comment comment) {
        text = encode(comment.getText());
        authorId = encode(comment.getAuthorId());
        milliseconds = -comment.getDate().getTime();
        rating = comment.getRating();
    }

    /**
     * Returns the real comment
     *
     * @return the comment
     */
    public Comment toComment() {
        return new Comment(decode(text), decode(authorId), new Date(milliseconds), -rating);
    }
}
