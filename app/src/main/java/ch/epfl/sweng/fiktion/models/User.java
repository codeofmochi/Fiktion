package ch.epfl.sweng.fiktion.models;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import ch.epfl.sweng.fiktion.providers.AuthProvider;
import ch.epfl.sweng.fiktion.providers.DatabaseProvider;

/**
 * This class represents the User in the application
 *
 * @author Rodrigo, Christoph
 */

public class User {
    private String name;
    //we could use same id as firebase id or create our own id system
    private final String id;
    private Boolean isPublicProfile;
    private Set<String> favourites;
    private Set<String> wishlist;
    private LinkedList<String> visited;
    //private Set<String> rated;
    private Set<String> friendlist;
    private Set<String> friendRequests;

    /**
     * Creates a new User with given parameters
     *
     * @param input_name Username
     * @param input_id   User id
     * @param favs list of favourite POIs
     * @param visits list of visited POIs
     * @param wishes POIs wish list
     * @param friends User friend list
     */
    public User(String input_name, String input_id, Set<String> favs,
                Set<String> wishes, Set<String> friends, Set<String> fRequests,
                LinkedList<String> visits, Boolean isPublic) {
        name = input_name;
        id = input_id;
        favourites = favs;
        wishlist = wishes;
        visited = visits;
        friendlist = friends;
        friendRequests = fRequests;
        isPublicProfile = isPublic;
    }

    /**
     * Creates a new User with given parameters
     *
     * @param input_name Username
     * @param input_id   User id
     * @param favs list of favourite POIs
     * @param wishes POIs wish list
     * @param visits list of visited POIs
     */
    public User(String input_name, String input_id, Set<String> favs, Set<String> wishes, LinkedList<String> visits) {
        name = input_name;
        id = input_id;
        favourites = favs;
        wishlist = wishes;
        visited = visits;
        friendlist = new TreeSet<>();
        friendRequests = new TreeSet<>();
        isPublicProfile = true;
    }

    /**
     * Creates a new User with given parameters
     *
     * @param input_name Username
     * @param input_id   User id
     */
    public User(String input_name, String input_id) {
        name = input_name;
        id = input_id;
        favourites = new TreeSet<>();
        wishlist = new TreeSet<>();
        visited = new LinkedList<>();
        friendlist = new TreeSet<>();
        friendRequests = new TreeSet<>();
        isPublicProfile = true;
    }

    /**
     * Changes the state of the user's profile privacy (true/false)
     *
     * @param db database containing the user data
     * @param privacyState The state of the privacy
     * @param listener Handles what happens in case of success or failure of the change
     */
    public void changeProfilePrivacy(final DatabaseProvider db, Boolean privacyState, final AuthProvider.AuthListener listener) {
        isPublicProfile = privacyState;
        db.modifyUser(this, new DatabaseProvider.ModifyUserListener() {
            @Override
            public void onSuccess() {
                listener.onSuccess();
            }

            @Override
            public void onDoesntExist() {
                listener.onFailure();
            }

            @Override
            public void onFailure() {
                listener.onFailure();
            }
        });
    }

    /**
     * Accept a friend request by adding it to the friend list if it is in the requests
     *
     * @param db database containing the user data
     * @param friendID the friend (user) that the user want to add to his friend list
     * @param listener Handles what happens in case of success or failure of the change
     */
    public void acceptFriendRequest(final DatabaseProvider db, final String friendID, final DatabaseProvider.ModifyUserListener listener) {
        if(friendRequests.contains(friendID)) {
            // Access other user (friend)
            db.getUserById(friendID, new DatabaseProvider.GetUserListener() {
                @Override
                public void onSuccess(User user) {
                    // modify the friend
                    db.modifyUser(user.addFriend(id), new DatabaseProvider.ModifyUserListener() {
                        @Override
                        public void onSuccess() {
                            // modify the user
                            db.modifyUser(User.this.removeRequest(friendID).addFriend(friendID), new DatabaseProvider.ModifyUserListener() {
                                @Override
                                public void onSuccess() {
                                    listener.onSuccess();
                                }

                                @Override
                                public void onDoesntExist() {
                                    // --> the user accepting the request doesn't exist anymore
                                    listener.onDoesntExist();
                                }

                                @Override
                                public void onFailure() {
                                    // --> error modifying the user
                                    listener.onFailure();
                                }
                            });
                        }

                        @Override
                        public void onDoesntExist() {
                            // --> friend we just accessed doesn't exist anymore
                            listener.onDoesntExist();
                        }

                        @Override
                        public void onFailure() {
                            // --> error on modifying friend
                            listener.onFailure();
                        }
                    });
                }

                @Override
                public void onDoesntExist() {
                    // --> report that friend doesn't exist
                    listener.onDoesntExist();
                }

                @Override
                public void onFailure() {
                    // --> report couldn't access friend (database error)
                    listener.onFailure();
                }
            });
        } else {
            // should not happen, false request (programmer error)
            listener.onFailure();
        }
    }

