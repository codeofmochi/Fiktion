package ch.epfl.sweng.fiktion;

import com.firebase.geofire.GeoFire;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.sweng.fiktion.models.PointOfInterest;
import ch.epfl.sweng.fiktion.models.Position;
import ch.epfl.sweng.fiktion.providers.DatabaseProvider;

import static ch.epfl.sweng.fiktion.FirebaseTest.Result.ALREADYEXISTS;
import static ch.epfl.sweng.fiktion.FirebaseTest.Result.FAILURE;
import static ch.epfl.sweng.fiktion.FirebaseTest.Result.SUCCESS;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

/**
 * Created by pedro on 23/10/17.
 */

@RunWith(PowerMockRunner.class)
public class FirebaseTest {
    List<PointOfInterest> pois = new ArrayList<>();

    PointOfInterest poi = new PointOfInterest("", new Position(0, 0));
    @Mock
    DatabaseReference dbRef;

    @Mock
    DatabaseReference poiRef;

    @Mock
    GeoFire geofire;

    public Result result = FAILURE;

    public enum Result {SUCCESS, ALREADYEXISTS, FAILURE}

    public void setResult(Result result) {
        this.result = result;
    }

    public void setPoi(String poiName) {
        poi = new PointOfInterest(poiName, new Position(0, 0));
    }


    @Test
    public void addPoiTest() throws Exception {
        when(FirebaseDatabase.getInstance().getReference()).thenReturn(dbRef);
        whenNew(GeoFire.class).withAnyArguments().thenReturn(geofire);
        when(dbRef.child("Points of interest").child(anyString())).thenReturn(poiRef);
        doNothing().when(poiRef).addListenerForSingleValueEvent(any(ValueEventListener.class));
        final DatabaseProvider.AddPoiListener listener = new DatabaseProvider.AddPoiListener() {
            @Override
            public void onSuccess() {
                setResult(SUCCESS);
                pois.add(poi);
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

        if (pois.contains(poi)) {
            listener.onAlreadyExists();
        } else {
            listener.onSuccess();
        }

        assertThat(result, is(SUCCESS));
    }
}
