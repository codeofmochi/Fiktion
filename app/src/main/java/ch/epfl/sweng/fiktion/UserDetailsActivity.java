package ch.epfl.sweng.fiktion;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

public class UserDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "UserDetails";
    private FirebaseAuth mAuth;
    //views
    private TextView user_name_view;
    private TextView user_email_view;
    private TextView user_id_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        Log.d(TAG, "Initialising User Details activity");
        mAuth = FirebaseAuth.getInstance();

        //initialise textViews
        user_name_view = (TextView) findViewById(R.id.detail_user_name);
        user_email_view = (TextView) findViewById(R.id.detail_user_email);
        user_id_view = (TextView) findViewById(R.id.detail_user_id);


    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            String name = user.getDisplayName();
            String email = user.getEmail();
            //Uri photoUrl = user.getPhotoUrl();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getToken() instead.
            String uid = user.getUid();

            //initialise textViews

            user_name_view.setText(name);
            TextView user_email_view = (TextView) findViewById(R.id.detail_user_email);
            user_email_view.setText(email);
            TextView user_id_view = (TextView) findViewById(R.id.detail_user_id);
            user_id_view.setText(uid);
        } else {
            Log.d(TAG, "Could not initialise user details, user is not signed in");
        }

    }

    private void signOut() {
        mAuth.signOut();
        Intent signInIntent = new Intent(this,SignInActivity.class);
        startActivity(signInIntent);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        switch(i) {
            case R.id.detail_signout:
                signOut();
                break;
            default: break;
        }
    }
}
