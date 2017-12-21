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
    private final Set<String> favourites;
    private final Set<String> wishlist;
    private final LinkedList<String> visited;
    private final Set<String> friendlist;
    private final Set<String> friendRequests;
    private final Set<String> upvoted;
    private final Settings settings;
    private PersonalUserInfos userInfos;

    /**
     * Creates a new User with given parameters
     *
     * @param input_name Username
     * @param input_id   User id
     * @param favs       list of favourite POIs
     * @param visits     list of visited POIs
     * @param wishes     POIs wish list
     * @param friends    User friend list
     */
    public User(String input_name, String input_id, Set<String> favs,
                Set<String> wishes, Set<String> friends, Set<String> fRequests,
                LinkedList<String> visits, Boolean isPublic, Set<String> upVotes,
                Settings settings, PersonalUserInfos infos) {
        name = input_name;
        id = input_id;
        favourites = new TreeSet<>(favs);
        wishlist = new TreeSet<>(wishes);
        visited = new LinkedList<>(visits);
        friendlist = new TreeSet<>(friends);
        friendRequests = new TreeSet<>(fRequests);
        isPublicProfile = isPublic;
        upvoted = new TreeSet<>(upVotes);
        this.settings = new Settings(settings.getSearchRadius());
        userInfos = infos;
    }

    /**
     * Creates a new User with given parameters
     *
     * @param input_name Username
     * @param input_id   User id
     * @param favs       list of favourite POIs
     * @param wishes     POIs wish list
     * @param visits     list of visited POIs
     */
    public User(String input_name, String input_id, Set<String> favs, Set<String> wishes, LinkedList<String> visits) {
        name = input_name;
        id = input_id;
        favourites = new TreeSet<>(favs);
        wishlist = new TreeSet<>(wishes);
        visited = new LinkedList<>(visits);
        friendlist = new TreeSet<>();
        friendRequests = new TreeSet<>();
        isPublicProfile = true;
        upvoted = new TreeSet<>();
        settings = new Settings(Settings.DEFAULT_SEARCH_RADIUS);
        userInfos = new PersonalUserInfos();
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
        upvoted = new TreeSet<>();
        settings = new Settings(Settings.DEFAULT_SEARCH_RADIUS);
        userInfos = new PersonalUserInfos();
    }

    /**
     * Current user upvotes given position of interest
     *
     * @param poiID    id of the poi to be upvoted
     * @param listener handles what to do when trying to modify the user
     */
    public void upVote(final String poiID, final DatabaseProvider.ModifyUserListener listener) {
        if (upvoted.add(poiID)) {
            DatabaseProvider.getInstance().modifyUser(this, new DatabaseProvider.ModifyUserListener() {
                @Override
                public void onSuccess() {
                    listener.onSuccess();
                }

                @Override
                public void onDoesntExist() {
                    upvoted.remove(poiID);
                    listener.onDoesntExist();
                }

                @Override
                public void onFailure() {
                    upvoted.remove(poiID);
                    listener.onFailure();
                }
            });
        } else {
            listener.onFailure();
        }
    }

    /**
     * Current user removes his upvote in the given position of interest
     *
     * @param poiID    id of the poi
     * @param listener handles what to do when trying to modify the user
     */
    public void removeVote(final String poiID, final DatabaseProvider.ModifyUserListener listener) {
        if (upvoted.remove(poiID)) {
            DatabaseProvider.getInstance().modifyUser(this, new DatabaseProvider.ModifyUserListener() {
                @Override
                public void onSuccess() {
                    listener.onSuccess();
                }

                @Override
                public void onDoesntExist() {
                    upvoted.add(poiID);
                    listener.onDoesntExist();
                }

                @Override
                public void onFailure() {
                    upvoted.add(poiID);
                    listener.onFailure();
                }
            });
        }
    }

    /**
     * Changes the state of the user's profile privacy (true/false)
     *
     * @param privacyState The state of the privacy
     * @param listener     Handles what happens in case of success or failure of the change
     */
    public void changeProfilePrivacy(Boolean privacyState, final AuthProvider.AuthListener listener) {
        final Boolean oldPrivacy = isPublicProfile;
        isPublicProfile = privacyState;
        DatabaseProvider.getInstance().modifyUser(this, new DatabaseProvider.ModifyUserListener() {
            @Override
            public void onSuccess() {
                listener.onSuccess();
            }

            @Override
            public void onDoesntExist() {
                isPublicProfile = oldPrivacy;
                listener.onFailure();
            }

            @Override
            public void onFailure() {
                // revert local changes
                isPublicProfile = oldPrivacy;
                listener.onFailure();
            }
        });
    }

    /**
     * Adds locally a friend request to the friendRequests
     *
     * @param userID The ID of the sender
     */
    public void addRequest(final String userID) {
        friendRequests.add(userID);
    }

    /**
     * @param userID The ID of the sender
     * @return The user with userID in his requests
     */
    public User addRequestAndGet(final String userID) {
        friendRequests.add(userID);
        return this;
    }

    /**
     * Removes locally a friend request from the friendRequests
     *
     * @param userID The ID of the sender
     */
    public void removeRequest(final String userID) {
        if (friendRequests.contains(userID)) {
            friendRequests.remove(userID);
        }
    }

    /**
     * @param userID The ID of the sender
     * @return The user without userID in his requests
     */
    public User removeRequestAndGet(final String userID) {
        if (friendRequests.contains(userID)) {
            friendRequests.remove(userID);
        }
        return this;
    }

    /**
     * Adds locally a friend to the friendlist
     *
     * @param userID The ID of the friend we want to add
     */
    public void addFriend(final String userID) {
        friendlist.add(userID);
    }

    /**
     * @param userID The ID of the friend we want to add
     * @return The user with userID in his friendlist
     */
    public User addFriendAndGet(final String userID) {
        friendlist.add(userID);
        return this;
    }

    /**
     * Removes locally a friend from the friendlist
     *
     * @param userID The ID of the friend we want to remove
     */
    public void removeFriend(final String userID) {
        if (friendlist.contains(userID)) {
            friendlist.remove(userID);
        }
    }

    /**
     * @param userID The ID of the friend we want to remove
     * @return The user without userID in his friendlist
     */
    public User removeFriendAndGet(final String userID) {
        if (friendlist.contains(userID)) {
            friendlist.remove(userID);
        }
        return this;
    }

    /**
     * Adds new point of interest to this user's visited list
     *
     * @param poiID    POI ID that the user wishes to visit
     * @param listener Handles what happens in case of success or failure of the change
     */
    public void visit(final String poiID, final DatabaseProvider.ModifyUserListener listener) {
        if (!visited.contains(poiID)) {
            visited.addFirst(poiID);
            DatabaseProvider.getInstance().modifyUser(this, new DatabaseProvider.ModifyUserListener() {
                @Override
                public void onSuccess() {
                    listener.onSuccess();
                }

                @Override
                public void onDoesntExist() {
                    visited.remove(poiID);
                    listener.onDoesntExist();
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
     * @param poiID    POI ID that the user wishes to remove from visited list
     * @param listener Handles what happens in case of success or failure of the change
     */
    public void removeFromVisited(final String poiID, final DatabaseProvider.ModifyUserListener listener) {
        if (visited.contains(poiID)) {
            //we keep the position in a variable if we fail to modify in database
            // and we need to restore the visited list state
            final int poiIndex = visited.indexOf(poiID);
            visited.remove(poiID);
            DatabaseProvider.getInstance().modifyUser(this, new DatabaseProvider.ModifyUserListener() {
                @Override
                public void onSuccess() {
                    listener.onSuccess();
                }

                @Override
                public void onDoesntExist() {
                    visited.add(poiIndex, poiID);
                    listener.onDoesntExist();
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
     * @param poiID    POI ID that the user wishes to visit
     * @param listener Handles what happens in case of success or failure of the change
     */
    public void addToWishlist(final String poiID, final DatabaseProvider.ModifyUserListener listener) {
        if (wishlist.add(poiID)) {
            DatabaseProvider.getInstance().modifyUser(this, new DatabaseProvider.ModifyUserListener() {
                @Override
                public void onSuccess() {
                    listener.onSuccess();
                }

                @Override
                public void onDoesntExist() {
                    wishlist.remove(poiID);
                    listener.onDoesntExist();
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
     * @param favID    POI ID
     * @param listener Handles what happens in case of success or failure of the change
     */
    public void addFavourite(final String favID, final DatabaseProvider.ModifyUserListener listener) {
        if (favourites.add(favID)) {
            DatabaseProvider.getInstance().modifyUser(this, new DatabaseProvider.ModifyUserListener() {
                @Override
                public void onSuccess() {
                    listener.onSuccess();
                }

                @Override
                public void onDoesntExist() {
                    favourites.remove(favID);
                    listener.onDoesntExist();
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
     * @param poiID    POI ID that user no longer wishes to visit
     * @param listener Handles what happens in case of success or failure of the change
     */
    public void removeFromWishlist(final String poiID, final DatabaseProvider.ModifyUserListener listener) {
        if (wishlist.remove(poiID)) {
            DatabaseProvider.getInstance().modifyUser(this, new DatabaseProvider.ModifyUserListener() {
                @Override
                public void onSuccess() {
                    listener.onSuccess();
                }

                @Override
                public void onDoesntExist() {
                    wishlist.add(poiID);
                    listener.onDoesntExist();
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
     * @param listener Handles what happens in case of success or failure of the change
     */
    public void removeFavourite(final String favID, final DatabaseProvider.ModifyUserListener listener) {
        if (favourites.remove(favID)) {
            DatabaseProvider.getInstance().modifyUser(this, new DatabaseProvider.ModifyUserListener() {
                @Override
                public void onSuccess() {
                    listener.onSuccess();
                }

                @Override
                public void onDoesntExist() {
                    favourites.add(favID);
                    listener.onDoesntExist();
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
    public void changeName(final String newName, final DatabaseProvider.ModifyUserListener listener) {
        //verification is done in the activity
        final String oldName = name;
        name = newName;
        DatabaseProvider.getInstance().modifyUser(this, new DatabaseProvider.ModifyUserListener() {
            @Override
            public void onSuccess() {
                listener.onSuccess();
            }

            @Override
            public void onDoesntExist() {
                name = oldName;
                listener.onDoesntExist();
            }

            @Override
            public void onFailure() {
                name = oldName;
                listener.onFailure();
            }
        });

    }

    public void updatePersonalInfos(PersonalUserInfos newInfos, final DatabaseProvider.ModifyUserListener listener) {
        final PersonalUserInfos backup = userInfos;
        userInfos = new PersonalUserInfos(newInfos.getBirthday(), newInfos.getFirstName(),
                newInfos.getLastName(), newInfos.getCountry());

        DatabaseProvider.getInstance().modifyUser(this, new DatabaseProvider.ModifyUserListener() {
            @Override
            public void onDoesntExist() {
                userInfos = backup;
                listener.onDoesntExist();
            }

            @Override
            public void onFailure() {
                userInfos = backup;
                listener.onFailure();
            }

            @Override
            public void onSuccess() {
                listener.onSuccess();
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

    /**
     * @return the user's requests list
     */
    public Set<String> getUpvoted() {
        return Collections.unmodifiableSet(new TreeSet<>(upvoted));
    }

    public void updateSettingsRadius(int radius, final DatabaseProvider.ModifyUserListener listener) {
        settings.updateSearchRadius(radius);
        DatabaseProvider.getInstance().modifyUser(this, listener);
    }

    public Settings getSettings() {
        return settings;
    }

    public PersonalUserInfos getPersonalUserInfos() {
        return userInfos;
    }
}
