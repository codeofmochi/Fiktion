package ch.epfl.sweng.fiktion.views;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
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
    private TextView empty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_list);

        Intent from = getIntent();
        double lat = from.getDoubleExtra("LATITUDE", 0);
        double lon = from.getDoubleExtra("LONGITUDE", 0);

        list = (LinearLayout) findViewById(R.id.nearbyList);
        empty = (TextView) findViewById(R.id.empty);

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
        list.addView(empty);
        DatabaseProvider.getInstance().findNearPOIs(new Position(latitude, longitude), Config.settings.getSearchRadius(), new DatabaseProvider.FindNearPOIsListener() {

            @Override
            public void onNewValue(PointOfInterest poi) {
                View v = POIDisplayer.createPoiCard(poi, ctx);
                list.addView(v);
                empty.setVisibility(View.GONE);
            }

            @Override
            public void onFailure() {
                Toast.makeText(ctx, "An error occured, please try again later", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
