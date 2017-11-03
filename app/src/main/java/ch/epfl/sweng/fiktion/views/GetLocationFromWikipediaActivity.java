package ch.epfl.sweng.fiktion.views;

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
            Toast.makeText(this, wikiName, Toast.LENGTH_LONG).show();

            // get wikipedia JSON response
            String wikiAPIURL = "https://en.wikipedia.org/w/api.php?action=query&prop=coordinates&format=json&titles=";
            URL wikiRequest;
            try {
                wikiRequest = new URL(wikiAPIURL + wikiName);

                Scanner scanner = new Scanner(wikiRequest.openStream());
                String response = scanner.useDelimiter("\\Z").next();

                JSONTokener tokener = new JSONTokener(response);
                JSONObject json = new JSONObject(tokener);

                // parse json and test for results

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Request failed, please try again", Toast.LENGTH_LONG).show();
            }
        }
    }
}
