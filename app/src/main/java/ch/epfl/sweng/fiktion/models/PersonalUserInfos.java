package ch.epfl.sweng.fiktion.models;

import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.PeriodType;

public class PersonalUserInfos {

    private final int year;
    private final int month;
    private final int day;
    private final String firstName;
    private final String lastName;
    private final String country;

    public PersonalUserInfos() {
        year = 1;
        month = 1;
        day = 1;

        firstName = "";
        lastName = "";
        country = "";
    }

    public PersonalUserInfos(int y, int m, int d, String firstName, String lastName, String country) {
        if (firstName == null || lastName == null || country == null) {
            throw new IllegalArgumentException("Cannot create personal infos with null strings");
        }
        if (y <= 0 || y > 9999) {
            throw new IllegalArgumentException("Years must be a value between 0 and 9999 inclusive");
        }

        if (m < 1 || m > 12) {
            throw new IllegalArgumentException("Months are from 0 to 11");
        }

        if (d <= 0 || d > 31) {
            throw new IllegalArgumentException("There are at most 31 days in a month");
        }

        this.year = y;
        this.month = m;
        this.day = d;
        this.firstName = firstName;
        this.lastName = lastName;
        this.country = country;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public int getAge() {
        LocalDate now = new LocalDate();
        LocalDate birthday = new LocalDate(year, month-1, day);
        Period period = new Period(birthday, now, PeriodType.years());
        return period.getYears();
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getCountry() {
        return country;
    }
}
