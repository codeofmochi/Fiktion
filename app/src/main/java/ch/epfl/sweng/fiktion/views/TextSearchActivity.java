package ch.epfl.sweng.fiktion.views;

import android.content.Intent;
import android.os.Bundle;

import ch.epfl.sweng.fiktion.R;
import ch.epfl.sweng.fiktion.views.parents.MenuDrawerActivity;

public class TextSearchActivity extends MenuDrawerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        includeLayout = R.layout.activity_text_search;
        super.onCreate(savedInstanceState);

        // get search text
        Intent i = getIntent();
        String searchText = i.getStringExtra("SEARCH_TEXT");
    }
}
