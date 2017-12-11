package ch.epfl.sweng.fiktion.controllers;

import ch.epfl.sweng.fiktion.listeners.DoesntExist;
import ch.epfl.sweng.fiktion.listeners.Failure;
import ch.epfl.sweng.fiktion.listeners.Success;
import ch.epfl.sweng.fiktion.models.User;
import ch.epfl.sweng.fiktion.providers.AuthProvider;
import ch.epfl.sweng.fiktion.providers.DatabaseProvider;

/**
 * This class defines the user's social interactions methods.
 *
 * @author Christoph
 */

public class UserController {

    private User localUser;

    public UserController(final ConstructStateListener listener) {
        AuthProvider.getInstance().getCurrentUser(new DatabaseProvider.GetUserListener() {
            @Override
            public void onNewValue(User user) {
                localUser = user;
                listener.onSuccess();
            }

            @Override
            public void onModifiedValue(User user) {
                localUser = user;
                listener.onModified();
            }

            @Override
            public void onDoesntExist() {
                localUser = null;
                listener.onFailure();
                throw new IllegalStateException("The local user does not exist");
            }

            @Override
            public void onFailure() {
                localUser = null;
                listener.onFailure();
                throw new IllegalStateException("The was an error fetching local user");
            }
        });
    }

    public UserController(User u) {
        this.localUser = u;
    }

    /**
     * @return the local user
     */
    public User getLocalUser() {
        return localUser;
    }

