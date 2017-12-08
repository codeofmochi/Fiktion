package ch.epfl.sweng.fiktion.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import ch.epfl.sweng.fiktion.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // redirect to home activity
        Intent i = new Intent(this, HomeActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        // we don't want this to exist anymore
        this.finish();
    }
}
