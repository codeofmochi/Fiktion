package ch.epfl.sweng.fiktion;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbAddName("Pedro");
    }

    private void dbAddName(String name) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("name");
        myRef.setValue(name);
    }
}
