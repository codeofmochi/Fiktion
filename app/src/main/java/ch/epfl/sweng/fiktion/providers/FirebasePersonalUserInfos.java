package ch.epfl.sweng.fiktion.providers;

import android.util.Log;

import org.joda.time.LocalDate;

import ch.epfl.sweng.fiktion.models.PersonalUserInfos;


public class FirebasePersonalUserInfos {
    public int year = 0;
    public int day = 0;
    public int month = 0;
    public String country = "";
    public String firstName = "";
    public String lastName = "";

    public FirebasePersonalUserInfos(){
    }

    public FirebasePersonalUserInfos(PersonalUserInfos infos){
        LocalDate date = infos.getBirthday();
        year = date.getYear();
        day = date.getDayOfMonth();
        month = date.getMonthOfYear();
        country = infos.getCountry();
        firstName = infos.getFirstName();
        lastName = infos.getLastName();
    }

    public PersonalUserInfos toPersonalUserInfos(){
        return new PersonalUserInfos(new LocalDate(year,month,day), firstName, lastName, country);
    }
}
