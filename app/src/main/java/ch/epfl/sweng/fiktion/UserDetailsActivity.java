package ch.epfl.sweng.fiktion;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class UserDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    //constants
        //LOGCAT
    private static final String TAG = "UserDetails";
        //UI modes
    private final int default_mode = 20;
    private final int changeName_mode = 21;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    //views
    private TextView user_name_view;
    private TextView user_email_view;
    private TextView user_verify_view;
    private EditText user_newName;
    //Buttons
    private Button verification;
    private Button choose;
    private Button confirmName;
    private Button pwReset;
    //Strings
    private String email;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        Log.d(TAG, "Initialising User Details activity");
        mAuth = FirebaseAuth.getInstance();

        //initialise views
        user_name_view = (TextView) findViewById(R.id.detail_user_name);
        user_email_view = (TextView) findViewById(R.id.detail_user_email);
        user_verify_view = (TextView) findViewById(R.id.detail_user_verify);
        user_newName = (EditText) findViewById(R.id.detail_new_name);

        //initialise button
        verification = (Button) findViewById(R.id.verification_button);
        choose = (Button) findViewById(R.id.detail_nickname_button);
        confirmName = (Button) findViewById(R.id.detail_confirm_name);
        pwReset = (Button)findViewById(R.id.detail_reset_password);
        //initialise User
        user = mAuth.getCurrentUser();
    }

    @Override
    public void onStart() {
        super.onStart();

        //initialise user details and firebase authentication

        if (user != null) {
            // Name, email address, and profile photo Url
            name = user.getDisplayName();
            email = user.getEmail();
            //Uri photoUrl = user.getPhotoUrl();
            //String uid = user.getUid();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getToken() instead. [I will keep this advice for now]
            updateUI(default_mode);
        } else {
            Log.d(TAG, "Could not initialise user details, user is not signed in");
        }

    }

    /**
     * This method signs the user out from Fiktion
     */
    private void signOut() {
        mAuth.signOut();
        Log.d(TAG, "User is signed out");
        onBackPressed();
    }

    /**
     * This method will send a verification email to the currently signed in user
     */
    private void sendEmailVerification() {
        // Disable button
        findViewById(R.id.verification_button).setEnabled(false);

        // Send verification email

        if (user != null) {
            user.sendEmailVerification()
                    .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            // Re-enable button
                            findViewById(R.id.verification_button).setEnabled(true);

                            if (task.isSuccessful()) {
                                Log.d(TAG, "Sending was successful");
                                Toast.makeText(UserDetailsActivity.this,
                                        "Verification email sent to " + email,
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Log.e(TAG, "sendEmailVerification failed", task.getException());
                                Toast.makeText(UserDetailsActivity.this,
                                        "Failed to send verification email.",
                                        Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
        } else {
            //handles the case if user is not currently signed right after calling this method
            Toast.makeText(UserDetailsActivity.this, "No User currently signed in", Toast.LENGTH_SHORT).show();
            findViewById(R.id.verification_button).setEnabled(true);
        }

    }

    private void sendPasswordResetEmail(){


        // Disable button
        findViewById(R.id.detail_reset_password).setEnabled(false);

        if(user!=null) {
            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            // Re-enable button
                            findViewById(R.id.detail_reset_password).setEnabled(true);

                            if (task.isSuccessful()) {
                                Log.d(TAG, "Sending was successful");
                                Toast.makeText(UserDetailsActivity.this,
                                        "Password reset email sent to " + email,
                                        Toast.LENGTH_SHORT).show();
                                pwReset.setVisibility(View.GONE);
                            } else {
                                Log.e(TAG, "sendPasswordResetEmail failed", task.getException());
                                Toast.makeText(UserDetailsActivity.this,
                                        "Failed to send password reset email.",
                                        Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
        } else {

            //handles the case if user is not currently signed right after calling this method
            Toast.makeText(UserDetailsActivity.this, "No User currently signed in", Toast.LENGTH_SHORT).show();
            // Re-enable button
            findViewById(R.id.detail_reset_password).setEnabled(true);

        }


    }

    private void updateUI(int i){
        if(i == changeName_mode){
            //activates this mode when user clicks on "choose" button
            choose.setVisibility(View.INVISIBLE);
            user_newName.setVisibility(View.VISIBLE);
            confirmName.setVisibility(View.VISIBLE);
        } else if(i==default_mode){
            //UI default mode
            //initialise views and buttons
            user_name_view.setText(name);
            user_email_view.setText(email);

            // show verification button only if not verified
            // show password reset button only if verified
            if (user.isEmailVerified()) {
                user_verify_view.setText("YES");
                verification.setVisibility(View.GONE);
                pwReset.setVisibility(View.VISIBLE);
            } else {
                user_verify_view.setText("NO");
                verification.setVisibility(View.VISIBLE);
                pwReset.setVisibility(View.GONE);
            }
            choose.setVisibility(View.VISIBLE);
            user_newName.setVisibility(View.INVISIBLE);
            confirmName.setVisibility(View.INVISIBLE);
        }
    }

    private void confirmName() {
        final String newName = user_newName.getText().toString();
        findViewById(R.id.detail_confirm_name).setEnabled(false);

        //validate name choice
        if (!newName.isEmpty()
                && !newName.equals(user.getDisplayName())
                && newName.length()<=15) {

            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(newName).build();
            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            findViewById(R.id.detail_confirm_name).setEnabled(true);
                            if (task.isSuccessful()) {
                                Log.d(TAG, "DisplayName was updated");
                                user_name_view.setText(newName);
                                recreate();
                                Toast.makeText(UserDetailsActivity.this,
                                        "User's name is now : " + newName,
                                        Toast.LENGTH_LONG).show();
                            } else {
                                Log.e(TAG, "DisplayName failed to update", task.getException());
                                Toast.makeText(UserDetailsActivity.this,
                                        "Failed to update User's name.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        } else{
            findViewById(R.id.detail_confirm_name).setEnabled(true);
            user_newName.setError("A new name is required");
        }

    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.detail_signout) {
            signOut();
        } else if (i == R.id.verification_button) {
            Log.d(TAG, "Sending Email Verification");
            sendEmailVerification();
        } else if (i == R.id.detail_nickname_button) {
            Log.d(TAG, "Setting up UI to change name");
            updateUI(changeName_mode);
        } else if (i == R.id.detail_confirm_name) {
            Log.d(TAG, "Changing name");
            confirmName();
        } else if(i == R.id.detail_reset_password){
            Log.d(TAG, "Sending password reset email");
            sendPasswordResetEmail();
        }
    }
}
