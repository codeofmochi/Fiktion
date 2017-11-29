package ch.epfl.sweng.fiktion.providers;

import java.util.Calendar;
import java.util.Date;

/**
 * A Date implementation for Firebase
 *
 * @author pedro
 */
public class FirebaseDate {
    public long milliseconds;

    /**
     * Default constructor for calls to DataSnapshot.getValue(FirebaseDate.class)
     */
    public FirebaseDate() {
    }

    /**
     * Constructs a Firebase date
     *
     * @param date a date
     */
    public FirebaseDate(Date date) {
        milliseconds = date.getTime();
    }

    /**
     * Returns the real date
     *
     * @return the date
     */
    public Date toDate() {
        return new Date(milliseconds);
    }
}