    /**
     * Ignore friend request by removing it from the request list
     *
     * @param db database containing the user data
     * @param friendID The friend (user) ID the user wants to ignore
     * @param listener Handles what happens in case of success or failure of the change
     */
    public void ignoreFriendRequest(final DatabaseProvider db, final String friendID, final AuthProvider.AuthListener listener) {
        if(friendRequests.remove(friendID)) {
            // modify user
            db.modifyUser(this, new DatabaseProvider.ModifyUserListener() {
                @Override
                public void onSuccess() {
                    listener.onSuccess();
                }

                @Override
                public void onDoesntExist() {
                    listener.onFailure();
                }

                @Override
                public void onFailure() {
                    friendRequests.add(friendID);
                    listener.onFailure();
                }
            });
        } else {
            // --> trying to remove a request that is not in the requestList
            listener.onFailure();
        }
    }

    /**
     * Send a friend request to the friend (user) that the user wants to add as a friend
     *
     * @param db database containing the user data
     * @param friendID The friend (user) that the user wants to add
     * @param listener Handles what happens in case of success or failure of the change
     */
    public void sendFriendRequest(final DatabaseProvider db, final String friendID, final userListener listener) {
        if(!friendlist.contains(friendID)) {
            db.getUserById(friendID, new DatabaseProvider.GetUserListener() {
                @Override
                public void onSuccess(User user) {
                    // add the request in the friend's requests list
                    addTofriendRequests(db, user, listener);
                }

                @Override
                public void onDoesntExist() {
                    listener.onDoesntExist();
                }

                @Override
                public void onFailure() {
                    listener.onFailure();
                }
            });
        } else {
            // friend already in friendlist
            listener.onFriendlistException();
        }
    }

    /**
     * Adds a friend request to the friendRequests
     *
     * @param userID The ID of the sender
     * @return The user with the friend added in his requestList
     */
    private User addRequest(final String userID) {
        friendRequests.add(userID);
        return this;
    }

    /**
     *
     * @param userID The ID of the sender
     * @return The user with the friend removed from his requestList
     */
    private User removeRequest(final String userID) {
        friendRequests.remove(userID);
        return this;
    }

    /**
     * Adds a friend to the friendlist
     *
     * @param userID The ID of the friend we want to add
     * @return The user with the the friend added in his friendlist
     */
    private User addFriend(final String userID) {
        friendlist.add(userID);
        return this;
    }

    /**
     * Remove a friend from the friendlist
     *
     * @param userID The ID of the friend we want to add
     * @return The user with the friend removed from the friendlist
     */
    private User removeFriend(final String userID) {
        friendlist.remove(userID);
        return this;
    }

    // User can be final ?
    /**
     * Adds a friend request in the friend requests list of a user
     *
     * @param db database containing the user data
     * @param user The user we want to add the request to
     * @param listener Handles what happens in case of success or failure of the change
     */
    private void addTofriendRequests(final DatabaseProvider db, User user, final userListener listener) {
        db.modifyUser(user.addRequest(id), new DatabaseProvider.ModifyUserListener() {
            @Override
            public void onSuccess() {
                listener.onSuccess();
            }

            @Override
            public void onDoesntExist() {
                listener.onDoesntExist();
            }

            @Override
            public void onFailure() {
                listener.onFailure();
            }
        });
    }

    /**
     * Helper function for removeFromfriendlist removing friend from user's friendlist
     *
     * @param db database containing the user data
     * @param instance Instance of the user we want to remove the friend
     * @param friendID The friend we want to remove
     * @param listener Handles what happens in case of success or failure of the change
     */
    private void removeFriendFromUserHelper(final DatabaseProvider db, User instance, final String friendID, final userListener listener) {
        // modify user
        db.modifyUser(instance.removeFriend(friendID), new DatabaseProvider.ModifyUserListener() {
            @Override
            public void onSuccess() {
                listener.onSuccess();
            }

            @Override
            public void onDoesntExist() {
                // --> (should not happen)
                // User performing removal is not in the database anymore
                listener.onFailure();
            }

            @Override
            public void onFailure() {
                // --> user is not in friend list anymore, but has friend in his list
                // Needs to perform the action again
                listener.onFailure();
            }
        });
    }

