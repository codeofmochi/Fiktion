package ch.epfl.sweng.bootcamp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener{


    private static final String TAG = "SignIn";
    private EditText UserEmail;
    private EditText UserPassword;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        //Initialise user content

        //Views
        UserEmail  = (EditText)findViewById(R.id.User_Email);
        UserPassword = (EditText)findViewById(R.id.User_Password);

        //Firebase Authenticator
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        // If User is signed in, UI will adapt, if User is null , UI will prompt a sign in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }
    private boolean validateCredentials(){
        boolean valid = false;
        String email = UserEmail.getText().toString();
        String password = UserPassword.getText().toString();

        if (password.length() >=6){
            valid = true;
            UserPassword.setError(null);
        } else{
            UserPassword.setError("Password must be of at least 6 characters");
        }
        if (email.contains("@")){
            valid = true;
            UserEmail.setError(null);
        } else{
            UserEmail.setError("Require a valid email");

        }

        return valid;
    }

    private void signIn(String email, String password) {
        if (!validateCredentials()) {
            return;
        }
        Log.d(TAG, "signIn:" + email);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            updateUI(null);
                        }

                    }
                });
    }


    private void updateUI(FirebaseUser user) {
        if (user != null) {
            //start details activity
        }
    }

    @Override
    public void onClick(View v){
        int i = v.getId();
        switch (i){
            case R.id.SignInButton :
                signIn(UserEmail.getText().toString(),UserPassword.getText().toString());
                break;
            case R.id.RegisterButton :
                break;
            default : break;
        }
    }


}
