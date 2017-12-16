package ch.epfl.sweng.fiktion.providers;

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

import java.util.List;

import ch.epfl.sweng.fiktion.models.Comment;
import ch.epfl.sweng.fiktion.models.PointOfInterest;
import ch.epfl.sweng.fiktion.models.Position;
import ch.epfl.sweng.fiktion.models.User;
import ch.epfl.sweng.fiktion.models.posts.AddPOIPost;
import ch.epfl.sweng.fiktion.models.posts.CommentPOIPost;
import ch.epfl.sweng.fiktion.models.posts.FavoritePOIPost;
import ch.epfl.sweng.fiktion.models.posts.PhotoUploadPost;
import ch.epfl.sweng.fiktion.models.posts.Post;
import ch.epfl.sweng.fiktion.models.posts.PostType;
import ch.epfl.sweng.fiktion.models.posts.VisitPOIPost;
import ch.epfl.sweng.fiktion.models.posts.WishlistPOIPost;
import ch.epfl.sweng.fiktion.providers.firebase_models.FirebaseAddPOIPost;
import ch.epfl.sweng.fiktion.providers.firebase_models.FirebaseComment;
import ch.epfl.sweng.fiktion.providers.firebase_models.FirebaseCommentPOIPost;
import ch.epfl.sweng.fiktion.providers.firebase_models.FirebaseFavoritePOIPost;
import ch.epfl.sweng.fiktion.providers.firebase_models.FirebasePhotoUploadPost;
import ch.epfl.sweng.fiktion.providers.firebase_models.FirebasePost;
import ch.epfl.sweng.fiktion.providers.firebase_models.FirebaseUser;
import ch.epfl.sweng.fiktion.providers.firebase_models.FirebaseVisitPOIPost;
import ch.epfl.sweng.fiktion.providers.firebase_models.FirebaseWishlistPOIPost;

/**
 * Firebase database provider
 *
 * @author pedro
 */
public class FirebaseDatabaseProvider extends DatabaseProvider {
    private DatabaseReference dbRef;
    private GeoFire geofire;
    private SearchProvider searchProvider;
    private static final String poisRefName = "Points of interest";
    private static final String usersRefName = "Users";
    private static final String commentsRef = "Comments";
    private static final String poiCommentsRef = "POI comments";
    private static final String commentVotersRef = "Comment voters";
    private static final String userPostsRef = "User posts";

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

    /*
     * --------------------------------------------------------------------------------
     * ----------------------------------POI methods-----------------------------------
     * --------------------------------------------------------------------------------
     */