    /**
     * Removes given friendID from the friend list
     *
     * @param db database containing the user data
     * @param friendID user (friend) ID that the user wants to remove
     * @param listener Handles what happens in case of success or failure of the change
     */
    public void removeFromFriendlist(final DatabaseProvider db, final String friendID, final userListener listener) {
        if(friendlist.remove(friendID)) {
            // get friend user
            db.getUserById(friendID, new DatabaseProvider.GetUserListener() {
                @Override
                public void onSuccess(User user) {
                    // modify friend, remove user from friend list
                    db.modifyUser(user.removeFriend(id), new DatabaseProvider.ModifyUserListener() {
                        @Override
                        public void onSuccess() {
                            // modify user
                            removeFriendFromUserHelper(db, User.this, friendID, listener);
                        }

                        @Override
                        public void onDoesntExist() {
                            // --> no modifications needed on friend, just on user
                            // modify user
                            removeFriendFromUserHelper(db, User.this, friendID, listener);
                        }

                        @Override
                        public void onFailure() {
                            // --> error, re-add friend locally
                            friendlist.add(friendID);
                            listener.onFailure();
                        }
                    });
                }

                @Override
                public void onDoesntExist() {
                    // --> no modifications needed on friend, just on user
                    // modify user
                    removeFriendFromUserHelper(db, User.this, friendID, listener);
                }

                @Override
                public void onFailure() {
                    // --> error, re-add friend locally
                    friendlist.add(friendID);
                    listener.onFailure();
                }
            });
        } else {
            listener.onFriendlistException();
        }
    }

    /**
     * Adds new point of interest to this user's visited list
     *
     * @param db database containing the user data
     * @param poiID POI ID that the user wishes to visit
     * @param listener Handles what happens in case of success or failure of the change
     */
    public void visit(final DatabaseProvider db,final String poiID, final AuthProvider.AuthListener listener) {
        if(!visited.contains(poiID)) {
            visited.addFirst(poiID);
            db.modifyUser(this, new DatabaseProvider.ModifyUserListener() {
                @Override
                public void onSuccess() {
                    listener.onSuccess();
                }

                @Override
                public void onDoesntExist() {
                    visited.remove(poiID);
                    listener.onFailure();
                }

                @Override
                public void onFailure() {
                    visited.remove(poiID);
                    listener.onFailure();
                }
            });
        } else {
            listener.onFailure();
        }
    }

    /**
     * Removes given point of interest of this user's visited list
     *
     * @param db database containing the user data
     * @param poiID POI ID that the user wishes to remove from visited list
     * @param listener Handles what happens in case of success or failure of the change
     */
    public void removeFromVisited(final DatabaseProvider db,final String poiID, final AuthProvider.AuthListener listener) {
        if(visited.contains(poiID)) {
            //we keep the position in a variable if we fail to modify in database
            // and we need to restore the visited list state
            final int poiIndex = visited.indexOf(poiID);
            visited.remove(poiID);
            db.modifyUser(this, new DatabaseProvider.ModifyUserListener() {
                @Override
                public void onSuccess() {
                    listener.onSuccess();
                }

                @Override
                public void onDoesntExist() {
                    visited.add(poiIndex, poiID);
                    listener.onFailure();
                }

                @Override
                public void onFailure() {
                    visited.add(poiIndex, poiID);
                    listener.onFailure();
                }
            });
        } else {
            listener.onFailure();
        }
    }
    /**
     * Adds new point of interest to this user's wishlist
     *
     * @param db database containing the user data
     * @param poiID POI ID that the user wishes to visit
     * @param listener Handles what happens in case of success or failure of the change
     */
    public void addToWishlist(final DatabaseProvider db,final String poiID, final AuthProvider.AuthListener listener) {
        if (wishlist.add(poiID)) {
            db.modifyUser(this, new DatabaseProvider.ModifyUserListener() {
                @Override
                public void onSuccess() {
                    listener.onSuccess();
                }

                @Override
                public void onDoesntExist() {
                    wishlist.remove(poiID);
                    listener.onFailure();
                }

                @Override
                public void onFailure() {
                    wishlist.remove(poiID);
                    listener.onFailure();
                }
            });
        } else {
            listener.onFailure();
        }
    }
    /**
     * Adds new favorite point of interest to this user's favorite list
     *
     * @param db database containing the user data
     * @param favID POI ID
     * @param listener Handles what happens in case of success or failure of the change
     */
    public void addFavourite(final DatabaseProvider db,final String favID, final AuthProvider.AuthListener listener) {
        if (favourites.add(favID)) {
            db.modifyUser(this, new DatabaseProvider.ModifyUserListener() {
                @Override
                public void onSuccess() {
                    listener.onSuccess();
                }

                @Override
                public void onDoesntExist() {
                    favourites.remove(favID);
                    listener.onFailure();
                }

                @Override
                public void onFailure() {
                    favourites.remove(favID);
                    listener.onFailure();
                }
            });
        } else {
            listener.onFailure();
        }
    }

