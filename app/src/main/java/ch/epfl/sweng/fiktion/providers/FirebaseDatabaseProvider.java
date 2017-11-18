package ch.epfl.sweng.fiktion.providers;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import ch.epfl.sweng.fiktion.models.PointOfInterest;
import ch.epfl.sweng.fiktion.models.Position;
import ch.epfl.sweng.fiktion.models.User;

/**
 * Firebase database provider
 *
 * @author pedro
 */
public class FirebaseDatabaseProvider extends DatabaseProvider {
    private DatabaseReference dbRef;
    private GeoFire geofire;
    private SearchProvider searchProvider;

    public FirebaseDatabaseProvider() {
        dbRef = FirebaseDatabase.getInstance().getReference();
        geofire = new GeoFire(dbRef.child("geofire"));
        searchProvider = new AlgoliaSearchProvider();
    }

    public FirebaseDatabaseProvider(DatabaseReference dbRef, GeoFire geofire, SearchProvider searchProvider) {
        this.dbRef = dbRef;
        this.geofire = geofire;
        this.searchProvider = searchProvider;
    }

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
                    final GeoLocation geoLocation = new GeoLocation(pos.latitude(), pos.longitude());
                    geofire.setLocation(poiName, geoLocation);

                    // add the poi also to the search provider
                    searchProvider.addPoi(poi, new AddPoiListener() {
                        @Override
                        public void onSuccess() {
                            listener.onSuccess();
                        }

                        @Override
                        public void onAlreadyExists() {

                        }

                        @Override
                        public void onFailure() {
                            poiRef.removeValue();
                            geofire.removeLocation(poiName);
                            listener.onFailure();
                        }
                    });
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
        DatabaseReference poiRef = dbRef.child("Points of interest").child(poiName);
        poiRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    FirebasePointOfInterest fPoi = dataSnapshot.getValue(FirebasePointOfInterest.class);
                    if (fPoi == null) {
                        listener.onFailure();
                    } else {
                        // inform the listener that we got the matching poi
                        listener.onSuccess(fPoi.toPoi());
                    }
                } else {
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


    /**
     * {@inheritDoc}
     */
    public void searchByText(String text, final SearchPOIByTextListener listener) {
        // ask the search provider to retrieve the pois
        searchProvider.searchByText(text, new SearchProvider.SearchPOIsByTextListener() {
            @Override
            public void onSuccess(List<String> poiIDs) {
                for (String poiID : poiIDs) {
                    getPoi(poiID, new GetPoiListener() {
                        @Override
                        public void onSuccess(PointOfInterest poi) {
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
            }

            @Override
            public void onFailure() {
                listener.onFailure();
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addUser(final User user, final AddUserListener listener) {
        // get/create the reference of the user
        final DatabaseReference userRef = dbRef.child("Users").child(user.getID());

        // add only if the reference doesn't exist
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // inform the listener that the user already exists
                    listener.onAlreadyExists();
                } else {
                    // add the user to the database
                    FirebaseUser fUser = new FirebaseUser(user);
                    userRef.setValue(fUser);

                    //inform the listener that the operation succeeded
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
    public void getUserById(String id, final GetUserListener listener) {
        // get the reference of the user associated with the id
        DatabaseReference userRef = dbRef.child("Users").child(id);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    FirebaseUser fUser = dataSnapshot.getValue(FirebaseUser.class);
                    if (fUser == null) {
                        // we found the id but conversion failed, error of data handling probably
                        listener.onFailure();
                    } else {
                        // inform the listener that we got the matching user
                        listener.onSuccess(fUser.toUser());
                    }
                } else {
                    // inform the listener that the user (id) doesnt exist
                    listener.onDoesntExist();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onFailure();
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleterUserById(String id, final DeleteUserListener listener) {
        // get the reference of the user associated with the id
        final DatabaseReference userRef = dbRef.child("Users").child(id);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // if it exists, remove it by removing its value
                    userRef.removeValue();

                    // and inform the listener of the success of the deletion
                    listener.onSuccess();
                } else {
                    // inform the listener that the user (id) doesn't exist
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
    public void modifyUser(final User user, final ModifyUserListener listener) {
        // get the reference of the user
        final DatabaseReference userRef = dbRef.child("Users").child(user.getID());
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // if it exists, replace its value
                    FirebaseUser fUser = new FirebaseUser(user);
                    userRef.setValue(fUser);

                    // and inform the listener of the success of the modification
                    listener.onSuccess();
                } else {
                    // inform the listener that the user doesn't exist
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


}
