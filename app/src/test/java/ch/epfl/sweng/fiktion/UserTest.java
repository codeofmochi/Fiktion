package ch.epfl.sweng.fiktion;

import com.firebase.geofire.GeoFire;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.TreeSet;

import ch.epfl.sweng.fiktion.models.User;
import ch.epfl.sweng.fiktion.providers.AuthProvider;
import ch.epfl.sweng.fiktion.providers.DatabaseProvider;
import ch.epfl.sweng.fiktion.providers.FirebaseUser;
import ch.epfl.sweng.fiktion.providers.LocalDatabaseProvider;

import static ch.epfl.sweng.fiktion.UserTest.Result.FAILURE;
import static ch.epfl.sweng.fiktion.UserTest.Result.NOTHING;
import static ch.epfl.sweng.fiktion.UserTest.Result.SUCCESS;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

/**
 * This class tests methods of the class USER
 * Created by Rodrigo on 22.10.2017.
 */

@RunWith(MockitoJUnitRunner.class)
public class UserTest {

    private final User user = new User("default", "defaultID", new TreeSet<String>());
    private DatabaseProvider localDB = new LocalDatabaseProvider();
    private DatabaseProvider.ModifyUserListener dbListener;

    private void setDBList(DatabaseProvider.ModifyUserListener listener) {
        dbListener = listener;
    }

    private AuthProvider.AuthListener authListener = new AuthProvider.AuthListener() {
        @Override
        public void onSuccess() {
            setResult(SUCCESS);
        }

        @Override
        public void onFailure() {
            setResult(FAILURE);
        }
    };

    @Mock
    DatabaseReference dbRef, usersRef, userRef;
    @Mock
    DatabaseProvider dbp;

    @Mock
    GeoFire geofire;

    @Mock
    DataSnapshot snapshot;

    public enum Result {SUCCESS, FAILURE, NOTHING}
    private UserTest.Result result;
    private void setResult(UserTest.Result result) {
        this.result = result;
    }

    @Before
    public void setUp() {
        localDB = new LocalDatabaseProvider();
        result = NOTHING;

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                setDBList((DatabaseProvider.ModifyUserListener) invocation.getArgument(1));
                return null;
            }
        }).when(dbp).modifyUser(any(User.class), any(DatabaseProvider.ModifyUserListener.class));

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
    public void testDatabaseInteractionsFavourites() {

        user.addFavourite(dbp, "new POI", authListener);

        dbListener.onSuccess();
        assertThat(result, is(SUCCESS));

        dbListener.onDoesntExist();
        assertThat(result, is(FAILURE));

        dbListener.onFailure();
        assertThat(result, is(FAILURE));

        TreeSet<String> set = new TreeSet<>();
        set.add("new POI");
        new User("", "", set).removeFavourite(dbp, "new POI", authListener);

        dbListener.onSuccess();
        assertThat(result, is(SUCCESS));

        dbListener.onDoesntExist();
        assertThat(result, is(FAILURE));

        dbListener.onFailure();
        assertThat(result, is(FAILURE));

    }

    @Test
    public void testDatabaseInteractionsChangeName() {

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                setDBList((DatabaseProvider.ModifyUserListener) invocation.getArgument(1));
                return null;
            }
        }).when(dbp).modifyUser(any(User.class), any(DatabaseProvider.ModifyUserListener.class));


        user.changeName(dbp,"new", authListener);
        dbListener.onSuccess();
        assertThat(result, is(SUCCESS));
        dbListener.onDoesntExist();
        assertThat(result, is(FAILURE));
        dbListener.onFailure();
        assertThat(result, is(FAILURE));

    }

    @Test
    public void testAddFavouriteLogic() {
        //change to Local database to test addFavourite logic

        user.addFavourite(localDB, "new POI", new AuthProvider.AuthListener() {
            @Override
            public void onSuccess() {
                assertTrue(user.getFavourites().contains("new POI"));
            }

            @Override
            public void onFailure() {
                Assert.fail();
            }
        });

        user.addFavourite(localDB, "new POI", new AuthProvider.AuthListener() {
            @Override
            public void onSuccess() {
                Assert.fail();
            }

            @Override
            public void onFailure() {
                assertTrue(user.getFavourites().contains("new POI"));
            }
        });
    }

    @Test
    public void testRemoveFavouriteLogic() {

        TreeSet<String> set = new TreeSet<>();
        set.add("new POI");
        new User("default", "defaultID", set).removeFavourite(localDB, "new POI", new AuthProvider.AuthListener() {
            @Override
            public void onSuccess() {
                assertFalse(user.getFavourites().contains("new POI"));
            }

            @Override
            public void onFailure() {
                Assert.fail();
            }
        });

        new User("default", "defaultID", set).removeFavourite(localDB, "new POI", new AuthProvider.AuthListener() {
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
