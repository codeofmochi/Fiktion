package ch.epfl.sweng.fiktion;

import android.support.v7.app.AppCompatActivity;

import ch.epfl.sweng.fiktion.providers.DatabaseProvider;
import ch.epfl.sweng.fiktion.providers.FirebaseDatabaseProvider;

/**
 * Created by pedro on 15/10/17.
 */

public class DatabaseActivity extends AppCompatActivity {
    public static DatabaseProvider database = new FirebaseDatabaseProvider();
}
