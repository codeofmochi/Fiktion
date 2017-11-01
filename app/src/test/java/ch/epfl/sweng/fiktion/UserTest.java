package ch.epfl.sweng.fiktion;

import org.junit.Test;

import ch.epfl.sweng.fiktion.models.User;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/** This class tests methods of the class USER
 * Created by Rodrigo on 22.10.2017.
 */

public class UserTest {

    private User user = new User("new user","id");

    @Test
    public void correctlyCreatesUser(){
        assertThat(user.getName(),is("new user"));
        assertThat(user.getID(), is("id"));
    }

}
