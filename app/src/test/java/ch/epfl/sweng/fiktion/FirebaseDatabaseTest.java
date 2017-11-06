package ch.epfl.sweng.fiktion;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import ch.epfl.sweng.fiktion.models.PointOfInterest;
import ch.epfl.sweng.fiktion.models.Position;
import ch.epfl.sweng.fiktion.providers.DatabaseProvider;
import ch.epfl.sweng.fiktion.providers.FirebaseDatabaseProvider;
import ch.epfl.sweng.fiktion.providers.FirebasePointOfInterest;

import static ch.epfl.sweng.fiktion.FirebaseDatabaseTest.Result.ALREADYEXISTS;
import static ch.epfl.sweng.fiktion.FirebaseDatabaseTest.Result.DOESNTEXIST;
import static ch.epfl.sweng.fiktion.FirebaseDatabaseTest.Result.FAILURE;
import static ch.epfl.sweng.fiktion.FirebaseDatabaseTest.Result.NOTHING;
import static ch.epfl.sweng.fiktion.FirebaseDatabaseTest.Result.SUCCESS;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

/**
 * Created by pedro on 23/10/17.
 */

@RunWith(MockitoJUnitRunner.class)
public class FirebaseDatabaseTest {

    FirebaseDatabaseProvider database;

    PointOfInterest poiTest = new PointOfInterest("poiName", new Position(10, 12));

    ValueEventListener vel;

    @Mock
    DatabaseReference dbRef, poisRef, poiRef;

    @Mock
    GeoFire geofire;

    @Mock
    DataSnapshot snapshot;

    private Result result;

    public enum Result {SUCCESS, ALREADYEXISTS, DOESNTEXIST, FAILURE, NOTHING}

    private void setResult(Result result) {
        this.result = result;
    }

    private void setVel(ValueEventListener vel) {
        this.vel = vel;
    }

    @Before
    public void initializers() throws Exception {
        database = new FirebaseDatabaseProvider(dbRef, geofire);
        result = NOTHING;
    }

    @Test
    public void addPoiTest() {

        when(dbRef.child("Points of interest")).thenReturn(poisRef);
        when(poisRef.child(anyString())).thenReturn(poiRef);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                setVel((ValueEventListener) invocation.getArguments()[0]);
                return null;
            }
        }).when(poiRef).addListenerForSingleValueEvent(any(ValueEventListener.class));

        DatabaseProvider.AddPoiListener listener = new DatabaseProvider.AddPoiListener() {
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
        when(poiRef.setValue(any(FirebasePointOfInterest.class))).thenReturn(null);
        doNothing().when(geofire).setLocation(anyString(), any(GeoLocation.class));

        when(snapshot.exists()).thenReturn(false, true);
        database.addPoi(poiTest, listener);
        vel.onDataChange(snapshot);
        assertThat(result, is(SUCCESS));

        when(snapshot.exists()).thenReturn(true);
        vel.onDataChange(snapshot);
        assertThat(result, is(ALREADYEXISTS));

        vel.onCancelled(null);
        assertThat(result, is(FAILURE));
    }

    @Test
    public void GetPoiTest() {
        when(dbRef.child("Points of interest")).thenReturn(poisRef);
        when(poisRef.child(anyString())).thenReturn(poiRef);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                setVel((ValueEventListener) invocation.getArguments()[0]);
                return null;
            }
        }).when(poiRef).addListenerForSingleValueEvent(any(ValueEventListener.class));
        DatabaseProvider.GetPoiListener listener = new DatabaseProvider.GetPoiListener() {
            @Override
            public void onSuccess(PointOfInterest poi) {
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

        when(snapshot.exists()).thenReturn(true);
        when(snapshot.getValue(FirebasePointOfInterest.class)).thenReturn(new FirebasePointOfInterest(poiTest));
        database.getPoi(poiTest.name(), listener);
        vel.onDataChange(snapshot);
        assertThat(result, is(SUCCESS));

        when(snapshot.getValue(FirebasePointOfInterest.class)).thenReturn(null);
        vel.onDataChange(snapshot);
        assertThat(result, is(FAILURE));

        when(snapshot.exists()).thenReturn(false);
        vel.onDataChange(snapshot);
        assertThat(result, is(DOESNTEXIST));

        vel.onCancelled(null);
        assertThat(result, is(FAILURE));
    }

    private GeoQueryEventListener geoQueryEventListener;

    private void setGqel(GeoQueryEventListener geoQueryEventListener) {
        this.geoQueryEventListener = geoQueryEventListener;
    }

    private DatabaseProvider.GetPoiListener getPoiListener;

    private void setGPL(DatabaseProvider.GetPoiListener getPoiListener) {
        this.getPoiListener = getPoiListener;
    }

    private int keyCount = 0;

    private void incr() {
        ++keyCount;
    }

    @Test
    public void findNearPoisTest() throws Exception {
        GeoQuery geoQuery = mock(GeoQuery.class);
        when(geofire.queryAtLocation(any(GeoLocation.class), anyDouble())).thenReturn(geoQuery);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                setGqel((GeoQueryEventListener) invocation.getArguments()[0]);
                return null;
            }
        }).when(geoQuery).addGeoQueryEventListener(any(GeoQueryEventListener.class));
        DatabaseProvider.FindNearPoisListener findPoiListener = new DatabaseProvider.FindNearPoisListener() {
            @Override
            public void onNewValue(PointOfInterest poi) {
                incr();
            }

            @Override
            public void onFailure() {
                setResult(FAILURE);
            }
        };

        FirebaseDatabaseProvider databaseSpy = spy(database);

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                setGPL((DatabaseProvider.GetPoiListener) invocation.getArguments()[1]);
                return null;
            }
        }).when(databaseSpy).getPoi(anyString(), any(DatabaseProvider.GetPoiListener.class));

        databaseSpy.findNearPois(poiTest.position(), 10, findPoiListener);
        geoQueryEventListener.onKeyEntered("key", null);
        getPoiListener.onSuccess(poiTest);
        assertThat(keyCount, is(1));
        getPoiListener.onSuccess(poiTest);
        getPoiListener.onSuccess(poiTest);
        getPoiListener.onSuccess(poiTest);
        getPoiListener.onSuccess(poiTest);
        assertThat(keyCount, is(5));
        getPoiListener.onDoesntExist();
        getPoiListener.onFailure();
        geoQueryEventListener.onKeyExited("key");
        geoQueryEventListener.onKeyMoved("key", null);
        geoQueryEventListener.onGeoQueryReady();
        geoQueryEventListener.onGeoQueryError(null);
        assertThat(result, is(FAILURE));
    }
}
