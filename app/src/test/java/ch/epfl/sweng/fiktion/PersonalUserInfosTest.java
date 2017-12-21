package ch.epfl.sweng.fiktion;

import org.joda.time.LocalDate;
import org.junit.Test;

import ch.epfl.sweng.fiktion.models.PersonalUserInfos;

import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class PersonalUserInfosTest {

    @Test
    public void initializeDefault() {
        PersonalUserInfos infos = new PersonalUserInfos();
        assertThat(infos.getAge(), is(0));
        assertThat(infos.getCountry(), is(""));
        assertThat(infos.getFirstName(), is(""));
        assertThat(infos.getLastName(), is(""));
        assertThat(infos.getBirthday(), is(new LocalDate()));
    }

    @Test
    public void initializeReal() {
        PersonalUserInfos infos = new PersonalUserInfos(new LocalDate(1992, 11, 27), "Rodrigo", "Soares Granja", "Switzerland");
        assertThat(infos.getAge(), is(25));
        assertThat(infos.getCountry(), is("Switzerland"));
        assertThat(infos.getFirstName(), is("Rodrigo"));
        assertThat(infos.getLastName(), is("Soares Granja"));
        assertNotNull(infos.getBirthday());
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalFirstNameArgument() {
        new PersonalUserInfos(new LocalDate(), null, "", "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalLastNameArgument() {
        new PersonalUserInfos(new LocalDate(), "", null, "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalCountryArgument() {
        new PersonalUserInfos(new LocalDate(), "", "", null);
    }


}
