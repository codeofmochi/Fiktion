package ch.epfl.sweng.fiktion;

import junit.framework.Assert;

import org.junit.Test;

import ch.epfl.sweng.fiktion.models.User;
import ch.epfl.sweng.fiktion.providers.AuthProvider;
import ch.epfl.sweng.fiktion.providers.FirebaseUser;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
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

    @Test
    public void testEquals(){
        User other = new User("other user","id");
        User almostEqual = new User("new user", "id1");
        User same = new User("new user","id");

        assertFalse(user.equals(null));
        assertFalse(user.equals(almostEqual));
        assertFalse(user.equals(other));
        assertTrue(user.equals(same));
        assertFalse(user.equals(new FirebaseUser()));
    }



}
