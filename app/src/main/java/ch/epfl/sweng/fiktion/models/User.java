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
 * @author Rodrigo
 */

public class User {
    private String name;
    //we could use same id as firebase id or create our own id system
    private final String id;
    private TreeSet<String> favourites;
    private TreeSet<String> wishlist;
    private LinkedList<String> visited;
    //private Set<String> rated;

    /**
     * Creates a new User with given paramaters
     *
     * @param input_name Username
     * @param input_id   User id
     */
    public User(String input_name, String input_id, TreeSet<String> favs, TreeSet<String> wishes, LinkedList<String> visits) {
        name = input_name;
        id = input_id;
        favourites = favs;
        wishlist = wishes;
        visited = visits;
    }

    /**
     * Adds new point of interest to this user's visited list
     * @param db database containing the user data
     * @param poiID POI ID that the user wishes to visit
     * @param listener Handles what happens in case of success or failure of the changement
     */
    public void visit(final DatabaseProvider db,final String poiID, final AuthProvider.AuthListener listener) {
        if(!visited.contains(poiID)) {
            visited.addFirst(poiID);
            db.modifyUser(new User(name, id, favourites, wishlist, visited), new DatabaseProvider.ModifyUserListener() {
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
        }else{
            listener.onFailure();
        }
    }

    /**
     * Removes given point of interest of this user's visited list
     * @param db database containing the user data
     * @param poiID POI ID that the user wishes to remove from visited list
     * @param listener Handles what happens in case of success or failure of the changement
     */
    public void removeFromVisited(final DatabaseProvider db,final String poiID, final AuthProvider.AuthListener listener) {
        if(visited.contains(poiID)) {
            //we keep the position in a variable if we fail to modify in database
            // and we need to restore the visited list state
            final int poiIndex = visited.indexOf(poiID);
            visited.remove(poiID);
            db.modifyUser(new User(name, id, favourites, wishlist, visited), new DatabaseProvider.ModifyUserListener() {
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
        }else{
            listener.onFailure();
        }
    }
    /**
     * Adds new point of interest to this user's wishlist
     *
     * @param poiID POI ID that the user wishes to visit
     * @param listener Handles what happens in case of success or failure of the changement
     */
    public void addToWishlist(final DatabaseProvider db,final String poiID, final AuthProvider.AuthListener listener) {
        if (wishlist.add(poiID)) {
            db.modifyUser(new User(name, id, favourites,wishlist, visited), new DatabaseProvider.ModifyUserListener() {
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
            db.modifyUser(new User(name, id, favourites, wishlist, visited), new DatabaseProvider.ModifyUserListener() {
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
            database.modifyUser(new User(name, id, favourites, wishlist, visited), new DatabaseProvider.ModifyUserListener() {
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
            database.modifyUser(new User(name, id, favourites, wishlist, visited), new DatabaseProvider.ModifyUserListener() {
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
        database.modifyUser(new User(newName, id, favourites, wishlist, visited), new DatabaseProvider.ModifyUserListener() {
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
     * @return the user's set with his favourite POI's IDs
     */
    public Set<String> getFavourites() {
        return Collections.unmodifiableSet(new TreeSet<>(favourites));
    }

    /**
     * @return the user's set with his wished POI's IDs
     */
    public Set<String> getWishlist() {
        return Collections.unmodifiableSet(new TreeSet<>(wishlist));
    }

    /**
     * @return list of POIs visited by the user
     */
    public List<String> getVisited() {
        return Collections.unmodifiableList(new LinkedList<>(visited));
    }

}
