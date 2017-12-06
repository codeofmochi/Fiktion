package ch.epfl.sweng.fiktion.providers;

import android.util.Log;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.sweng.fiktion.models.Comment;
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
    private final String poisRefName = "Points of interest";
    private final String usersRefName = "Users";
    private final String commentsRef = "Comments";

    /**
     * Constructs a firebase database class that provides database methods
     */
    public FirebaseDatabaseProvider() {
        dbRef = FirebaseDatabase.getInstance().getReference();
        geofire = new GeoFire(dbRef.child("geofire"));
        searchProvider = new AlgoliaSearchProvider();
    }

    /**
     * Constructs a firebase database class with the given fields. Mainly used for testing
     */
    public FirebaseDatabaseProvider(DatabaseReference dbRef, GeoFire geofire, SearchProvider searchProvider) {
        this.dbRef = dbRef;
        this.geofire = geofire;
        this.searchProvider = searchProvider;
    }

    /**
     * encodes a String so that firebase can store it, use decode to decode it
     *
     * @param s the String to encode
     * @return the encoded String
     */
    public static String encode(String s) {
        return s.replace("%", "%%")
                .replace(".", "%P")
                .replace("$", "%D")
                .replace("[", "%O")
                .replace("]", "%C")
                .replace("#", "%H")
                .replace("/", "%S");
    }

    /**
     * decodes an encoded String to get back is value
     *
     * @param s the encoded String
     * @return the decoded String
     */
    public static String decode(String s) {
        return s.replace("%%", "%")
                .replace("%P", ".")
                .replace("%D", "$")
                .replace("%O", "[")
                .replace("%C", "]")
                .replace("%H", "#")
                .replace("%S", "/");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addPoi(final PointOfInterest poi, final AddPoiListener listener) {
        final String poiName = poi.name();

        // get/create the reference of the point of interest
        final DatabaseReference poiRef = dbRef.child(poisRefName).child(poiName);

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
        DatabaseReference poiRef = dbRef.child(poisRefName).child(poiName);
        poiRef.addValueEventListener(new ValueEventListener() {
            private boolean firstCall = true;

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    FirebasePointOfInterest fPoi = dataSnapshot.getValue(FirebasePointOfInterest.class);
                    if (fPoi == null) {
                        listener.onFailure();
                    } else {
                        if (firstCall) {
                            // inform the listener that we got the matching poi
                            listener.onSuccess(fPoi.toPoi());
                            firstCall = false;
                        } else {
                            //inform the listener that the poi has been modified
                            listener.onModified(fPoi.toPoi());
                        }
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
    public void modifyPOI(final PointOfInterest poi, final ModifyPOIListener listener) {
        final DatabaseReference poiRef = dbRef.child(poisRefName).child(poi.name());
        poiRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    // modify the poi in the search provider
                    searchProvider.modifyPOI(poi, new ModifyPOIListener() {
                        @Override
                        public void onSuccess() {
                            // if it succeeds, replace the poi value in firebase
                            FirebasePointOfInterest fPOI = new FirebasePointOfInterest(poi);
                            poiRef.setValue(fPOI);

                            // update the position for geofire
                            Position pos = poi.position();
                            final GeoLocation geoLocation = new GeoLocation(pos.latitude(), pos.longitude());
                            geofire.setLocation(poi.name(), geoLocation);

                            // and inform the listener of the success of the modification
                            listener.onSuccess();
                        }

                        @Override
                        public void onDoesntExist() {
                            // if it doesn't exist in the search provider, we got a coherence problem -> failure
                            listener.onFailure();
                        }

                        @Override
                        public void onFailure() {
                            // inform the listener that the operation failed
                            listener.onFailure();
                        }
                    });
                } else {
                    // inform the listener that the point of interest doesn't exist
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
    public void upvote(String poiName, final ModifyPOIListener listener) {
        final DatabaseReference poiRef = dbRef.child(poisRefName).child(poiName);
        poiRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Object value = dataSnapshot.child("rating").getValue();
                    // if value is null, then the poi was created before pois had rating, set it to 0
                    long rating = value == null ? 0 : (long) value;
                    poiRef.child("rating").setValue(rating + 1);
                    listener.onSuccess();
                } else {
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
    public void downvote(String poiName, final ModifyPOIListener listener) {
        final DatabaseReference poiRef = dbRef.child(poisRefName).child(poiName);
        poiRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Object value = dataSnapshot.child("rating").getValue();
                    long rating = value == null ? 0 : (long) value;
                    // if value is null, then the poi was created before pois had rating, set it to 0
                    poiRef.child("rating").setValue(rating - 1);
                    listener.onSuccess();
                } else {
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
                    public void onModified(PointOfInterest poi) {

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
                        public void onModified(PointOfInterest poi) {
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
        final DatabaseReference userRef = dbRef.child(usersRefName).child(user.getID());

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
        DatabaseReference userRef = dbRef.child(usersRefName).child(id);
        userRef.addValueEventListener(new ValueEventListener() {
            private boolean firstCall = true;

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("mylogs", "onDataChange: ");
                if (dataSnapshot.exists()) {
                    FirebaseUser fUser = dataSnapshot.getValue(FirebaseUser.class);
                    if (fUser == null) {
                        Log.d("mylogs", "onDataChange: if");
                        // we found the id but conversion failed, error of data handling probably
                        listener.onFailure();
                    } else {
                        Log.d("mylogs", "onDataChange: else");
                        if (firstCall) {
                            Log.d("mylogs", "onDataChange: elseif");
                            // inform the listener that we got the matching user
                            listener.onSuccess(fUser.toUser());
                            firstCall = false;
                        } else {
                            Log.d("mylogs", "onDataChange: elseelse");
                            // inform the listener that the user has been modified
                            listener.onModified(fUser.toUser());
                        }
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
        final DatabaseReference userRef = dbRef.child(usersRefName).child(id);
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
        final DatabaseReference userRef = dbRef.child(usersRefName).child(user.getID());
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void addComment(final Comment comment, String poiName, final AddCommentListener listener) {
        // get the poi comments reference
        final DatabaseReference cPOIRef = dbRef.child(commentsRef).child(poiName);
        cPOIRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // append the new comment to the list of comments of the point of interest
                String index = String.valueOf(dataSnapshot.getChildrenCount());
                FirebaseComment fComment = new FirebaseComment(comment);
                cPOIRef.child(index).setValue(fComment);

                // inform to the listener that the operation succeeded
                listener.onSuccess();
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
    public void getComments(String poiName, final GetCommentsListener listener) {
        // get the poi comments reference
        final DatabaseReference cPOIRef = dbRef.child(commentsRef).child(poiName);

        cPOIRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                // convert into a comment
                FirebaseComment fComment = dataSnapshot.getValue(FirebaseComment.class);
                if (fComment != null) {
                    // send the comment to the listener
                    listener.onNewValue(fComment.toComment());
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // inform the listener that the operation failed
                listener.onFailure();
            }
        });
    }


}
