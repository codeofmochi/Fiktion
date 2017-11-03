package ch.epfl.sweng.fiktion.views;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.net.URL;
import java.util.Scanner;

import ch.epfl.sweng.fiktion.R;

public class GetLocationFromWikipediaActivity extends AppCompatActivity {
    public static final String NEW_POI_LATITUDE = "ch.epfl.sweng.fiktion.GetLocationFromMapActivity.newLatitude";
    public static final String NEW_POI_LONGITUDE = "ch.epfl.sweng.fiktion.GetLocationFromMapActivity.newLongitude";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_location_from_wikipedia);

        // Change policy to allow networking on main thread
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
    }

    /*
        Retrieve link from text field and get coordinates
        Called when Get Coordinates button is pressed
     */
    public void retrieveWikiLink(View view) {
        // get value
        EditText urlInput = (EditText) findViewById(R.id.wikipedia_url);
        String wikiURL = urlInput.getText().toString();
        double lat, lon;

        // check if empty
        if (wikiURL.isEmpty()) {
            urlInput.setError("Please provide a link");
        }
        // check if is wikipedia link
        else if (!wikiURL.toLowerCase().contains("wikipedia.org")) {
            String err = "Link must be from wikipedia.org";
            urlInput.setError(err);
        }
        // check if correct article link
        else if (!wikiURL.toLowerCase().matches(".*(wikipedia.org\\/wiki\\/.+)")) {
            String err = "Wrong link format : must follow wikipedia.org/wiki/Article";
            urlInput.setError(err);
        } else {
            // get article ID
            int articleNamePos = wikiURL.lastIndexOf("wiki/") + "wiki/".length();
            String wikiName = wikiURL.substring(articleNamePos);

            // get wikipedia JSON response
            String wikiAPIURL = "https://en.wikipedia.org/w/api.php?action=query&prop=coordinates&format=json&titles=";
            URL wikiRequest;
            try {
                wikiRequest = new URL(wikiAPIURL + wikiName);

                Scanner scanner = new Scanner(wikiRequest.openStream());
                String response = scanner.useDelimiter("\\Z").next();

                JSONTokener tokener = new JSONTokener(response);
                JSONObject json = new JSONObject(tokener);

                // parse json and test for results :
                // test for query property, and if at least 1 page with index > -1
                if (!json.has("query") || json.getJSONObject("query").getJSONObject("pages").has("-1")) {
                    Toast.makeText(this, "No article found", Toast.LENGTH_SHORT).show();
                } else {
                    String pageID = json.getJSONObject("query").getJSONObject("pages").keys().next();
                    JSONObject page = json.getJSONObject("query").getJSONObject("pages").getJSONObject(pageID);

                    // check if coordinates property exists
                    if (!page.has("coordinates")) {
                        Toast.makeText(this, "No coordinates found in article", Toast.LENGTH_SHORT).show();
                    }
                    // we have our values
                    else {
                        lat = page.getJSONArray("coordinates").getJSONObject(0).getDouble("lat");
                        lon = page.getJSONArray("coordinates").getJSONObject(0).getDouble("lon");

                        // return values to caller activity
                        Intent retrieveCoordsIntent = new Intent();
                        retrieveCoordsIntent.putExtra(NEW_POI_LATITUDE, lat);
                        retrieveCoordsIntent.putExtra(NEW_POI_LONGITUDE, lon);
                        // send the intent to the parent
                        setResult(RESULT_OK, retrieveCoordsIntent);
                        // close this activity
                        finish();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Request failed, please try again", Toast.LENGTH_LONG).show();
            }
        }
    }
}
