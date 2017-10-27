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
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import ch.epfl.sweng.fiktion.models.PointOfInterest;
import ch.epfl.sweng.fiktion.models.Position;
import ch.epfl.sweng.fiktion.providers.DatabaseProvider;
import ch.epfl.sweng.fiktion.providers.FirebaseDatabaseProvider;
import ch.epfl.sweng.fiktion.providers.FirebasePointOfInterest;

import static ch.epfl.sweng.fiktion.FirebaseTest.Result.ALREADYEXISTS;
import static ch.epfl.sweng.fiktion.FirebaseTest.Result.DOESNTEXIST;
import static ch.epfl.sweng.fiktion.FirebaseTest.Result.FAILURE;
import static ch.epfl.sweng.fiktion.FirebaseTest.Result.SUCCESS;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;
import static org.powermock.api.support.membermodification.MemberMatcher.method;
import static org.powermock.api.support.membermodification.MemberModifier.suppress;

/**
 * Created by pedro on 23/10/17.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({FirebaseDatabase.class, GeoFire.class})
public class FirebaseTest {

    FirebaseDatabaseProvider database;

    PointOfInterest poiTest = new PointOfInterest("poiName", new Position(10, 12));

    ValueEventListener vel;

    @Mock
    FirebaseDatabase fb;

    @Mock
    DatabaseReference dbRef;

    @Mock
    DatabaseReference poisRef;

    @Mock
    DatabaseReference poiRef;

    @Mock
    GeoFire geofire;

    @Mock
    DataSnapshot snapshot;

    public Result result = FAILURE;

    public enum Result {SUCCESS, ALREADYEXISTS, DOESNTEXIST, FAILURE}

    private void setResult(Result result) {
        this.result = result;
    }

    private void setVel(ValueEventListener vel) {
        this.vel = vel;
    }

    @Before
    public void initializers() throws Exception {
        mockStatic(FirebaseDatabase.class);
        when(FirebaseDatabase.getInstance()).thenReturn(fb);
        when(fb.getReference()).thenReturn(dbRef);
        whenNew(GeoFire.class).withAnyArguments().thenReturn(geofire);
        database = new FirebaseDatabaseProvider();
    }

    @Test
    public void addPoiTest() throws Exception {

        when(dbRef.child("Points of interest")).thenReturn(poisRef);
        when(poisRef.child(Mockito.anyString())).thenReturn(poiRef);
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                setVel((ValueEventListener) invocation.getArguments()[0]);
                return null;
            }
        }).when(poiRef).addListenerForSingleValueEvent(Mockito.any(ValueEventListener.class));

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
        when(poiRef.setValue(Mockito.any(FirebasePointOfInterest.class))).thenReturn(null);
        whenNew(GeoLocation.class).withAnyArguments().thenReturn(null);
        suppress(method(GeoFire.class, "setLocation", String.class, GeoLocation.class));

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
        when(poisRef.child(Mockito.anyString())).thenReturn(poiRef);
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                setVel((ValueEventListener) invocation.getArguments()[0]);
                return null;
            }
        }).when(poiRef).addListenerForSingleValueEvent(Mockito.any(ValueEventListener.class));
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

    GeoQueryEventListener gqel;

    private void setGqel(GeoQueryEventListener gqel) {
        this.gqel = gqel;
    }

    @Test
    public void findNearPoisTest() throws Exception {
        /*
        GeoQuery geoquery = Mockito.mock(GeoQuery.class);
        when(geofire.queryAtLocation(Mockito.any(GeoLocation.class), Mockito.anyDouble())).thenReturn(geoquery);
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                setGqel((GeoQueryEventListener) invocation.getArguments()[0]);
                return null;
            }
        }).when(geoquery).addGeoQueryEventListener(Mockito.any(GeoQueryEventListener.class));
        DatabaseProvider.FindNearPoisListener listener = new DatabaseProvider.FindNearPoisListener() {
            @Override
            public void onNewValue(PointOfInterest poi) {

            }

            @Override
            public void onFailure() {

            }
        };

        when(dbRef.child("Points of interest")).thenReturn(poisRef);
        when(poisRef.child(Mockito.anyString())).thenReturn(poiRef);
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                setVel((ValueEventListener) invocation.getArguments()[0]);
                return null;
            }
        }).when(poiRef).addListenerForSingleValueEvent(Mockito.any(ValueEventListener.class));

        DatabaseProvider.AddPoiListener getPoiListener = new DatabaseProvider.AddPoiListener() {
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
        when(poiRef.setValue(Mockito.any(FirebasePointOfInterest.class))).thenReturn(null);
        whenNew(GeoLocation.class).withAnyArguments().thenReturn(null);
        suppress(method(GeoFire.class, "setLocation", String.class, GeoLocation.class));

        database.findNearPois(poiTest.position(), 10, listener);
        */
    }
}
