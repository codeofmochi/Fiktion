package ch.epfl.sweng.fiktion;

import android.content.Intent;
import android.nfc.Tag;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DisplayNearestPoisActivity extends AppCompatActivity {

    private List<String> ls = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_nearest_pois);

        // recover the radius
        Intent intent = getIntent();
        int radius = intent.getIntExtra(FindNearestPoisActivity.RADIUS_KEY, 0);

        // get the database and geofire reference
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        GeoFire geo = new GeoFire(dbRef.child("geofire"));

        // query the points of interests within the radius
        GeoQuery geoQuery = geo.queryAtLocation(new GeoLocation(0,0), radius);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                ls.add(key);
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                // Creates a new ListView to display the research results
                ListView lvFoundPois = (ListView)findViewById(R.id.displayResultPois);
                // Creates a new adapter for the input array into the ListView
                ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, ls);
                // Sets the Adapter
                lvFoundPois.setAdapter(adapter);
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });


    }
}