    /**
     * Removes given point of interest of this user wishlist
     *
     * @param db database containing the user data
     * @param poiID POI ID that user no longer wishes to visit
     * @param listener Handles what happens in case of success or failure of the change
     */
    public void removeFromWishlist(final DatabaseProvider db, final String poiID, final AuthProvider.AuthListener listener) {
        if (wishlist.remove(poiID)) {
            db.modifyUser(this, new DatabaseProvider.ModifyUserListener() {
                @Override
                public void onSuccess() {
                    listener.onSuccess();
                }

                @Override
                public void onDoesntExist() {
                    wishlist.add(poiID);
                    listener.onFailure();
                }

                @Override
                public void onFailure() {
                    wishlist.add(poiID);
                    listener.onFailure();
                }
            });
        } else {
            listener.onFailure();
        }
    }

    /**
     * Removes given point of interest of this user favorite list
     *
     * @param db database containing the user data
     * @param favID POI ID
     * @param listener Handles what happens in case of success or failure of the change
     */
    public void removeFavourite(final DatabaseProvider db, final String favID, final AuthProvider.AuthListener listener) {
        if (favourites.remove(favID)) {
            db.modifyUser(this, new DatabaseProvider.ModifyUserListener() {
                @Override
                public void onSuccess() {
                    listener.onSuccess();
                }

                @Override
                public void onDoesntExist() {
                    favourites.add(favID);
                    listener.onFailure();
                }

                @Override
                public void onFailure() {
                    favourites.add(favID);
                    listener.onFailure();
                }
            });
        } else {
            listener.onFailure();
        }
    }

    /**
     * Changes this user's username
     *
     * @param db database containing the user data
     * @param newName  New username value
     * @param listener Handles what happens in case of success or failure of the change
     */
    public void changeName(DatabaseProvider db, final String newName, final AuthProvider.AuthListener listener) {
        //verification is done in the activity
        db.modifyUser(this, new DatabaseProvider.ModifyUserListener() {
            @Override
            public void onSuccess() {
                name = newName;
                listener.onSuccess();
            }

            @Override
            public void onDoesntExist() {
                listener.onFailure();
            }

            @Override
            public void onFailure() {
                listener.onFailure();
            }
        });

    }


    @Override
    public boolean equals(Object other) {
        if (other == null || !User.class.isAssignableFrom(other.getClass())) {
            return false;
        }

        User otherUser = (User) other;

        return this.name.equals(otherUser.name)
                && this.id.equals(otherUser.id);
    }

    /**
     * Interface userListener to handle friendExceptions
     */
    public interface userListener {
        /**
         * what to do if action succeed
         */
        void onSuccess();

        /**
         * What to do if there is a problem with the friendlist
         */
        void onFriendlistException();

        /**
         * what to do if no matching user id is found
         */
        void onDoesntExist();

        /**
         * what to do if the action fails
         */
        void onFailure();
    }

    /**
     * @return the user display name
     */
    public String getName() {
        return name;
    }

    /**
     * @return user ID
     */
    public String getID() {
        return id;
    }

    /**
     * @return true if user profile is public, else false
     */
    public Boolean isPublicProfile() {
        return isPublicProfile;
    }

    /**
     * @return the user's favourite POI's IDs as a set (favourites)
     */
    public Set<String> getFavourites() {
        return Collections.unmodifiableSet(new TreeSet<>(favourites));
    }

    /**
     * @return the user's wished POI's IDs as a set (wishlist)
     */
    public Set<String> getWishlist() {
        return Collections.unmodifiableSet(new TreeSet<>(wishlist));
    }

    /**
     * @return the user's friends IDs as a set (friendlist)
     */
    public Set<String> getFriendlist() {
        return Collections.unmodifiableSet(new TreeSet<>(friendlist));
    }

    /**
     * @return the user's requests list
     */
    public Set<String> getRequests() {
        return Collections.unmodifiableSet(new TreeSet<>(friendRequests));
    }

    /**
     * @return list of POIs visited by the user
     */
    public List<String> getVisited() {
        return Collections.unmodifiableList(new LinkedList<>(visited));
    }
}
