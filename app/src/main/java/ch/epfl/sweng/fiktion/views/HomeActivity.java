package ch.epfl.sweng.fiktion.views;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import ch.epfl.sweng.fiktion.R;

public class HomeActivity extends AppCompatActivity {

    // menu drawer properties
    private String[] menuItems = {"Home", "Nearby", "Profile", "Discover", "Contribute", "Settings"};
    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private ActionBarDrawerToggle drawerToggle;

    // click menu items listener
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
            selectItem(pos);
        }
    }

    // what to do on menu item select
    private void selectItem(int pos) {
        // close current activity
        switch (pos) {
            case 0: {
                // home activity
                if (this.getClass().equals(HomeActivity.class)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
                else {
                    Intent i = new Intent(this, HomeActivity.class);
                    startActivity(i);
                }
                break;
            }
            case 1: {
                // location activity
                if(this.getClass().equals(LocationActivity.class)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
                else {
                    Intent i = new Intent(this, LocationActivity.class);
                    startActivity(i);
                }
                break;
            }
            case 2:
                // profile activity
                break;
            case 3:
                // discover activity
                break;
            case 4: {
                // add POI activity
                if(this.getClass().equals(AddPOIActivity.class)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
                else {
                    Intent i = new Intent(this, AddPOIActivity.class);
                    startActivity(i);
                }
                break;
            }
            case 5:
                // settings activity
                break;
            default:
                break;
        }

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

        // setup icon menu toggle
        drawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                R.string.drawer_open,
                R.string.drawer_close
        );
        drawerLayout.setDrawerListener(drawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        drawerToggle.syncState();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration conf) {
        super.onConfigurationChanged(conf);
        drawerToggle.onConfigurationChanged(conf);
    }

    /**
     * Starts the location activity
     */
    public void startLocationActivity(View view) {
        Intent i = new Intent(this, LocationActivity.class);
        startActivity(i);
    }
}
