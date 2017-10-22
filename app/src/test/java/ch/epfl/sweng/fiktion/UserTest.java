package ch.epfl.sweng.fiktion;

import org.junit.BeforeClass;
import org.junit.Test;

import ch.epfl.sweng.fiktion.models.User;
import ch.epfl.sweng.fiktion.providers.AuthProvider;
import ch.epfl.sweng.fiktion.providers.Providers;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by Rodrigo on 22.10.2017.
 */

@SuppressWarnings("DefaultFileTemplate")
public class UserTest {

    private User user = new User("new user","new@test.ch","id",false);

    @Test
    public void correctlyCreatesUser(){
        assertThat(user.getEmail(), is("new@test.ch"));
        assertThat(user.getName(),is("new user"));
        assertThat(user.getID(), is("id"));
        assertThat(user.isEmailVerified(), is(false));
    }

}
