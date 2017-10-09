package ch.epfl.sweng.fiktion;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "RegisterActivity";

    //TextViews
    private EditText reg_email;
    private EditText reg_password;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"Initialising Register activity");
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart(){
        super.onStart();

        //initialise widgets
        reg_email = (EditText)findViewById(R.id.register_email);
        reg_password = (EditText)findViewById(R.id.register_password);

    }

    /**
     * This method checks if the credentials are valid by firebase standards
     * @return true is the credentials are valid, false otherwise
     */
    private boolean validateCredentials() {
        boolean validEmail = false;
        boolean validPassword = false;
        String email = reg_email.getText().toString();
        String password = reg_password.getText().toString();
        Log.d(TAG,"Validating credentials");

        if (password.isEmpty()) {
            reg_password.setError("Password is required");
            Log.d(TAG,"Password validation failed");
        } else
        {
            if (password.length() >= 6) {
                validPassword = true;
                reg_password.setError(null);
            } else {
                reg_password.setError("Password must be of at least 6 characters");
                Log.d(TAG,"Password validation failed");
            }
        }
        if (email.contains("@")) {
            validEmail = true;
            reg_email.setError(null);
        } else {
            reg_email.setError("Require a valid email");
            Log.d(TAG,"Email validation failed");

        }

        return validEmail && validPassword;
    }

    /**
     * This method creates a firebase user account using an input email and password
     * @param email provided by the user
     * @param password provided by the user
     */
    private void createAccount(String email, String password) {
        Log.d(TAG, "createAccount:" + email);
        if (!validateCredentials()) {
            return;
        }
        Log.d(TAG,"Credentials are valid");

        //firebase function that actually creates an account
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            Toast.makeText(RegisterActivity.this, "Registration Successful!",
                                    Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            onBackPressed();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG,"User clicked somewhere");
        int i = v.getId();
        if(i == R.id.register_click){
            createAccount(reg_email.getText().toString(),reg_password.getText().toString());
        }
    }
}
