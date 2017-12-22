package ch.epfl.sweng.fiktion;

import org.junit.Test;

import ch.epfl.sweng.fiktion.models.PersonalUserInfos;

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
        assertThat(infos.getMonth(), is(1));
        assertThat(infos.getDay(), is(1));
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

    @Test(expected = IllegalArgumentException.class)
    public void yearUnderMin() {
        new PersonalUserInfos(0, 1, 1, "", "", "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void yearOverMax() {
        new PersonalUserInfos(10000, 1, 1, "", "", "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void monthUnderMin() {
        new PersonalUserInfos(1, 0, 1, "", "", "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void monthOverMax() {
        new PersonalUserInfos(1, 13, 1, "", "", "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void dayUnderMin() {
        new PersonalUserInfos(1, 1, 0, "", "", "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void dayOverMax() {
        new PersonalUserInfos(1, 1, 32, "", "", "");
    }
}
