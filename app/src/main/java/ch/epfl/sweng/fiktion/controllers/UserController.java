package ch.epfl.sweng.fiktion.controllers;

import ch.epfl.sweng.fiktion.models.User;
import ch.epfl.sweng.fiktion.providers.DatabaseProvider;

/**
 * This class defines the user's social interactions methods.
 *
 * @author Christoph
 */

public class UserController {

    /**
     * Sends a friend request from localUser to friendID
     *
     * @param localUser The local user
     * @param friendID The ID of the user receiving the request
     * @param listener The listener handling every DB responses
     */
    public void sendFriendResquest(final User localUser, final String friendID, final RequestListener listener) {
        if(localUser.getFriendlist().contains(friendID)) {
            listener.onAlreadyFriend();
        } else if(localUser.getRequests().contains(friendID)) {
            // set each other as friend and remove from requests
            DatabaseProvider.getInstance().getUserById(friendID, new DatabaseProvider.GetUserListener() {
                @Override
                public void onSuccess(final User user) {
                    DatabaseProvider.getInstance().modifyUser(user.addFriendAndGet(user.getID()), new DatabaseProvider.ModifyUserListener() {
                        @Override
                        public void onSuccess() {
                            DatabaseProvider.getInstance().modifyUser(localUser.addFriendAndGet(user.getID()), new DatabaseProvider.ModifyUserListener() {
                                @Override
                                public void onSuccess() {
                                    listener.onNewFriend();
                                }

                                @Override
                                public void onDoesntExist() {
                                    localUser.removeFriend(user.getID());
                                    listener.onFailure();
                                }

                                @Override
                                public void onFailure() {
                                    localUser.removeFriend(user.getID());
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
                public void onSuccess(final User user) {
                    // if the sender is in user friend list (caused by a previous database modification error)
                    if(user.getFriendlist().contains(localUser.getID())) {
                        // add friend to sender's friendlist
                        DatabaseProvider.getInstance().modifyUser(localUser.addFriendAndGet(user.getID()), new DatabaseProvider.ModifyUserListener() {
                            @Override
                            public void onSuccess() {
                                listener.onNewFriend();
                            }

                            @Override
                            public void onDoesntExist() {
                                localUser.removeFriend(user.getID());
                                listener.onFailure();
                            }

                            @Override
                            public void onFailure() {
                                localUser.removeFriend(user.getID());
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
     * Accepts the friend request requestID received by localUser
     * (In case of onDoesntexist(), does not remove request, should ask user to ignore it)
     *
     * @param localUser The local user
     * @param requestID The ID of the user that the local user wants to accept
     * @param listener The listener handling every DB responses
     */
    public void acceptFriendRequest(final User localUser, final String requestID, final DatabaseProvider.ModifyUserListener listener) {
        DatabaseProvider.getInstance().getUserById(requestID, new DatabaseProvider.GetUserListener() {
            @Override
            public void onSuccess(User user) {
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
     *
     * @param localUser
     * @param requestID
     * @param listener
     */
    public void ignoreFriendRequest(final User localUser, final String requestID, final RequestListener listener) {

    }

    /**
     *
     * @param localUser
     * @param friendID
     * @param listener
     */
    public void removeFromFriendList(final User localUser, final String friendID, final RequestListener listener) {

    }

    public interface RequestListener {
        /**
         * what to do if action succeed
         */
        void onSuccess();

        /**
         * what to do if no matching user id is found
         */
        void onDoesntExist();

        /**
         * what to do if the action fails
         */
        void onFailure();

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
