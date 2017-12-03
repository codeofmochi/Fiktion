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
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.TreeSet;

import ch.epfl.sweng.fiktion.models.PointOfInterest;
import ch.epfl.sweng.fiktion.models.Position;
import ch.epfl.sweng.fiktion.providers.DatabaseProvider;
import ch.epfl.sweng.fiktion.providers.FirebaseDatabaseProvider;
import ch.epfl.sweng.fiktion.providers.FirebasePointOfInterest;
import ch.epfl.sweng.fiktion.providers.SearchProvider;
import ch.epfl.sweng.fiktion.utils.Mutable;

import static junit.framework.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FirebaseDatabasePOITest {

    private FirebaseDatabaseProvider database;

    private PointOfInterest poiTest = new PointOfInterest("poiName", new Position(10, 12), new TreeSet<String>(), "", 0, "", "");

    @Mock
    private DatabaseReference dbRef;

    @Mock
    private GeoFire geofire;

    @Mock
    private SearchProvider searchProvider;

    @Mock
    private DataSnapshot snapshot;

    @Captor
    private ArgumentCaptor<ValueEventListener> vel;

    @Captor
    private ArgumentCaptor<DatabaseProvider.AddPoiListener> addPoiListener;

    @Captor
    private ArgumentCaptor<GeoQueryEventListener> geoQueryEventListener;

    @Captor
    private ArgumentCaptor<DatabaseProvider.GetPoiListener> getPOIListener;

    @Captor
    private ArgumentCaptor<SearchProvider.SearchPOIsByTextListener> searchPOIsByTextListener;

    @Captor
    private ArgumentCaptor<DatabaseProvider.ModifyPOIListener> modifyPOIListener;

    @Before
    public void initializers() {
        database = new FirebaseDatabaseProvider(dbRef, geofire, searchProvider);
    }

    @Test
    public void addPoiTest() {
        when(dbRef.child(anyString())).thenReturn(dbRef);
        doNothing().when(dbRef).addListenerForSingleValueEvent(vel.capture());

        doNothing().when(searchProvider).addPoi(any(PointOfInterest.class), addPoiListener.capture());

        final Mutable<String> result = new Mutable<>("");

        DatabaseProvider.AddPoiListener listener = new DatabaseProvider.AddPoiListener() {
            @Override
            public void onSuccess() {
                result.set("SUCCESS");
            }

            @Override
            public void onAlreadyExists() {
                result.set("ALREADYEXISTS");
            }

            @Override
            public void onFailure() {
                result.set("FAILURE");
            }
        };
        when(dbRef.setValue(any(FirebasePointOfInterest.class))).thenReturn(null);
        when(dbRef.removeValue()).thenReturn(null);
        doNothing().when(geofire).setLocation(anyString(), any(GeoLocation.class));
        doNothing().when(geofire).removeLocation(anyString());

        when(snapshot.exists()).thenReturn(false);
        database.addPoi(poiTest, listener);
        vel.getValue().onDataChange(snapshot);
        addPoiListener.getValue().onSuccess();
        assertThat(result.get(), is("SUCCESS"));

        addPoiListener.getValue().onAlreadyExists();

        addPoiListener.getValue().onFailure();
        assertThat(result.get(), is("FAILURE"));

        when(snapshot.exists()).thenReturn(true);
        vel.getValue().onDataChange(snapshot);
        assertThat(result.get(), is("ALREADYEXISTS"));

        vel.getValue().onCancelled(null);
        assertThat(result.get(), is("FAILURE"));
    }

    @Test
    public void GetPoiTest() {
        when(dbRef.child(anyString())).thenReturn(dbRef);
        when(dbRef.addValueEventListener(vel.capture())).thenReturn(null);

        final Mutable<String> result = new Mutable<>("");

        DatabaseProvider.GetPoiListener listener = new DatabaseProvider.GetPoiListener() {
            @Override
            public void onSuccess(PointOfInterest poi) {
                result.set("SUCCESS");
            }

            @Override
            public void onModified(PointOfInterest poi) {
                result.set("MODIFIED");
            }

            @Override
            public void onDoesntExist() {
                result.set("DOESNTEXIST");
            }

            @Override
            public void onFailure() {
                result.set("FAILURE");
            }
        };

        database.getPoi(poiTest.name(), listener);

        when(snapshot.exists()).thenReturn(true);
        when(snapshot.getValue(FirebasePointOfInterest.class)).thenReturn(new FirebasePointOfInterest(poiTest));
        vel.getValue().onDataChange(snapshot);
        assertThat(result.get(), is("SUCCESS"));
        vel.getValue().onDataChange(snapshot);
        assertThat(result.get(), is("MODIFIED"));

        when(snapshot.getValue(FirebasePointOfInterest.class)).thenReturn(null);
        vel.getValue().onDataChange(snapshot);
        assertThat(result.get(), is("FAILURE"));

        when(snapshot.exists()).thenReturn(false);
        vel.getValue().onDataChange(snapshot);
        assertThat(result.get(), is("DOESNTEXIST"));

        vel.getValue().onCancelled(null);
        assertThat(result.get(), is("FAILURE"));
    }

    @Test
    public void modifyPOITest() {
        when(dbRef.child(anyString())).thenReturn(dbRef);
        doNothing().when(dbRef).addListenerForSingleValueEvent(vel.capture());
        doNothing().when(searchProvider).modifyPOI(any(PointOfInterest.class), modifyPOIListener.capture());
        when(dbRef.setValue(any())).thenReturn(null);
        doNothing().when(geofire).setLocation(anyString(), any(GeoLocation.class));
        final Mutable<String> result = new Mutable<>("");

        DatabaseProvider.ModifyPOIListener listener = new DatabaseProvider.ModifyPOIListener() {
            @Override
            public void onSuccess() {
                result.set("SUCCESS");
            }

            @Override
            public void onDoesntExist() {
                result.set("DOESNTEXIST");
            }

            @Override
            public void onFailure() {
                result.set("FAILURE");
            }
        };

        database.modifyPOI(poiTest, listener);
        when(snapshot.exists()).thenReturn(true);
        vel.getValue().onDataChange(snapshot);
        modifyPOIListener.getValue().onSuccess();
        assertThat(result.get(), is("SUCCESS"));
        modifyPOIListener.getValue().onDoesntExist();
        assertThat(result.get(), is("FAILURE"));
        modifyPOIListener.getValue().onFailure();
        assertThat(result.get(), is("FAILURE"));

        when(snapshot.exists()).thenReturn(false);
        vel.getValue().onDataChange(snapshot);
        assertThat(result.get(), is("DOESNTEXIST"));
        vel.getValue().onCancelled(null);
    }

    @Test
    public void upvoteAndDownvoteTest() {
        when(dbRef.child(anyString())).thenReturn(dbRef);
        doNothing().when(dbRef).addListenerForSingleValueEvent(vel.capture());
        ArgumentCaptor<Long> value = ArgumentCaptor.forClass(Long.class);
        when(dbRef.setValue(value.capture())).thenReturn(null);
        final Mutable<String> result = new Mutable<>("");
        DatabaseProvider.ModifyPOIListener listener = new DatabaseProvider.ModifyPOIListener() {
            @Override
            public void onSuccess() {
                result.set("SUCCESS");
            }

            @Override
            public void onDoesntExist() {
                result.set("DOESNTEXIST");
            }

            @Override
            public void onFailure() {
                result.set("FAILURE");
            }
        };
        database.upvote("randomPOI", listener);
        when(snapshot.exists()).thenReturn(true);
        when(snapshot.child(anyString())).thenReturn(snapshot);
        when(snapshot.getValue()).thenReturn((long) 10);
        vel.getValue().onDataChange(snapshot);
        assertThat(result.get(), is("SUCCESS"));
        assertThat(value.getValue(), is((long) 11));
        result.set("");

        when(snapshot.getValue()).thenReturn(null);
        vel.getValue().onDataChange(snapshot);
        assertThat(value.getValue(), is((long) 1));
        assertThat(result.get(), is("SUCCESS"));

        when(snapshot.exists()).thenReturn(false);
        vel.getValue().onDataChange(snapshot);
        assertThat(result.get(), is("DOESNTEXIST"));

        vel.getValue().onCancelled(null);
        assertThat(result.get(), is("FAILURE"));

        // ---- downvote ----

        database.downvote("randomPOI", listener);
        when(snapshot.exists()).thenReturn(true);
        when(snapshot.child(anyString())).thenReturn(snapshot);
        when(snapshot.getValue()).thenReturn((long) 10);
        vel.getValue().onDataChange(snapshot);
        assertThat(result.get(), is("SUCCESS"));
        assertThat(value.getValue(), is((long) 9));
        result.set("");

        when(snapshot.getValue()).thenReturn(null);
        vel.getValue().onDataChange(snapshot);
        assertThat(value.getValue(), is((long) 0));
        assertThat(result.get(), is("SUCCESS"));

        when(snapshot.exists()).thenReturn(false);
        vel.getValue().onDataChange(snapshot);
        assertThat(result.get(), is("DOESNTEXIST"));

        vel.getValue().onCancelled(null);
        assertThat(result.get(), is("FAILURE"));

    }

    @Test
    public void findNearPoisTest() {
        GeoQuery geoQuery = mock(GeoQuery.class);
        when(geofire.queryAtLocation(any(GeoLocation.class), anyDouble())).thenReturn(geoQuery);
        doNothing().when(geoQuery).addGeoQueryEventListener(geoQueryEventListener.capture());

        final Mutable<Boolean> isFailure = new Mutable<>(false);
        final Mutable<Integer> nbPOIs = new Mutable<>(0);

        DatabaseProvider.FindNearPoisListener findPoiListener = new DatabaseProvider.FindNearPoisListener() {
            @Override
            public void onNewValue(PointOfInterest poi) {
                nbPOIs.set(nbPOIs.get() + 1);
            }

            @Override
            public void onFailure() {
                isFailure.set(true);
            }
        };

        FirebaseDatabaseProvider databaseSpy = spy(database);

        doNothing().when(databaseSpy).getPoi(anyString(), getPOIListener.capture());

        databaseSpy.findNearPois(poiTest.position(), 10, findPoiListener);
        geoQueryEventListener.getValue().onKeyEntered("key", null);
        getPOIListener.getValue().onSuccess(poiTest);
        assertThat(nbPOIs.get(), is(1));
        getPOIListener.getValue().onSuccess(poiTest);
        getPOIListener.getValue().onSuccess(poiTest);
        getPOIListener.getValue().onSuccess(poiTest);
        getPOIListener.getValue().onSuccess(poiTest);
        assertThat(nbPOIs.get(), is(5));
        getPOIListener.getValue().onModified(poiTest);
        getPOIListener.getValue().onDoesntExist();
        getPOIListener.getValue().onFailure();
        geoQueryEventListener.getValue().onKeyExited("key");
        geoQueryEventListener.getValue().onKeyMoved("key", null);
        geoQueryEventListener.getValue().onGeoQueryReady();
        geoQueryEventListener.getValue().onGeoQueryError(null);
        assertTrue(isFailure.get());
    }

    @Test
    public void searchByTextTest() {
        doNothing().when(searchProvider).searchByText(anyString(), searchPOIsByTextListener.capture());

        final Mutable<Boolean> isFailure = new Mutable<>(false);
        final Mutable<Integer> nbPOIs = new Mutable<>(0);

        DatabaseProvider.SearchPOIByTextListener listener = new DatabaseProvider.SearchPOIByTextListener() {
            @Override
            public void onNewValue(PointOfInterest poi) {
                nbPOIs.set(nbPOIs.get() + 1);
            }

            @Override
            public void onFailure() {
                isFailure.set(true);
            }
        };

        FirebaseDatabaseProvider databaseSpy = spy(database);

        doNothing().when(databaseSpy).getPoi(anyString(), getPOIListener.capture());

        databaseSpy.searchByText("", listener);
        searchPOIsByTextListener.getValue().onSuccess(Collections.singletonList("poi"));

        assertThat(nbPOIs.get(), is(0));
        getPOIListener.getValue().onSuccess(null);
        assertThat(nbPOIs.get(), is(1));
        getPOIListener.getValue().onSuccess(null);
        getPOIListener.getValue().onSuccess(null);
        getPOIListener.getValue().onSuccess(null);
        getPOIListener.getValue().onSuccess(null);
        assertThat(nbPOIs.get(), is(5));
        getPOIListener.getValue().onModified(poiTest);
        getPOIListener.getValue().onDoesntExist();
        getPOIListener.getValue().onFailure();
        assertThat(nbPOIs.get(), is(5));

        searchPOIsByTextListener.getValue().onFailure();
        assertTrue(isFailure.get());

    }
}
