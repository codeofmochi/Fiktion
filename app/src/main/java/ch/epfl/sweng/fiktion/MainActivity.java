package ch.epfl.sweng.fiktion;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.MainButton) {
            Log.d(TAG, "Advancing");
            //we advance to the login activity
            Intent signInActivity = new Intent(this, SignInActivity.class);
            startActivity(signInActivity);
        }
    }
}
