package ch.epfl.sweng.fiktion;

import com.firebase.geofire.GeoFire;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

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
import ch.epfl.sweng.fiktion.providers.FirebaseDatabaseProvider;
import ch.epfl.sweng.fiktion.providers.FirebaseUser;
import ch.epfl.sweng.fiktion.providers.LocalDatabaseProvider;

import static ch.epfl.sweng.fiktion.UserTest.Result.DOESNTEXIST;
import static ch.epfl.sweng.fiktion.UserTest.Result.FAILURE;
import static ch.epfl.sweng.fiktion.UserTest.Result.NOTHING;
import static ch.epfl.sweng.fiktion.UserTest.Result.SUCCESS;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

/**
 * This class tests methods of the class USER
 * Created by Rodrigo on 22.10.2017.
 */

@RunWith(MockitoJUnitRunner.class)
public class UserTest {

    private final User user = new User("default", "defaultID", new TreeSet<String>());
    FirebaseDatabaseProvider database;
    DatabaseProvider localDB = new LocalDatabaseProvider();
    private DatabaseProvider.ModifyUserListener dbListener;

    private void setDBList(DatabaseProvider.ModifyUserListener listener) {
        dbListener = listener;
    }

    ValueEventListener vel;

    private void setVel(ValueEventListener vel) {
        this.vel = vel;
    }

    @Mock
    DatabaseReference dbRef, usersRef, userRef;

    @Mock
    GeoFire geofire;

    @Mock
    DataSnapshot snapshot;

    public enum Result {SUCCESS, ALREADYEXISTS, DOESNTEXIST, FAILURE, NOTHING}

    private UserTest.Result result;


    private void setResult(UserTest.Result result) {
        this.result = result;
    }

    @Before
    public void setDatabase() {
        database = new FirebaseDatabaseProvider(dbRef, geofire);
        localDB = new LocalDatabaseProvider();
        result = NOTHING;
    }


    public void prepareDatabaseModifyMock() {
        when(dbRef.child("Users")).thenReturn(usersRef);
        when(usersRef.child(anyString())).thenReturn(userRef);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                setVel((ValueEventListener) invocation.getArguments()[0]);
                return null;
            }
        }).when(userRef).addListenerForSingleValueEvent(any(ValueEventListener.class));

        DatabaseProvider.ModifyUserListener listener = new DatabaseProvider.ModifyUserListener() {
            @Override
            public void onSuccess() {
                setResult(SUCCESS);
            }

            @Override
            public void onDoesntExist() {
                setResult(DOESNTEXIST);
            }

            @Override
            public void onFailure() {
                setResult(FAILURE);
            }
        };

        when(userRef.setValue(any(FirebaseUser.class))).thenReturn(null);

        AuthProvider.AuthListener authListener = new AuthProvider.AuthListener() {
            @Override
            public void onSuccess() {
                setResult(SUCCESS);
            }

            @Override
            public void onFailure() {
                setResult(FAILURE);
            }
        };

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

        prepareDatabaseModifyMock();

        AuthProvider.AuthListener authListener = new AuthProvider.AuthListener() {
            @Override
            public void onSuccess() {
                setResult(SUCCESS);
            }

            @Override
            public void onFailure() {
                setResult(FAILURE);
            }
        };

        user.addFavourite(database, "new POI", authListener);

        when(snapshot.exists()).thenReturn(true);
        vel.onDataChange(snapshot);
        assertThat(result, is(SUCCESS));

        when(snapshot.exists()).thenReturn(false);
        vel.onDataChange(snapshot);
        assertThat(result, is(FAILURE));

        vel.onCancelled(null);
        assertThat(result, is(FAILURE));

        TreeSet<String> set = new TreeSet<>();
        set.add("new POI");
        new User("", "", set).removeFavourite(database, "new POI", authListener);

        when(snapshot.exists()).thenReturn(true);
        vel.onDataChange(snapshot);
        assertThat(result, is(SUCCESS));

        when(snapshot.exists()).thenReturn(false);
        vel.onDataChange(snapshot);
        assertThat(result, is(FAILURE));

        vel.onCancelled(null);
        assertThat(result, is(FAILURE));

    }

    @Test
    public void testDatabaseInteractionsChangeName() {

        prepareDatabaseModifyMock();

        AuthProvider.AuthListener authListener = new AuthProvider.AuthListener() {
            @Override
            public void onSuccess() {
                setResult(SUCCESS);
            }

            @Override
            public void onFailure() {
                setResult(FAILURE);
            }
        };

        user.changeName(database,"new", authListener);

        when(snapshot.exists()).thenReturn(true);
        vel.onDataChange(snapshot);
        assertThat(result, is(SUCCESS));

        when(snapshot.exists()).thenReturn(false);
        vel.onDataChange(snapshot);
        assertThat(result, is(FAILURE));

        vel.onCancelled(null);
        assertThat(result, is(FAILURE));

        TreeSet<String> set = new TreeSet<>();
        set.add("new POI");
        new User("", "", set).removeFavourite(database, "new POI", authListener);

        when(snapshot.exists()).thenReturn(true);
        vel.onDataChange(snapshot);
        assertThat(result, is(SUCCESS));

        when(snapshot.exists()).thenReturn(false);
        vel.onDataChange(snapshot);
        assertThat(result, is(FAILURE));

        vel.onCancelled(null);
        assertThat(result, is(FAILURE));

    }

    @Test
    public void testAddFavouriteLogic(){
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
    public void testRemoveFavouriteLogic(){

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
