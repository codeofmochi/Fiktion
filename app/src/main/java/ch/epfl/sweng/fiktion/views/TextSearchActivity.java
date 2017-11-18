package ch.epfl.sweng.fiktion.views;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import ch.epfl.sweng.fiktion.R;
import ch.epfl.sweng.fiktion.models.PointOfInterest;
import ch.epfl.sweng.fiktion.providers.DatabaseProvider;
import ch.epfl.sweng.fiktion.providers.DatabaseSingleton;
import ch.epfl.sweng.fiktion.views.parents.MenuDrawerActivity;
import ch.epfl.sweng.fiktion.views.utils.POIDisplayer;

public class TextSearchActivity extends MenuDrawerActivity {

    private EditText searchField;
    private LinearLayout resultsList;
    private Context ctx = this;
    private TextView noResults;
    private final int SEARCH_TIMEOUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        includeLayout = R.layout.activity_text_search;
        super.onCreate(savedInstanceState);

        // adjust background color
        ScrollView bg = (ScrollView) findViewById(R.id.menu_scroll);
        bg.setBackgroundColor(getResources().getColor(R.color.bgLightGray));

        // get search text
        Intent i = getIntent();
        String searchText = i.getStringExtra("SEARCH_TEXT");

        // find search box
        searchField = (EditText) findViewById(R.id.searchText);
        searchField.setText(searchText);

        // find results list
        resultsList = (LinearLayout) findViewById(R.id.resultsList);

        // find no results text
        noResults = (TextView) findViewById(R.id.noResults);

        // search
        search(searchText);
    }

    /**
     * Searches for given text and updates UI when results are found
     *
     * @param text the text to search POIs for
     */
    private void search(String text) {
        // clear previous results
        if (resultsList.getChildCount() > 0) resultsList.removeAllViews();
        // check and alert if search field is empty
        if (text.isEmpty()) {
            searchField.setError(getString(R.string.empty_search));
            Toast.makeText(ctx, "Search field is empty", Toast.LENGTH_SHORT).show();
            return;
        }

        // perform search
        DatabaseSingleton.database.searchByText(text, new DatabaseProvider.SearchPOIByTextListener() {
            @Override
            public void onNewValue(PointOfInterest poi) {
                View pv = POIDisplayer.createPoiCard(poi, ctx);

                // add it to the results list
                resultsList.addView(pv);

                // we found a POI so update no results message
                if (noResults.getVisibility() == View.VISIBLE)
                    noResults.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure() {
                // something went wrong, show error toast
                String err = "Request failed, please try again later.";
                Toast.makeText(ctx, err, Toast.LENGTH_SHORT).show();
            }
        });

        // show loading text (will be hidden if new result, and replaced by no results if nothing shows up)
        noResults.setText("Loading...");
        if (noResults.getVisibility() == View.INVISIBLE) noResults.setVisibility(View.VISIBLE);
        new Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        noResults.setText("No results found");
                    }
                },
                SEARCH_TIMEOUT
        );
    }

    /**
     * Triggered by search button press
     *
     * @param view
     */
    public void triggerSearch(View view) {
        String searchText = searchField.getText().toString();
        // search
        search(searchText);
    }
}
