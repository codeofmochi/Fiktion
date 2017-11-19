package ch.epfl.sweng.fiktion.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import ch.epfl.sweng.fiktion.R;
import ch.epfl.sweng.fiktion.providers.AuthProvider;
import ch.epfl.sweng.fiktion.providers.AuthSingleton;
import ch.epfl.sweng.fiktion.providers.DatabaseSingleton;

/**
 * This activity enables the user to create a new account using its email and password
 * @author Rodrigo
 */
public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    //TextViews
    private EditText regEmail;
    private EditText regPassword;
    private EditText regConfirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "Initialising Register activity");
        setContentView(R.layout.activity_register);
    }

    @Override
    public void onStart() {
        super.onStart();

        //initialise widgets
        regEmail = (EditText) findViewById(R.id.register_email);
        regPassword = (EditText) findViewById(R.id.register_password);
        regConfirmPassword = (EditText) findViewById(R.id.register_confirm_password);

    }

    /**
     * This method creates a firebase user account using an input email and password
     *
     * @param email    provided by the user
     * @param password provided by the user
     */
    private void createAccount(String email, String password) {
        Log.d(TAG, "createAccount:" + email);

        //we need to check if the credentials are valid before attempting to sign in
        //first we check if the email is valid, do not proceed if it is not valid
        String emailErr = AuthSingleton.auth.validateEmail(email);
        if (!emailErr.isEmpty()) {
            Log.d(TAG, "Email is not valid");
            //we set an error corresponding to the failure
            regEmail.setError(emailErr);
            return;
        }
        //after making sure the email is valid we check if the password is valid and if not we do not proceed
        String passwordErr = AuthSingleton.auth.validatePassword(password);
        if (!passwordErr.isEmpty()) {
            Log.d(TAG, "Password is not valid");
            //we set an error corresponding to the failure
            regPassword.setError(passwordErr);
            return;
        }
        if (!regConfirmPassword.getText().toString().equals(password)) {
            //we set an error and cancel action if the password confirmation failed
            regConfirmPassword.setError("Both fields must be equal");
            return;
        }
        Log.d(TAG, "Credentials are valid");

        AuthSingleton.auth.createUserWithEmailAndPassword(DatabaseSingleton.database,email, password, new AuthProvider.AuthListener() {
            @Override
            public void onSuccess() {
                //account creation was successful
                Log.d(TAG, "createUserWithEmail:success");
                Toast.makeText(RegisterActivity.this, "Registration Successful!",
                        Toast.LENGTH_SHORT).show();
                login();
                finish();
            }

            @Override
            public void onFailure() {
                //account creation failed
                Log.d(TAG, "createUserWithEmail:failure");
                Toast.makeText(RegisterActivity.this, "Could not create user with given email",
                        Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**
     * Starts a sign in activity
     */
    private void login() {
        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
        finish();
    }


    /**
     * Starts the creation of a user account
     *
     * @param v button clicked
     */
    //Methods are called by android and we have no use for the View v argument -> ignore waring
    public void register(@SuppressWarnings("UnusedParameters") View v) {
        Log.d(TAG, "registration attempt");
        createAccount(regEmail.getText().toString(), regPassword.getText().toString());
    }
}
