package ch.epfl.sweng.fiktion.providers;

import java.util.Calendar;
import java.util.Date;

/**
 * A Date implementation for Firebase
 *
 * @author pedro
 */
public class FirebaseDate {
    public int year = 2017;
    public int month = 0;
    public int day = 1;
    public int hour = 0;
    public int minute = 0;
    public int second = 0;

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
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);
        second = calendar.get(Calendar.SECOND);
    }

    /**
     * Returns the real date
     *
     * @return the date
     */
    public Date toDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);
        return calendar.getTime();
    }
}
