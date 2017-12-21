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
        assertThat(infos.getYear(), is(1));
    }

    @Test
    public void initializeReal() {
        PersonalUserInfos infos = new PersonalUserInfos(1992, 11, 27, "John", "Doe", "Switzerland");
        assertThat(infos.getAge(), is(25));
        assertThat(infos.getCountry(), is("Switzerland"));
        assertThat(infos.getFirstName(), is("John"));
        assertThat(infos.getLastName(), is("Doe"));
        assertThat(infos.getYear(), is(1992));
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalFirstNameArgument() {
        new PersonalUserInfos(1,1,1, null, "", "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalLastNameArgument() {
        new PersonalUserInfos(1,1,1, "", null, "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalCountryArgument() {
        new PersonalUserInfos(1,1,1, "", "", null);
    }


}
