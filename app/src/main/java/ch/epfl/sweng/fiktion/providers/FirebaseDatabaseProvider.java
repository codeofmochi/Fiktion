package ch.epfl.sweng.fiktion.providers;

import android.util.Log;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ch.epfl.sweng.fiktion.models.PointOfInterest;
import ch.epfl.sweng.fiktion.models.Position;

/**
 * Firebase database provider
 *
 * @author pedro
 */
public class FirebaseDatabaseProvider extends DatabaseProvider {
    private final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
    private final GeoFire geofire = new GeoFire(dbRef.child("geofire"));

    /**
     * {@inheritDoc}
     */
    @Override
    public void addPoi(final PointOfInterest poi, final AddPoiListener listener) {
        final String poiName = poi.name();
        // get/create the reference of the point of interest
        final DatabaseReference poiRef = dbRef.child("Points of interest").child(poiName);
        // add only if the reference doesn't exist
        poiRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // inform the listener that the poi already exists
                    listener.onAlreadyExists();
                } else {
                    // set values in database
                    FirebasePointOfInterest fPoi = new FirebasePointOfInterest(poi);
                    poiRef.setValue(fPoi);
                    Position pos = poi.position();
                    geofire.setLocation(poiName, new GeoLocation(pos.latitude(), pos.longitude()));

                    // inform the listener that the operation succeeded
                    listener.onSuccess();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // inform the listener that the operation failed
                listener.onFailure();
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void getPoi(String poiName, final GetPoiListener listener) {
        // get the reference of the poi
        final DatabaseReference poiRef = dbRef.child("Points of interest").child(poiName);
        poiRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("mylogs", "getPoiDatachange");
                if (dataSnapshot.exists()) {
                    Log.d("mylogs", dataSnapshot.toString());
                    FirebasePointOfInterest fPoi = dataSnapshot.getValue(FirebasePointOfInterest.class);
                    if (fPoi == null) {
                        listener.onFailure();
                    } else {
                        // inform the listener that we got the matching poi
                        listener.onSuccess(fPoi.toPoi());
                    }
                    Log.d("mylogs", "getPoiDone");
                } else {
                    Log.d("mylogs", "getPoiDoesntExist");
                    // inform the listener that the poi doesn't exist
                    listener.onDoesntExist();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // inform the listener that the operation failed
                listener.onFailure();
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void findNearPois(Position pos, int radius, final FindNearPoisListener listener) {
        // query the points of interests within the radius
        GeoQuery geoQuery = geofire.queryAtLocation(new GeoLocation(pos.latitude(), pos.longitude()), radius);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(final String key, GeoLocation location) {
                // for a near poi, retrieve it from the firebase
                getPoi(key, new GetPoiListener() {
                    @Override
                    public void onSuccess(PointOfInterest poi) {
                        // inform the listener that we got a new poi
                        listener.onNewValue(poi);
                    }

                    @Override
                    public void onDoesntExist() {

                    }

                    @Override
                    public void onFailure() {

                    }
                });

            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {
                // inform the listener that the operation failed
                listener.onFailure();
            }
        });
    }


}
