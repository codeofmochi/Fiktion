package ch.epfl.sweng.fiktion.views;

import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import ch.epfl.sweng.fiktion.R;
import ch.epfl.sweng.fiktion.views.parents.MenuDrawerActivity;

public class HomeActivity extends MenuDrawerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // give layout to parent class
        includeLayout = R.layout.activity_home;
        super.onCreate(savedInstanceState);

        // change text color
        TextView featured = (TextView) findViewById(R.id.featured);
        Spannable span = new SpannableString(featured.getText());
        span.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorPrimary)), 12, featured.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        featured.setText(span);
    }

    /**
     * Starts the location activity
     */
    public void startLocationActivity(View view) {
        Intent i = new Intent(this, LocationActivity.class);
        startActivity(i);
    }

    /**
     * Starts display akihabara activity
     *
     * @param view
     */
    public void startDisplayAkihabara(View view) {
        Intent i = new Intent(this, POIPageActivity.class);
        i.putExtra("POI_NAME", "Akihabara");
        startActivity(i);
    }

    public void startTextSearchActivity(View view) {
        Intent i = new Intent(this, TextSearchActivity.class);
        // get search field
        EditText placeText = (EditText) findViewById(R.id.placeText);
        EditText fictionText = (EditText) findViewById(R.id.fictionText);
        String searchText = placeText.getText().toString();
        if (searchText.isEmpty()) searchText = fictionText.getText().toString();

        i.putExtra("SEARCH_TEXT", searchText);
        startActivity(i);
    }
}
