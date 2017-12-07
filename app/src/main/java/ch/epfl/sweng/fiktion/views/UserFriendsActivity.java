package ch.epfl.sweng.fiktion.views;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;
import android.widget.TextView;

import ch.epfl.sweng.fiktion.R;

public class UserFriendsActivity extends AppCompatActivity {

    private TextView friendsRequestsTitle;
    private LinearLayout friendsRequests;
    private TextView friendsListTitle;
    private LinearLayout friendsList;
    private TextView friendsListEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_friends);

        // find views
        friendsRequestsTitle = (TextView) findViewById(R.id.friends_requests_title);
        friendsRequests = (LinearLayout) findViewById(R.id.friends_requests);
        friendsListTitle = (TextView) findViewById(R.id.friends_list_title);
        friendsList = (LinearLayout) findViewById(R.id.friends_list);
        friendsListEmpty = (TextView) findViewById(R.id.friends_list_empty);
    }
}
