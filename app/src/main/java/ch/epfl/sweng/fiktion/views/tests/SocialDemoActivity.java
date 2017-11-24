package ch.epfl.sweng.fiktion.views.tests;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

import ch.epfl.sweng.fiktion.R;
import ch.epfl.sweng.fiktion.models.User;
import ch.epfl.sweng.fiktion.providers.AuthProvider;
import ch.epfl.sweng.fiktion.providers.AuthSingleton;
import ch.epfl.sweng.fiktion.providers.DatabaseProvider;
import ch.epfl.sweng.fiktion.providers.DatabaseSingleton;
import ch.epfl.sweng.fiktion.views.HomeActivity;

public class SocialDemoActivity extends AppCompatActivity {

    // user
    private User user;

    // text views
    private TextView userName;
    private EditText userInput;

    // list adapters
    private ArrayAdapter<String> friendsAdapter;
    private ArrayAdapter<String> requestsAdapter;

    // lists
    private ListView friendsListView;
    private ListView requestsListView;

    // buttons
    private Button sendRequestButton;
    private Button removeFriendButton;
    private Button acceptRequestButton;
    private Button ignoreRequestButton;

    private final Context ctx = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_demo);

        // initialize views
        userName = (TextView) findViewById(R.id.display_user_name);
        userInput = (EditText) findViewById(R.id.user_input);
        sendRequestButton = (Button) findViewById(R.id.send_request_button);
        removeFriendButton = (Button) findViewById(R.id.remove_friend_button);
        acceptRequestButton = (Button) findViewById(R.id.accept_request_button);
        ignoreRequestButton = (Button) findViewById(R.id.ignore_request_button);

    }

    @Override
    public void onStart() {
        super.onStart();

        friendsListView = (ListView) findViewById(R.id.user_friends_list);
        requestsListView = (ListView) findViewById(R.id.user_requests_list);

        if (AuthSingleton.auth.isConnected()) {
            AuthSingleton.auth.getCurrentUser(DatabaseSingleton.database, new DatabaseProvider.GetUserListener() {
                @Override
                public void onSuccess(User currentUser) {
                    user = currentUser;
                    userName.setText(user.getName());

                    friendsAdapter = new ArrayAdapter<String>(ctx, android.R.layout.simple_list_item_1, new ArrayList<String>(user.getFriendlist()));
                    requestsAdapter = new ArrayAdapter<String>(ctx, android.R.layout.simple_list_item_1, new ArrayList<String>(user.getRequests()));

                }

                @Override
                public void onDoesntExist() {
                    user = null;
                    goHome();
                }

                @Override
                public void onFailure() {
                    user = null;
                    goHome();
                }
            });
        } else {
            goHome();
        }
    }

    private void goHome() {

        Intent i = new Intent(this, HomeActivity.class);
        this.startActivity(i);
        this.finish();
    }

    public void clickAddFriend(View v) {
        sendRequestButton.setEnabled(false);
        final String friendID = userInput.getText().toString();
        user.sendFriendRequest(DatabaseSingleton.database, friendID, new User.userListener() {
            @Override
            public void onSuccess() {
                sendRequestButton.setEnabled(true);
                Toast.makeText(ctx, "Friend request sent to " + friendID, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFriendlistException() {
                sendRequestButton.setEnabled(true);
                Toast.makeText(ctx, friendID + " is already your friend!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDoesntExist() {
                sendRequestButton.setEnabled(true);
                Toast.makeText(ctx, friendID + " does not exist", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure() {
                sendRequestButton.setEnabled(true);
                Toast.makeText(ctx, "Failed to send friend request", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void clickRemoveFriend(View v){
        removeFriendButton.setEnabled(false);
        final String friendID = userInput.getText().toString();
        user.removeFromFriendlist(DatabaseSingleton.database, friendID, new User.userListener() {
            @Override
            public void onSuccess() {
                removeFriendButton.setEnabled(true);
                Toast.makeText(ctx, friendID+" was successfully removed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFriendlistException() {
                removeFriendButton.setEnabled(true);
                Toast.makeText(ctx, friendID+" is not in your friend list", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDoesntExist() {
                removeFriendButton.setEnabled(true);
            }

            @Override
            public void onFailure() {
                removeFriendButton.setEnabled(true);
                Toast.makeText(ctx, "Failed to remove the friend", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void clickAcceptRequest(View v) {
        acceptRequestButton.setEnabled(false);
        final String friendID = userInput.getText().toString();
        user.acceptFriendRequest(DatabaseSingleton.database, friendID, new DatabaseProvider.ModifyUserListener() {
            @Override
            public void onSuccess() {
                acceptRequestButton.setEnabled(true);
                Toast.makeText(ctx, friendID+" is now your friend!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDoesntExist() {
                acceptRequestButton.setEnabled(true);
                Toast.makeText(ctx, "the user "+friendID+" does no longer exist", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure() {
                acceptRequestButton.setEnabled(true);
                Toast.makeText(ctx, "Failed to accept friend request", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void clickIgnoreRequest(View v) {
        ignoreRequestButton.setEnabled(false);
        final String friendID = userInput.getText().toString();
        user.ignoreFriendRequest(DatabaseSingleton.database, friendID, new AuthProvider.AuthListener() {
            @Override
            public void onSuccess() {
                ignoreRequestButton.setEnabled(true);
                Toast.makeText(ctx, "The friend request was ignored", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure() {
                ignoreRequestButton.setEnabled(true);
                Toast.makeText(ctx, "Failed to ignore friend request", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
