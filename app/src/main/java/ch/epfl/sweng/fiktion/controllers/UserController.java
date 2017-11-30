package ch.epfl.sweng.fiktion.controllers;

import ch.epfl.sweng.fiktion.models.User;
import ch.epfl.sweng.fiktion.providers.DatabaseProvider;

/**
 * This class defines the user's social interactions methods.
 *
 * @author Christoph
 */

public class UserController {

    public void sendFriendResquest(final User localUser, final String friendId, final requestListener listener) {
        if(localUser.getFriendlist().contains(friendId)) {
            listener.onAlreadyFriend();
        } else if(localUser.getRequests().contains(friendId)) {
            // set each other as friend and remove from requests
            DatabaseProvider.getInstance().getUserById(friendId, new DatabaseProvider.GetUserListener() {
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
            DatabaseProvider.getInstance().getUserById(friendId, new DatabaseProvider.GetUserListener() {
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

    public void acceptFriendRequest(User localUser, String requestId) {

    }

    public void ignoreFriendRequest(User localUser, String requestId) {

    }

    public void removeFromFriendList(User localUser, String friendId) {

    }

    public interface requestListener {
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
