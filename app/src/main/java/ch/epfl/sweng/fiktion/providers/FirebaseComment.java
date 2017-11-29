package ch.epfl.sweng.fiktion.providers;

import ch.epfl.sweng.fiktion.models.Comment;

/**
 * A comment implementation for Firebase
 *
 * @author pedro
 */
public class FirebaseComment {
    public String text = "";
    public String authorId = "";
    public FirebaseDate date = new FirebaseDate();

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
        text = comment.getText();
        authorId = comment.getAuthorId();
        date = new FirebaseDate(comment.getDate());
    }

    /**
     * Returns the real comment
     *
     * @return the comment
     */
    public Comment toComment() {
        return new Comment(text, authorId, date.toDate());
    }
}