    /**
     * {@inheritDoc}
     */
    @Override
    public void addPOI(final PointOfInterest poi, final AddPOIListener listener) {
        final String poiName = poi.name();

        if (poiName.isEmpty()) {
            listener.onFailure();
        }

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
                    searchProvider.addPOI(poi, new AddPOIListener() {
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
    public void getPOI(String poiName, final GetPOIListener listener) {
        if (poiName.isEmpty()) {
            listener.onFailure();
        }

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
                            listener.onNewValue(fPoi.toPoi());
                            firstCall = false;
                        } else {
                            //inform the listener that the poi has been modified
                            listener.onModifiedValue(fPoi.toPoi());
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
        final String poiName = poi.name();

        if (poiName.isEmpty()) {
            listener.onFailure();
        }

        final DatabaseReference poiRef = dbRef.child(poisRefName).child(poiName);
        poiRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    // modify the poi in the search provider
                    searchProvider.modifyPOI(poi, new ModifyPOIListener() {
                        @Override
                        public void onSuccess() {
                            // if it succeeds, replace the poi value in firebase
                            FirebasePointOfInterest fPOI = new FirebasePointOfInterest(poi);
                            // don't change the rating
                            Object value = dataSnapshot.getValue(FirebasePointOfInterest.class);
                            fPOI.rating = value == null ? 0 : ((FirebasePointOfInterest) value).rating;
                            poiRef.setValue(fPOI);

                            // update the position for geofire
                            Position pos = poi.position();
                            final GeoLocation geoLocation = new GeoLocation(pos.latitude(), pos.longitude());
                            geofire.setLocation(poiName, geoLocation);

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
        if (poiName.isEmpty()) {
            listener.onFailure();
        }

        final DatabaseReference poiRef = dbRef.child(poisRefName).child(poiName);
        poiRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // get the rating value of the poi
                    Object value = dataSnapshot.child("rating").getValue();

                    // if the poi doesn't have the field rating, set it to 0 (+1 for the upvote)
                    long newRating = value == null ? 1 : (long) value + 1;
                    poiRef.child("rating").setValue(newRating);

                    // inform the listener that the upvote succeeded
                    listener.onSuccess();
                } else {
                    // inform the listener that the poi doesn't exist
                    listener.onDoesntExist();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // inform the listener that the upvote failed
                listener.onFailure();
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    public void downvote(String poiName, final ModifyPOIListener listener) {
        if (poiName.isEmpty()) {
            listener.onFailure();
        }

        final DatabaseReference poiRef = dbRef.child(poisRefName).child(poiName);
        poiRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // get the rating value of the poi
                    Object value = dataSnapshot.child("rating").getValue();

                    long newRating;
                    if (value == null) {
                        // if the poi doesn't have the field rating, set it to 0
                        newRating = 0;
                    } else {
                        // decrement the rating value
                        newRating = (long) value - 1;
                        if (newRating < 0) {
                            // this should not happen since we are removing a vote from someone who
                            // upvoted but if the rating is negative, set it to 0
                            newRating = 0;
                        }
                    }

                    // set the new rating
                    poiRef.child("rating").setValue(newRating);

                    // inform the listener that the downvote succeeded
                    listener.onSuccess();
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
    public void findNearPOIs(Position pos, int radius, final FindNearPOIsListener listener) {
        // query the points of interests within the radius
        GeoQuery geoQuery = geofire.queryAtLocation(new GeoLocation(pos.latitude(), pos.longitude()), radius);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(final String key, GeoLocation location) {
                // for a near poi, retrieve it from the firebase
                getPOI(key, new GetPOIListener() {
                    @Override
                    public void onNewValue(PointOfInterest poi) {
                        // inform the listener that we got a new poi
                        listener.onNewValue(poi);
                    }

                    @Override
                    public void onModifiedValue(PointOfInterest poi) {

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
                    getPOI(poiID, new GetPOIListener() {
                        @Override
                        public void onNewValue(PointOfInterest poi) {
                            listener.onNewValue(poi);
                        }

                        @Override
                        public void onModifiedValue(PointOfInterest poi) {
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

    /*
     * --------------------------------------------------------------------------------
     * ----------------------------------User methods----------------------------------
     * --------------------------------------------------------------------------------
     */

    /**
     * {@inheritDoc}
     */
    @Override
    public void addUser(final User user, final AddUserListener listener) {
        if (user.getID().isEmpty()) {
            listener.onFailure();
        }

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
        if (id.isEmpty()) {
            listener.onFailure();
        }

        // get the reference of the user associated with the id
        DatabaseReference userRef = dbRef.child(usersRefName).child(id);
        userRef.addValueEventListener(new ValueEventListener() {
            private boolean firstCall = true;

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    FirebaseUser fUser = dataSnapshot.getValue(FirebaseUser.class);
                    if (fUser == null) {
                        // we found the id but conversion failed, error of data handling probably
                        listener.onFailure();
                    } else {
                        if (firstCall) {
                            // inform the listener that we got the matching user
                            listener.onNewValue(fUser.toUser());
                            firstCall = false;
                        } else {
                            // inform the listener that the user has been modified
                            listener.onModifiedValue(fUser.toUser());
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
        if (id.isEmpty()) {
            listener.onFailure();
        }

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
        if (user.getID().isEmpty()) {
            listener.onFailure();
        }

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

    /*
     * --------------------------------------------------------------------------------
     * --------------------------------Comment methods---------------------------------
     * --------------------------------------------------------------------------------
     */

    /**
     * {@inheritDoc}
     */
    @Override
    public void addComment(final Comment comment, final String poiName, final AddCommentListener listener) {
        if (comment.getId().isEmpty()) {
            listener.onFailure();
        }

        // get the comment reference
        final DatabaseReference commentRef = dbRef.child(commentsRef).child(comment.getId());
        commentRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // if the comment already exists, inform the listener of a failure
                    listener.onFailure();
                    return;
                }

                // add the new comment
                FirebaseComment fComment = new FirebaseComment(comment);
                commentRef.setValue(fComment);

                // add the comment id to
                dbRef.child(poiCommentsRef).child(poiName).child(comment.getId()).setValue(true);

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
    public void getComment(String commentId, final GetCommentListener listener) {
        if (commentId.isEmpty()) {
            listener.onFailure();
        }

        // get the comment reference
        DatabaseReference commentRef = dbRef.child(commentsRef).child(commentId);
        commentRef.addValueEventListener(new ValueEventListener() {
            private boolean firstCall = true;

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    FirebaseComment fComment = dataSnapshot.getValue(FirebaseComment.class);
                    if (fComment == null) {
                        listener.onFailure();
                    } else {
                        if (firstCall) {
                            // inform the listener that we got the matching comment
                            listener.onNewValue(fComment.toComment());
                            firstCall = false;
                        } else {
                            // inform the listener that the comment has been modified
                            listener.onModifiedValue(fComment.toComment());
                        }
                    }
                } else {
                    // inform the listener that the comment doesn't exist
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
    public void getPOIComments(String poiName, final GetCommentsListener listener) {
        if (poiName.isEmpty()) {
            listener.onFailure();
        }

        // get the poi comments reference
        final DatabaseReference cPOIRef = dbRef.child(poiCommentsRef).child(poiName);

        // get the comments ids and get the actual comments
        cPOIRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                getComment(dataSnapshot.getKey(), new GetCommentListener() {
                    @Override
                    public void onNewValue(Comment comment) {
                        // inform the listener with every new comment
                        listener.onNewValue(comment);
                    }

                    @Override
                    public void onModifiedValue(Comment comment) {
                        // inform the listener when an already retrieved poi has been modified
                        listener.onModifiedValue(comment);
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void voteComment(final String commentId, final String userID, final int vote, final int previousVote, final VoteListener listener) {
        if (commentId.isEmpty()) {
            listener.onFailure();
        }

        // get the comment reference
        final DatabaseReference commentRef = dbRef.child(commentsRef).child(commentId);

        commentRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // get the rating
                    Object value = dataSnapshot.child("rating").getValue();
                    long rating = value == null ? 0 : (long) value;

                    // make the rating modification according to the vote and the previous vote
                    commentRef.child("rating").setValue(rating + vote - previousVote);

                    // record the vote state of the user
                    if (vote == NOVOTE) {
                        dbRef.child(commentVotersRef).child(commentId).child(userID).removeValue();
                    } else {
                        dbRef.child(commentVotersRef).child(commentId).child(userID).setValue(vote);
                    }

                    // inform the listener that the upvote succeeded
                    listener.onSuccess();
                } else {
                    // if there is no comment at the reference, inform the listener that the comment doesn't exist
                    listener.onFailure();
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
    public void getCommentVoteOfUser(String commentId, String userID, final GetVoteListener listener) {
        if (commentId.isEmpty()) {
            listener.onFailure();
        }

        // get the vote reference associated to the user for the comment
        DatabaseReference voteRef = dbRef.child(commentVotersRef).child(commentId).child(userID);

        voteRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int vote;
                if (dataSnapshot.exists()) {
                    Object voteValue = dataSnapshot.getValue();
                    long longVoteValue = voteValue == null ? 0 : (long) voteValue;
                    if (longVoteValue < 0) {
                        vote = DatabaseProvider.DOWNVOTE;
                    } else if (longVoteValue > 0) {
                        vote = DatabaseProvider.UPVOTE;
                    } else {
                        vote = DatabaseProvider.NOVOTE;
                    }
                } else {
                    // if it doesn't exist, then the user hasn't voted
                    vote = DatabaseProvider.NOVOTE;
                }
                // inform the listener of the vote
                listener.onNewValue(vote);
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
    public void addUserPost(String userId, Post post, AddPostListener listener) {
        if (userId.isEmpty() || post.getId().isEmpty()) {
            listener.onFailure();
            return;
        }

        // get the reference of the new post
        DatabaseReference postRef = dbRef.child(userPostsRef).child(userId).child(post.getId());

        // convert it in its firebase version
        FirebasePost fPost;
        try {
            switch (post.getType()) {
                case ADD_POI:
                    fPost = new FirebaseAddPOIPost((AddPOIPost) post);
                    break;

                case VISIT_POI:
                    fPost = new FirebaseVisitPOIPost((VisitPOIPost) post);
                    break;

                case COMMENT_POI:
                    fPost = new FirebaseCommentPOIPost((CommentPOIPost) post);
                    break;

                case FAVORITE_POI:
                    fPost = new FirebaseFavoritePOIPost((FavoritePOIPost) post);
                    break;

                case PHOTO_UPLOAD:
                    fPost = new FirebasePhotoUploadPost((PhotoUploadPost) post);
                    break;

                case WISHLIST_POI:
                    fPost = new FirebaseWishlistPOIPost((WishlistPOIPost) post);
                    break;

                default:
                    listener.onFailure();
                    return;
            }
        } catch (ClassCastException e) {
            // if there is a cast problem (shouldn't happen), abort the operation
            listener.onFailure();
            return;
        }

        // set the value in firebase
        postRef.setValue(fPost);
        postRef.child("invMilliseconds").setValue(-post.getDate().getTime());
        listener.onSuccess();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void getUserPosts(String userId, final GetPostListener listener) {
        if (userId.isEmpty()) {
            listener.onFailure();
            return;
        }

        // get the posts reference of the user
        DatabaseReference userPostsReference = dbRef.child(userPostsRef).child(userId);

        // order the posts by time (inverse of milliseconds to get in descending order) and get the posts
        userPostsReference.orderByChild("invMilliseconds").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                // get the type of the post
                PostType type = dataSnapshot.child("type").getValue(PostType.class);
                if (type == null) {
                    return;
                }

                // get the post in its firebase form
                FirebasePost fPost;
                switch (type) {
                    case ADD_POI:
                        fPost = dataSnapshot.getValue(FirebaseAddPOIPost.class);
                        break;

                    case VISIT_POI:
                        fPost = dataSnapshot.getValue(FirebaseVisitPOIPost.class);
                        break;

                    case COMMENT_POI:
                        fPost = dataSnapshot.getValue(FirebaseCommentPOIPost.class);
                        break;

                    case FAVORITE_POI:
                        fPost = dataSnapshot.getValue(FirebaseFavoritePOIPost.class);
                        break;

                    case PHOTO_UPLOAD:
                        fPost = dataSnapshot.getValue(FirebasePhotoUploadPost.class);
                        break;

                    case WISHLIST_POI:
                        fPost = dataSnapshot.getValue(FirebaseWishlistPOIPost.class);
                        break;

                    default:
                        return;
                }
                if (fPost != null) {
                    // convert it into a real post and inform the listener
                    listener.onNewValue(fPost.toPost());
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
                listener.onFailure();
            }
        });
    }


}
