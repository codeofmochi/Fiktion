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
        year = infos.getYear();
        day = infos.getDay();
        month = infos.getMonth();
        country = infos.getCountry();
        firstName = infos.getFirstName();
        lastName = infos.getLastName();
    }

    public PersonalUserInfos toPersonalUserInfos(){
        return new PersonalUserInfos(year,month, day, firstName, lastName, country);
    }
}
