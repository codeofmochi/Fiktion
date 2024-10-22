package ch.epfl.sweng.fiktion.views;

import android.content.Context;
import android.os.Bundle;
import android.widget.LinearLayout;

import ch.epfl.sweng.fiktion.R;
import ch.epfl.sweng.fiktion.models.PointOfInterest;
import ch.epfl.sweng.fiktion.providers.DatabaseProvider;
import ch.epfl.sweng.fiktion.views.parents.MenuDrawerActivity;
import ch.epfl.sweng.fiktion.views.utils.POIDisplayer;

public class DiscoverActivity extends MenuDrawerActivity {
    private Context ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        includeLayout = R.layout.activity_discover;
        super.onCreate(savedInstanceState);

        ctx = this;

        int numberOfResults = 200;
        DatabaseProvider.getInstance().getAllPOIs(numberOfResults, new DatabaseProvider.GetMultiplePOIsListener() {
            private LinearLayout layout = (LinearLayout) findViewById(R.id.discover_layout);

            @Override
            public void onFailure() {
            }

            @Override
            public void onNewValue(final PointOfInterest poi) {
                layout.addView(POIDisplayer.createPoiCard(poi, ctx));
            }
        });
    }
}
