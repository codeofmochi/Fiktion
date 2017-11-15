package ch.epfl.sweng.fiktion;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import java.util.TreeSet;

import ch.epfl.sweng.fiktion.models.User;
import ch.epfl.sweng.fiktion.providers.AuthProvider;
import ch.epfl.sweng.fiktion.providers.DatabaseProvider;
import ch.epfl.sweng.fiktion.providers.FirebaseUser;
import ch.epfl.sweng.fiktion.providers.LocalDatabaseProvider;

import static junit.framework.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

/**
 * This class tests methods of the class USER
 * Created by Rodrigo on 22.10.2017.
 */

public class UserTest {

    private final User user = new User("default", "defaultID", new TreeSet<String>());
    private DatabaseProvider database;

    @Before
    public void setDatabase() {
        database = new LocalDatabaseProvider();
    }

    @Test
    public void correctlyCreatesUser() {
        assertThat(user.getName(), is("default"));
        assertThat(user.getID(), is("defaultID"));
        assertThat(user.getFavourites().size(), is(0));
    }

    @Test
    public void testEquals() {
        User other = new User("other user", "defaultID", new TreeSet<String>());
        User almostEqual = new User("default", "id1", new TreeSet<String>());
        User same = new User("default", "defaultID", new TreeSet<String>());

        assertFalse(user.equals(null));
        assertFalse(user.equals(almostEqual));
        assertFalse(user.equals(other));
        assertTrue(user.equals(same));
        assertFalse(user.equals(new FirebaseUser()));
    }

    @Test
    public void testFavourites() {
        user.addFavourite(database, "new POI", new AuthProvider.AuthListener() {
            @Override
            public void onSuccess() {
                assertTrue(user.getFavourites().contains("new POI"));
            }

            @Override
            public void onFailure() {
                Assert.fail();
            }
        });

        user.addFavourite(database, "new POI", new AuthProvider.AuthListener() {
            @Override
            public void onSuccess() {
                Assert.fail();
            }

            @Override
            public void onFailure() {
                assertTrue(user.getFavourites().contains("new POI"));
            }
        });

        user.removeFavourite(database, "new POI", new AuthProvider.AuthListener() {
            @Override
            public void onSuccess() {
                assertFalse(user.getFavourites().contains("new POI"));
            }

            @Override
            public void onFailure() {
                Assert.fail();
            }
        });

        user.removeFavourite(database, "new POI", new AuthProvider.AuthListener() {
            @Override
            public void onSuccess() {
                Assert.fail();
            }

            @Override
            public void onFailure() {
                assertFalse(user.getFavourites().contains("new POI"));
            }
        });
    }


}
