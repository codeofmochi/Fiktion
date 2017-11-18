package ch.epfl.sweng.fiktion;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
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

import java.util.Collections;
import java.util.TreeSet;

import ch.epfl.sweng.fiktion.models.PointOfInterest;
import ch.epfl.sweng.fiktion.models.Position;
import ch.epfl.sweng.fiktion.providers.DatabaseProvider;
import ch.epfl.sweng.fiktion.providers.FirebaseDatabaseProvider;
import ch.epfl.sweng.fiktion.providers.FirebasePointOfInterest;
import ch.epfl.sweng.fiktion.providers.SearchProvider;

import static ch.epfl.sweng.fiktion.FirebaseDatabasePOITest.Result.ALREADYEXISTS;
import static ch.epfl.sweng.fiktion.FirebaseDatabasePOITest.Result.DOESNTEXIST;
import static ch.epfl.sweng.fiktion.FirebaseDatabasePOITest.Result.FAILURE;
import static ch.epfl.sweng.fiktion.FirebaseDatabasePOITest.Result.NOTHING;
import static ch.epfl.sweng.fiktion.FirebaseDatabasePOITest.Result.SUCCESS;
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
public class FirebaseDatabasePOITest {

    FirebaseDatabaseProvider database;

    PointOfInterest poiTest = new PointOfInterest("poiName", new Position(10, 12), new TreeSet<String>(), "", 0, "", "");

    ValueEventListener vel;

    DatabaseProvider.AddPoiListener apl;

    @Mock
    DatabaseReference dbRef, poisRef, poiRef;

    @Mock
    GeoFire geofire;

    @Mock
    SearchProvider searchProvider;

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

    private void setApl(DatabaseProvider.AddPoiListener apl) {
        this.apl = apl;
    }

    @Before
    public void initializers() {
        database = new FirebaseDatabaseProvider(dbRef, geofire, searchProvider);
        result = NOTHING;
        nbPOIs = 0;
    }

    @Test
    public void addPoiTest() {
        when(dbRef.child("Points of interest")).thenReturn(poisRef);
        when(poisRef.child(anyString())).thenReturn(poiRef);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                setVel((ValueEventListener) invocation.getArgument(0));
                return null;
            }
        }).when(poiRef).addListenerForSingleValueEvent(any(ValueEventListener.class));

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                setApl((DatabaseProvider.AddPoiListener) invocation.getArgument(1));
                return null;
            }
        }).when(searchProvider).addPoi(any(PointOfInterest.class), any(DatabaseProvider.AddPoiListener.class));

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
        when(poiRef.removeValue()).thenReturn(null);
        doNothing().when(geofire).setLocation(anyString(), any(GeoLocation.class));
        doNothing().when(geofire).removeLocation(anyString());

        when(snapshot.exists()).thenReturn(false);
        database.addPoi(poiTest, listener);
        vel.onDataChange(snapshot);
        apl.onSuccess();
        assertThat(result, is(SUCCESS));

        apl.onFailure();
        assertThat(result, is(FAILURE));

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

        database.getPoi(poiTest.name(), listener);

        when(snapshot.exists()).thenReturn(true);
        when(snapshot.getValue(FirebasePointOfInterest.class)).thenReturn(new FirebasePointOfInterest(poiTest));
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

    private int nbPOIs = 0;

    private void incr() {
        ++nbPOIs;
    }

    @Test
    public void findNearPoisTest() {
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
        assertThat(nbPOIs, is(1));
        getPoiListener.onSuccess(poiTest);
        getPoiListener.onSuccess(poiTest);
        getPoiListener.onSuccess(poiTest);
        getPoiListener.onSuccess(poiTest);
        assertThat(nbPOIs, is(5));
        getPoiListener.onDoesntExist();
        getPoiListener.onFailure();
        geoQueryEventListener.onKeyExited("key");
        geoQueryEventListener.onKeyMoved("key", null);
        geoQueryEventListener.onGeoQueryReady();
        geoQueryEventListener.onGeoQueryError(null);
        assertThat(result, is(FAILURE));
    }

    private SearchProvider.SearchPOIsByTextListener spbtl;

    public void setSpbtl(SearchProvider.SearchPOIsByTextListener spbtl) {
        this.spbtl = spbtl;
    }

    @Test
    public void searchByTextTest() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                setSpbtl((SearchProvider.SearchPOIsByTextListener) invocation.getArgument(1));
                return null;
            }
        }).when(searchProvider).searchByText(anyString(), any(SearchProvider.SearchPOIsByTextListener.class));

        DatabaseProvider.SearchPOIByTextListener listener = new DatabaseProvider.SearchPOIByTextListener() {
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

        databaseSpy.searchByText("", listener);
        spbtl.onSuccess(Collections.singletonList("poi"));

        assertThat(nbPOIs, is(0));
        getPoiListener.onSuccess(null);
        assertThat(nbPOIs, is(1));
        getPoiListener.onSuccess(null);
        getPoiListener.onSuccess(null);
        getPoiListener.onSuccess(null);
        getPoiListener.onSuccess(null);
        assertThat(nbPOIs, is(5));
        getPoiListener.onDoesntExist();
        getPoiListener.onFailure();
        assertThat(nbPOIs, is(5));

        spbtl.onFailure();
        assertThat(result, is(FAILURE));

    }
}
