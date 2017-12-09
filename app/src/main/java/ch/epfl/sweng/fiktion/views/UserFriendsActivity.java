package ch.epfl.sweng.fiktion.views;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import ch.epfl.sweng.fiktion.R;
import ch.epfl.sweng.fiktion.controllers.UserController;
import ch.epfl.sweng.fiktion.models.User;
import ch.epfl.sweng.fiktion.providers.DatabaseProvider;
import ch.epfl.sweng.fiktion.views.utils.UserDisplayer;

public class UserFriendsActivity extends AppCompatActivity {

    // views
    private TextView friendsRequestsTitle;
    private LinearLayout friendsRequests;
    private TextView friendsListTitle;
    private LinearLayout friendsList;
    private TextView friendsListEmpty;
    private User user;
    private UserController userCtrl;

    // flags data
    private String userId;
    private ProfileActivity.Action state;

    // activity's context
    private Context ctx = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_friends);

        // get data from caller activity
        Intent from = getIntent();
        // user ID
        userId = from.getStringExtra(ProfileActivity.USER_ID_KEY);
        // activity action
        String stateKey = from.getStringExtra(ProfileActivity.PROFILE_ACTION_KEY);
        if (stateKey.equals(ProfileActivity.PROFILE_ACTION_ME))
            state = ProfileActivity.Action.MY_PROFILE;
        else if (stateKey.equals(ProfileActivity.PROFILE_ACTION_ANOTHER))
            state = ProfileActivity.Action.ANOTHER_PROFILE;
        // if no extra data found, give up
        if (userId == null || stateKey == null) return;

        // find views
        friendsRequestsTitle = (TextView) findViewById(R.id.friends_requests_title);
        friendsRequests = (LinearLayout) findViewById(R.id.friends_requests);
        friendsListTitle = (TextView) findViewById(R.id.friends_list_title);
        friendsList = (LinearLayout) findViewById(R.id.friends_list);
        friendsListEmpty = (TextView) findViewById(R.id.friends_list_empty);

        // get the user we want to display the infos from
        DatabaseProvider.getInstance().getUserById(userId, new DatabaseProvider.GetUserListener() {
            @Override
            public void onSuccess(User u) {
                updateContent(u);
            }

            @Override
            public void onModified(User u) {
                updateContent(u);
            }

            @Override
            public void onDoesntExist() {
                Snackbar.make(friendsListTitle, R.string.user_not_found, Snackbar.LENGTH_INDEFINITE).show();
            }

            @Override
            public void onFailure() {
                Snackbar.make(friendsListTitle, R.string.failed_to_fetch_data, Snackbar.LENGTH_INDEFINITE).show();
            }
        });
    }


    private void updateContent(User u) {
        // assign user
        user = u;
        userCtrl = new UserController(user);
        // update lists : reset friends requests
        friendsRequestsTitle.setVisibility(View.GONE);
        friendsRequests.setVisibility(View.GONE);
        friendsRequests.removeAllViews();
        // reset friends list
        friendsList.removeAllViews();
        friendsList.addView(friendsListEmpty);
        friendsListEmpty.setVisibility(View.VISIBLE);

        // show requests only if my profile
        if (state == ProfileActivity.Action.MY_PROFILE) {
            for (final String r : u.getRequests()) {
                // show friend requests since there are some
                friendsRequestsTitle.setVisibility(View.VISIBLE);
                friendsRequests.setVisibility(View.VISIBLE);

                // find infos of that request's user
                DatabaseProvider.getInstance().getUserById(r, new DatabaseProvider.GetUserListener() {
                    @Override
                    public void onSuccess(User requester) {
                        View v = UserDisplayer.createUserListElement(requester, ctx);
                        v = UserDisplayer.withV((LinearLayout) v, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // accept friend request
                                userCtrl.acceptFriendRequest(r, new DatabaseProvider.ModifyUserListener() {
                                    @Override
                                    public void onSuccess() {
                                        Snackbar.make(friendsListTitle, R.string.friend_added, Snackbar.LENGTH_LONG).show();
                                    }

                                    @Override
                                    public void onDoesntExist() {
                                        Snackbar.make(friendsListTitle, R.string.user_not_found, Snackbar.LENGTH_LONG).show();
                                    }

                                    @Override
                                    public void onFailure() {
                                        Snackbar.make(friendsListTitle, R.string.request_failed, Snackbar.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }, ctx);
                        v = UserDisplayer.withX((LinearLayout) v, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // deny friend request
                                userCtrl.ignoreFriendRequest(r, new UserController.BinaryListener() {
                                    @Override
                                    public void onSuccess() {
                                        Snackbar.make(friendsListTitle, R.string.friend_request_ignored, Snackbar.LENGTH_LONG).show();
                                    }

                                    @Override
                                    public void onFailure() {
                                        Snackbar.make(friendsListTitle, R.string.request_failed, Snackbar.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }, ctx);
                        friendsRequests.addView(v);
                    }

                    @Override
                    public void onModified(User user) { /* nothing */ }

                    @Override
                    public void onDoesntExist() { /* nothing */ }

                    @Override
                    public void onFailure() { /* nothing */ }
                });
            }
        }

        for (final String f : u.getFriendlist()) {
            // hide empty message since there are some
            friendsListEmpty.setVisibility(View.GONE);

            // find infos of friend
            DatabaseProvider.getInstance().getUserById(f, new DatabaseProvider.GetUserListener() {
                @Override
                public void onSuccess(final User friend) {
                    View v = UserDisplayer.createUserListElement(friend, ctx);
                    // put delete button if my profile
                    if (state == ProfileActivity.Action.MY_PROFILE) {
                        v = UserDisplayer.withX((LinearLayout) v, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                                builder.setMessage(getString(R.string.deletion_confirmation_of, friend.getName()))
                                        .setCancelable(true)
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                userCtrl.removeFromFriendList(f, new UserController.BinaryListener() {
                                                    @Override
                                                    public void onSuccess() {
                                                        Snackbar.make(friendsListTitle, R.string.friend_deleted, Snackbar.LENGTH_LONG).show();
                                                    }

                                                    @Override
                                                    public void onFailure() {
                                                        Snackbar.make(friendsListTitle, R.string.request_failed, Snackbar.LENGTH_LONG).show();
                                                    }
                                                });
                                            }
                                        })
                                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                            }
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();
                            }
                        }, ctx);
                    }
                    friendsList.addView(v);
                }

                @Override
                public void onModified(User user) { /* nothing */ }

                @Override
                public void onDoesntExist() { /* nothing */ }

                @Override
                public void onFailure() { /* nothing */ }
            });
        }
    }
}
