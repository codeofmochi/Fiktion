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

    /**
     * Creates a new User with given paramaters
     *
     * @param input_name Username
     * @param input_id   User id
     * @param favs list of favourite POIs
     * @param wishes POIs wish list
     * @param friends User friend list
     */
    public User(String input_name, String input_id, TreeSet<String> favs, TreeSet<String> wishes, TreeSet<String> friends) {
        name = input_name;
        id = input_id;
        favourites = favs;
        wishlist = wishes;
        friendlist = friends;
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
    }

    /**
     * Adds new point of interest to this user's wishlist
     *
     * @param poiID POI ID that the user wishes to visit
     * @param listener Handles what happens in case of success or failure of the changement
     */
    public void addToWishlist(final DatabaseProvider db,final String poiID, final AuthProvider.AuthListener listener) {
        if (wishlist.add(poiID)) {
            db.modifyUser(new User(name, id, favourites,wishlist), new DatabaseProvider.ModifyUserListener() {
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
     * @param listener Handles what happens in case of success or failure of the changement
     */
    public void addFavourite(final DatabaseProvider db,final String favID, final AuthProvider.AuthListener listener) {
        if (favourites.add(favID)) {
            db.modifyUser(new User(name, id, favourites, wishlist), new DatabaseProvider.ModifyUserListener() {
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
     * @param listener Handles what happens in case of success or failure of the changement
     */
    public void removeFromWishlist(final DatabaseProvider database, final String poiID, final AuthProvider.AuthListener listener) {
        if (wishlist.remove(poiID)) {
            database.modifyUser(new User(name, id, favourites, wishlist), new DatabaseProvider.ModifyUserListener() {
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
     * @param listener Handles what happens in case of success or failure of the changement
     */
    public void removeFavourite(final DatabaseProvider database, final String favID, final AuthProvider.AuthListener listener) {
        if (favourites.remove(favID)) {
            database.modifyUser(new User(name, id, favourites, wishlist), new DatabaseProvider.ModifyUserListener() {
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
     * @param listener Handles what happens in case of success or failure of the changement
     */
    public void changeName(DatabaseProvider database, final String newName, final AuthProvider.AuthListener listener) {
        //verification is done in the activity
        database.modifyUser(new User(newName, id, favourites, wishlist), new DatabaseProvider.ModifyUserListener() {
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
}
