package ch.epfl.sweng.fiktion.views;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

import ch.epfl.sweng.fiktion.R;
import ch.epfl.sweng.fiktion.models.PointOfInterest;
import ch.epfl.sweng.fiktion.models.Position;
import ch.epfl.sweng.fiktion.providers.DatabaseProvider;
import ch.epfl.sweng.fiktion.providers.GoogleMapsLocationProvider;
import ch.epfl.sweng.fiktion.utils.Config;
import ch.epfl.sweng.fiktion.views.utils.POIDisplayer;

public class NearbyListActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMapsLocationProvider locationProvider = new GoogleMapsLocationProvider();
    private Context ctx = this;
    private LinearLayout list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_list);

        Intent from = getIntent();
        double lat = from.getDoubleExtra("LATITUDE", 0);
        double lon = from.getDoubleExtra("LONGITUDE", 0);

        list = (LinearLayout) findViewById(R.id.nearbyList);

        searchAndDisplay(lat, lon);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        locationProvider.mapReady(this, googleMap);
        locationProvider.setCustomLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                searchAndDisplay(location.getLatitude(), location.getLongitude());
            }
        });
    }

    private void searchAndDisplay(double latitude, double longitude) {
        // trigger a search
        list.removeAllViews();
        DatabaseProvider.getInstance().findNearPois(new Position(latitude, longitude),
                Config.settings.getSearchRadius(), new DatabaseProvider.FindNearPoisListener() {
            @Override
            public void onNewValue(PointOfInterest poi) {
                View v = POIDisplayer.createPoiCard(poi, ctx);
                list.addView(v);
            }

            @Override
            public void onFailure() {
                Toast.makeText(ctx, "An error occured, please try again later", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
