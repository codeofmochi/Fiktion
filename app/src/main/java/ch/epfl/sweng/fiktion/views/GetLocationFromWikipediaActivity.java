package ch.epfl.sweng.fiktion.views;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import ch.epfl.sweng.fiktion.R;

public class GetLocationFromWikipediaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_location_from_wikipedia);
    }

    /*
        Retrieve link from text field and get coordinates
        Called when Get Coordinates button is pressed
     */
    public void retrieveWikiLink(View view) {
        // get value
        EditText urlInput= (EditText) findViewById(R.id.wikipedia_url);
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
        else if(!wikiURL.toLowerCase().matches(".*(wikipedia.org\\/wiki\\/.*)")) {
            String err = "Wrong link format : must follow wikipedia.org/wiki/Article";
            urlInput.setError(err);
        }
        else {
            // get article ID
            int articleNamePos = wikiURL.indexOf("wiki/")+"wiki/".length();
            String wikiName = wikiURL.substring(articleNamePos);
            Toast.makeText(this, wikiName, Toast.LENGTH_LONG).show();
        }
    }
}
