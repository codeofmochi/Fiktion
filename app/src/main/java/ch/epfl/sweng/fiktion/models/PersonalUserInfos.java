package ch.epfl.sweng.fiktion.models;

import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.PeriodType;

public class PersonalUserInfos {

    private final LocalDate birthday;
    private final String firstName;
    private final String lastName;
    private final String country;

    public PersonalUserInfos() {
        birthday = null;
        firstName = "";
        lastName = "";
        country = "";
    }

    public PersonalUserInfos(LocalDate birthday, String firstName, String lastName, String country) {
        if (firstName == null || lastName == null || country == null) {
            throw new IllegalArgumentException("Cannot create personal infos with null strings");
        }
        this.birthday = birthday; // we may set this null
        this.firstName = firstName;
        this.lastName = lastName;
        this.country = country;
    }

    public LocalDate getBirthday() {
        if (birthday == null) {
            return new LocalDate();
        } else {
            return new LocalDate(birthday);
        }
    }

    public int getAge() {
        if (birthday == null) {
            return 0;
        } else {
            LocalDate now = new LocalDate();
            Period period = new Period(birthday, now, PeriodType.years());
            return period.getYears();

        }
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
