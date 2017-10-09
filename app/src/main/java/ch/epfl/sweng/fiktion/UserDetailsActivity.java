package ch.epfl.sweng.fiktion;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
    private Button verification;

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

        //initialise button
        verification = (Button)findViewById(R.id.verification_button);


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
            // FirebaseUser.getToken() instead. [I will keep this advice for now]
            String uid = user.getUid();

            //initialise textViews
            user_name_view.setText(name);
            user_email_view.setText(email + "(verified : " + user.isEmailVerified() + ")");
            user_id_view.setText(uid);

            //show verification button only if not verified
            if(user.isEmailVerified()){
                verification.setVisibility(View.GONE);
            } else {
                verification.setVisibility(View.VISIBLE);
            }
        } else {
            Log.d(TAG, "Could not initialise user details, user is not signed in");
        }

    }

    /**
     * This method signs the user out from Fiktion
     */
    private void signOut() {
        mAuth.signOut();
        Log.d(TAG,"User is signed out");
        Intent signInIntent = new Intent(this, SignInActivity.class);
        startActivity(signInIntent);
    }

    /**
     * This method will send a verification email to the currently signed in user
     */
    private void sendEmailVerification() {
        // Disable button
        findViewById(R.id.verification_button).setEnabled(false);

        // Send verification email
        // [START send_email_verification]
        final FirebaseUser user = mAuth.getCurrentUser();
        if(user!=null) {
            user.sendEmailVerification()
                    .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            // [START_EXCLUDE]
                            // Re-enable button
                            Log.d(TAG, "Sending was successful");
                            findViewById(R.id.verification_button).setEnabled(true);

                            if (task.isSuccessful()) {
                                Toast.makeText(UserDetailsActivity.this,
                                        "Verification email sent to " + user.getEmail(),
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Log.e(TAG, "sendEmailVerification", task.getException());
                                Toast.makeText(UserDetailsActivity.this,
                                        "Failed to send verification email.",
                                        Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
        } else{
            //handles the case if user is not currently signed right after calling this method
            Toast.makeText(UserDetailsActivity.this,"No User currently signed in",Toast.LENGTH_SHORT).show();
            findViewById(R.id.verification_button).setEnabled(true);
        }

    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if(i == R.id.detail_signout) {
            signOut();
        } else if(i==R.id.verification_button){
            Log.d(TAG,"Sending Email Verification");
            sendEmailVerification();
        }
    }
}