    /**
     * Sends a friend request from localUser to friendID
     *
     * @param friendID The ID of the user receiving the request
     * @param listener The listener handling every DB responses
     */
    public void sendFriendRequest(final String friendID, final RequestListener listener) {
        if (!localUser.getID().equals(friendID)) {
            if (localUser.getFriendlist().contains(friendID)) {
                listener.onAlreadyFriend();
            } else if (localUser.getRequests().contains(friendID)) {
                // set each other as friend and remove from requests
                DatabaseProvider.getInstance().getUserById(friendID, new DatabaseProvider.GetUserListener() {
                    @Override
                    public void onNewValue(final User user) {
                        DatabaseProvider.getInstance().modifyUser(user.addFriendAndGet(localUser.getID()), new DatabaseProvider.ModifyUserListener() {
                            @Override
                            public void onSuccess() {
                                DatabaseProvider.getInstance().modifyUser(localUser.addFriendAndGet(friendID).removeRequestAndGet(user.getID()), new DatabaseProvider.ModifyUserListener() {
                                    @Override
                                    public void onSuccess() {
                                        listener.onNewFriend();
                                    }

                                    @Override
                                    public void onDoesntExist() {
                                        localUser.removeFriend(friendID);
                                        listener.onFailure();
                                    }

                                    @Override
                                    public void onFailure() {
                                        localUser.removeFriend(friendID);
                                        listener.onFailure();
                                    }
                                });
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

                    @Override
                    public void onModifiedValue(User user) {
                        // do nothing
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
                // add user id in friend's requestList
                DatabaseProvider.getInstance().getUserById(friendID, new DatabaseProvider.GetUserListener() {
                    @Override
                    public void onNewValue(User user) {
                        // if the sender is in user friend list (caused by a previous database modification error)
                        if (user.getFriendlist().contains(localUser.getID())) {
                            // add friend to sender's friendlist
                            DatabaseProvider.getInstance().modifyUser(localUser.addFriendAndGet(friendID), new DatabaseProvider.ModifyUserListener() {
                                @Override
                                public void onSuccess() {
                                    listener.onNewFriend();
                                }

                                @Override
                                public void onDoesntExist() {
                                    localUser.removeFriend(friendID);
                                    listener.onFailure();
                                }

                                @Override
                                public void onFailure() {
                                    localUser.removeFriend(friendID);
                                    listener.onFailure();
                                }
                            });
                        } else {
                            DatabaseProvider.getInstance().modifyUser(user.addRequestAndGet(localUser.getID()), new DatabaseProvider.ModifyUserListener() {
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
                    }

                    @Override
                    public void onModifiedValue(User user) {
                        // do nothing
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
        }
    }

    /**
     * Accepts the friend request requestID received by localUser
     * (In case of onDoesntexist(), does not remove request, should ask user to ignore it)
     *
     * @param requestID The ID of the user that the local user wants to accept
     * @param listener  The listener handling every DB responses
     */
    public void acceptFriendRequest(final String requestID, final DatabaseProvider.ModifyUserListener listener) {
        if (localUser.getRequests().contains(requestID)) {
            DatabaseProvider.getInstance().getUserById(requestID, new DatabaseProvider.GetUserListener() {
                @Override
                public void onNewValue(User user) {
                    DatabaseProvider.getInstance().modifyUser(user.addFriendAndGet(localUser.getID()), new DatabaseProvider.ModifyUserListener() {
                        @Override
                        public void onSuccess() {
                            DatabaseProvider.getInstance().modifyUser(localUser.addFriendAndGet(requestID).removeRequestAndGet(requestID), new DatabaseProvider.ModifyUserListener() {
                                @Override
                                public void onSuccess() {
                                    listener.onSuccess();
                                }

                                @Override
                                public void onDoesntExist() {
                                    localUser.removeFriend(requestID);
                                    localUser.addRequest(requestID);
                                    listener.onFailure();
                                }

                                @Override
                                public void onFailure() {
                                    localUser.removeFriend(requestID);
                                    localUser.addRequest(requestID);
                                    listener.onFailure();
                                }
                            });
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

                @Override
                public void onModifiedValue(User user) {
                    // do nothing
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
    }

    /**
     * Ignores the friend request requestID received by localUser
     *
     * @param requestID The user ID that the local user wants to ignore
     * @param listener  The listener handling every DB responses
     */
    public void ignoreFriendRequest(final String requestID, final BinaryListener listener) {
        DatabaseProvider.getInstance().modifyUser(localUser.removeRequestAndGet(requestID), new DatabaseProvider.ModifyUserListener() {
            @Override
            public void onSuccess() {
                listener.onSuccess();
            }

            @Override
            public void onDoesntExist() {
                localUser.addRequest(requestID);
                listener.onFailure();
            }

            @Override
            public void onFailure() {
                localUser.addRequest(requestID);
                listener.onFailure();
            }
        });
    }

    /**
     * Removes localUser and user with id friendID from each other's friendlist
     * (In case of failure, it can happen that localUser is not in friend's friendlist but has friendID in his list,
     * the problem can be fixed by repeating the action)
     *
     * @param friendID The id of the friend that the local user wants to remove
     * @param listener The listener handling every DB responses
     */
    public void removeFromFriendList(final String friendID, final BinaryListener listener) {
        DatabaseProvider.getInstance().getUserById(friendID, new DatabaseProvider.GetUserListener() {
            @Override
            public void onNewValue(User user) {
                DatabaseProvider.getInstance().modifyUser(user.removeFriendAndGet(localUser.getID()), new DatabaseProvider.ModifyUserListener() {
                    @Override
                    public void onSuccess() {
                        removeFromLocalUserFriendList(friendID, listener);
                    }

                    @Override
                    public void onDoesntExist() {
                        removeFromLocalUserFriendList(friendID, listener);
                    }

                    @Override
                    public void onFailure() {
                        listener.onFailure();
                    }
                });
            }

            @Override
            public void onModifiedValue(User user) {
                // do nothing
            }

            @Override
            public void onDoesntExist() {
                removeFromLocalUserFriendList(friendID, listener);
            }

            @Override
            public void onFailure() {
                listener.onFailure();
            }
        });
    }

    /**
     * Removes user with id friendID from localUser's friendList
     * (helper method for removeFromFriendList)
     *
     * @param friendID The id of the friend that the local user wants to remove
     * @param listener The listener handling every DB responses
     */
    private void removeFromLocalUserFriendList(final String friendID, final BinaryListener listener) {
        DatabaseProvider.getInstance().modifyUser(localUser.removeFriendAndGet(friendID), new DatabaseProvider.ModifyUserListener() {
            @Override
            public void onSuccess() {
                listener.onSuccess();
            }

            @Override
            public void onDoesntExist() {
                localUser.addFriend(friendID);
                listener.onFailure();
            }

            @Override
            public void onFailure() {
                localUser.addFriend(friendID);
                listener.onFailure();
            }
        });
    }

    public interface BinaryListener extends Success, Failure {
    }

    public interface ConstructStateListener extends Success, Failure {

        /**
         * what to do on object update
         */
        void onModified();
    }

    public interface RequestListener extends Success, DoesntExist, Failure {

        /**
         * what to do if users are already friends
         */
        void onAlreadyFriend();

        /**
         * what to do if a new friend is added
         */
        void onNewFriend();
    }
}
