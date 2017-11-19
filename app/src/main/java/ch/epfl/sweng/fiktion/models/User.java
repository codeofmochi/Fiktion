package ch.epfl.sweng.fiktion.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import ch.epfl.sweng.fiktion.providers.AuthProvider;
import ch.epfl.sweng.fiktion.providers.DatabaseProvider;

/**
 * This class represents the User in the application
 *
 * @author Rodrigo
 */

public class User {
    private String name;
    //we could use same id as firebase id or create our own id system
    private final String id;
    private TreeSet<String> favourites;
    private TreeSet<String> wishlist;
    //private Set<String> rated;
    private TreeSet<String> friendlist;
    private TreeSet<String> friendRequests;

    /**
     * Creates a new User with given paramaters
     *
     * @param input_name Username
     * @param input_id   User id
     * @param favs list of favourite POIs
     * @param wishes POIs wish list
     * @param friends User friend list
     */
    public User(String input_name, String input_id, TreeSet<String> favs, TreeSet<String> wishes, TreeSet<String> friends, TreeSet<String> fRequests) {
        name = input_name;
        id = input_id;
        favourites = favs;
        wishlist = wishes;
        friendlist = friends;
        friendRequests = fRequests;
    }

    /**
     * Creates a new User with given paramaters
     *
     * @param input_name Username
     * @param input_id   User id
     * @param favs list of favourite POIs
     * @param wishes POIs wish list
     */
    public User(String input_name, String input_id, TreeSet<String> favs, TreeSet<String> wishes) {
        name = input_name;
        id = input_id;
        favourites = favs;
        wishlist = wishes;
        friendlist = new TreeSet<>();
        friendRequests = new TreeSet<>();
    }

    /**
     * Creates a new User with given paramaters
     *
     * @param input_name Username
     * @param input_id   User id
     */
    public User(String input_name, String input_id) {
        name = input_name;
        id = input_id;
        favourites = new TreeSet<>();
        wishlist = new TreeSet<>();
        friendlist = new TreeSet<>();
        friendRequests = new TreeSet<>();
    }

    /**
     * Accept a friend request by adding it to the friend list if it is in the requests
     *
     * @param friendID the friend (user) that the user want to add to his friend list
     * @param listener Handles what happens in case of success or failure of the change
     */
    public void acceptFriendRequest(final DatabaseProvider db, final String friendID, final DatabaseProvider.ModifyUserListener listener) {
        if(friendRequests.contains(friendID)) {
            // remove friend invitation
            friendRequests.remove(friendID);
            // Access other user (friend)
            db.getUserById(friendID, new DatabaseProvider.GetUserListener() {
                @Override
                public void onSuccess(User user) {
                    // add friend in friend list
                    addToFriendlist(db, friendID, listener);
                    // add it self in friend's friend list
                    user.addToFriendlist(db, id, listener);
                }

                @Override
                public void onDoesntExist() {
                    // remove friend invitation
                    friendRequests.remove(friendID);
                    listener.onDoesntExist();
                }

                @Override
                public void onFailure() {
                    listener.onFailure();
                }
            });
        } else {
            listener.onFailure();
        }
    }

    /**
     * Ignore friend request by removing it from the request list
     *
     * @param friendID The friend (user) ID the user wants to ignore
     */
    public void ignoreFriendRequest(final String friendID) {
        friendRequests.remove(friendID);
    }

    /**
     * Send a friend request to the friend (user) that the user wants to add as a friend
     *
     * @param friendID The friend (user) that the user wants to add
     * @param listener Handles what happens in case of success or failure of the change
     */
    public void sendFriendRequest(final DatabaseProvider db, final String friendID, final DatabaseProvider.ModifyUserListener listener) {
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
            listener.onFailure();
        }
    }

    /**
     * Adds a friend request to the friendRequests
     *
     * @param userID The ID of the sender
     */
    private User addRequest(final String userID) {
        friendRequests.add(userID);
        return this;
    }

    // User can be final ?
    /**
     * Adds a friend request in the friend requests list of a user
     *
     * @param user The user we want to add the request to
     * @param listener Handles what happens in case of success or failure of the change
     */
    private void addTofriendRequests(final DatabaseProvider db, User user, final DatabaseProvider.ModifyUserListener listener) {
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
     * Add a new friend to the user's friend list
     *
     * @param friendID user (friend) ID that the user wants to add
     * @param listener Handles what happens in case of success or failure of the change
     */
    private void addToFriendlist(final DatabaseProvider db, final String friendID, final DatabaseProvider.ModifyUserListener listener) {
        if(friendlist.add(friendID)) {
            db.modifyUser(this, new DatabaseProvider.ModifyUserListener() {
                @Override
                public void onSuccess() {
                    listener.onSuccess();
                }

                @Override
                public void onDoesntExist() {
                    friendlist.remove(friendID);
                    listener.onDoesntExist();
                }

                @Override
                public void onFailure() {
                    friendlist.remove(friendID);
                    listener.onFailure();
                }
            });
        } else {
            listener.onFailure();
        }
    }

    /**
     * Removes given friendID from the friend list
     *
     * @param friendID user (friend) ID that the user wants to remove
     * @param listener Handles what happens in case of success or failure of the change
     */
    public void removeFromFriendlist(final DatabaseProvider db, final String friendID, final DatabaseProvider.ModifyUserListener listener) {
        if(friendlist.remove(friendID)) {
            db.modifyUser(this, new DatabaseProvider.ModifyUserListener() {
                @Override
                public void onSuccess() {
                    listener.onSuccess();
                }

                @Override
                public void onDoesntExist() {
                    friendlist.add(friendID);
                    listener.onDoesntExist();
                }

                @Override
                public void onFailure() {
                    friendlist.add(friendID);
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
     * @param poiID POI ID that user no longer wishes to visit
     * @param listener Handles what happens in case of success or failure of the change
     */
    public void removeFromWishlist(final DatabaseProvider database, final String poiID, final AuthProvider.AuthListener listener) {
        if (wishlist.remove(poiID)) {
            database.modifyUser(this, new DatabaseProvider.ModifyUserListener() {
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
     * @param favID POI ID
     * @param listener Handles what happens in case of success or failure of the change
     */
    public void removeFavourite(final DatabaseProvider database, final String favID, final AuthProvider.AuthListener listener) {
        if (favourites.remove(favID)) {
            database.modifyUser(this, new DatabaseProvider.ModifyUserListener() {
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
     * @param newName  New username value
     * @param listener Handles what happens in case of success or failure of the change
     */
    public void changeName(DatabaseProvider database, final String newName, final AuthProvider.AuthListener listener) {
        //verification is done in the activity
        database.modifyUser(this, new DatabaseProvider.ModifyUserListener() {
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
     * @return the user's favorite POI's IDs as a set (favourites)
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
}
