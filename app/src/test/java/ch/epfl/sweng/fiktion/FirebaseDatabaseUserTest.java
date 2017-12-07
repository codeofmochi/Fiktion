package ch.epfl.sweng.fiktion;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.LinkedList;
import java.util.TreeSet;

import ch.epfl.sweng.fiktion.models.User;
import ch.epfl.sweng.fiktion.providers.DatabaseProvider;
import ch.epfl.sweng.fiktion.providers.FirebaseDatabaseProvider;
import ch.epfl.sweng.fiktion.providers.FirebaseUser;

import static ch.epfl.sweng.fiktion.FirebaseDatabaseUserTest.Result.ALREADYEXISTS;
import static ch.epfl.sweng.fiktion.FirebaseDatabaseUserTest.Result.DOESNTEXIST;
import static ch.epfl.sweng.fiktion.FirebaseDatabaseUserTest.Result.FAILURE;
import static ch.epfl.sweng.fiktion.FirebaseDatabaseUserTest.Result.NOTHING;
import static ch.epfl.sweng.fiktion.FirebaseDatabaseUserTest.Result.SUCCESS;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

/**
 * This class tests the firebase database proxy concerning User interactions
 *
 * @author Pedro
 */

@RunWith(MockitoJUnitRunner.class)
public class FirebaseDatabaseUserTest {

    private FirebaseDatabaseProvider database;
    private User user = new User("testName", "id", new TreeSet<String>(), new TreeSet<String>(), new LinkedList<String>());

    @Mock
    DatabaseReference dbRef, usersRef, userRef;

    @Mock
    DataSnapshot snapshot;

    private FirebaseDatabaseUserTest.Result result;

    public enum Result {SUCCESS, ALREADYEXISTS, DOESNTEXIST, FAILURE, NOTHING}

    private void setResult(FirebaseDatabaseUserTest.Result result) {
        this.result = result;
    }

    private ValueEventListener vel;

    private void setVel(ValueEventListener vel) {
        this.vel = vel;
    }

    @Before
    public void setup() {
        database = new FirebaseDatabaseProvider(dbRef, null, null);
        result = NOTHING;
    }

    @Test
    public void addUserTest() {
        when(dbRef.child("Users")).thenReturn(usersRef);
        when(usersRef.child(anyString())).thenReturn(userRef);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                setVel((ValueEventListener) invocation.getArguments()[0]);
                return null;
            }
        }).when(userRef).addListenerForSingleValueEvent(any(ValueEventListener.class));

        DatabaseProvider.AddUserListener listener = new DatabaseProvider.AddUserListener() {
            @Override
            public void onSuccess() {
                setResult(SUCCESS);
            }

            @Override
            public void onAlreadyExists() {
                setResult(ALREADYEXISTS);
            }

            @Override
            public void onFailure() {
                setResult(FAILURE);
            }
        };

        when(userRef.setValue(any(FirebaseUser.class))).thenReturn(null);

        database.addUser(user, listener);

        when(snapshot.exists()).thenReturn(true);
        vel.onDataChange(snapshot);
        assertThat(result, is(ALREADYEXISTS));

        when(snapshot.exists()).thenReturn(false);
        vel.onDataChange(snapshot);
        assertThat(result, is(SUCCESS));

        vel.onCancelled(null);
        assertThat(result, is(FAILURE));
    }

    @Test
    public void getUserTest() {
        when(dbRef.child("Users")).thenReturn(usersRef);
        when(usersRef.child(anyString())).thenReturn(userRef);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                setVel((ValueEventListener) invocation.getArguments()[0]);
                return null;
            }
        }).when(userRef).addListenerForSingleValueEvent(any(ValueEventListener.class));


        DatabaseProvider.GetUserListener listener = new DatabaseProvider.GetUserListener() {
            @Override
            public void onSuccess(User user) {
                setResult(SUCCESS);
            }

            @Override
            public void onModified(User user) {

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

        database.getUserById(user.getID(), listener);

        when(snapshot.exists()).thenReturn(true);
        when(snapshot.getValue(FirebaseUser.class)).thenReturn(new FirebaseUser(user));
        vel.onDataChange(snapshot);
        assertThat(result, is(SUCCESS));

        when(snapshot.getValue(FirebaseUser.class)).thenReturn(null);
        vel.onDataChange(snapshot);
        assertThat(result, is(FAILURE));

        when(snapshot.exists()).thenReturn(false);
        vel.onDataChange(snapshot);
        assertThat(result, is(DOESNTEXIST));

        vel.onCancelled(null);
        assertThat(result, is(FAILURE));
    }

    @Test
    public void deleteUserTest() {
        when(dbRef.child("Users")).thenReturn(usersRef);
        when(usersRef.child(anyString())).thenReturn(userRef);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                setVel((ValueEventListener) invocation.getArguments()[0]);
                return null;
            }
        }).when(userRef).addListenerForSingleValueEvent(any(ValueEventListener.class));

        DatabaseProvider.DeleteUserListener listener = new DatabaseProvider.DeleteUserListener() {
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

        database.deleterUserById(user.getID(), listener);
        when(userRef.removeValue()).thenReturn(null);

        when(snapshot.exists()).thenReturn(true);
        vel.onDataChange(snapshot);
        assertThat(result, is(SUCCESS));

        when(snapshot.exists()).thenReturn(false);
        vel.onDataChange(snapshot);
        assertThat(result, is(DOESNTEXIST));

        vel.onCancelled(null);
        assertThat(result, is(FAILURE));
    }

    @Test
    public void modifiyUserTest() {
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

        database.modifyUser(user, listener);

        when(snapshot.exists()).thenReturn(true);
        vel.onDataChange(snapshot);
        assertThat(result, is(SUCCESS));

        when(snapshot.exists()).thenReturn(false);
        vel.onDataChange(snapshot);
        assertThat(result, is(DOESNTEXIST));

        vel.onCancelled(null);
        assertThat(result, is(FAILURE));
    }
}
