package ch.epfl.sweng.fiktion.views;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

import ch.epfl.sweng.fiktion.R;

public class HomeActivity extends AppCompatActivity {

    // menu drawer properties
    private String[] menuItems = {"Home", "Nearby", "Profile", "Discover", "Contribute", "Settings"};
    private DrawerLayout drawerLayout;
    private ListView drawerList;
    // click menu items listener
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
            selectItem(pos);
        }
    }
    // what to do on menu item select
    private void selectItem(int pos) {
        drawerList.setItemChecked(pos, true);
        drawerLayout.closeDrawer(drawerList);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // setup menu
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerList = (ListView) findViewById(R.id.menu_drawer);
        // set adapter for list view
        drawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, menuItems));
        drawerList.setOnItemClickListener(new DrawerItemClickListener());
    }

    /**
     * Starts the location activity
     */
    public void startLocationActivity(View view) {
        Intent i = new Intent(this, LocationActivity.class);
        startActivity(i);
    }
}
