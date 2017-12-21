package ch.epfl.sweng.fiktion.views.parents;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import ch.epfl.sweng.fiktion.R;
import ch.epfl.sweng.fiktion.views.AddPOIActivity;
import ch.epfl.sweng.fiktion.views.DiscoverActivity;
import ch.epfl.sweng.fiktion.views.HomeActivity;
import ch.epfl.sweng.fiktion.views.LocationActivity;
import ch.epfl.sweng.fiktion.views.ProfileActivity;
import ch.epfl.sweng.fiktion.views.SettingsActivity;
import ch.epfl.sweng.fiktion.views.TextSearchActivity;

/**
 * A parent class for activities that implement the left menu drawer
 * Created by dialexo on 26.10.17.
 */

public abstract class MenuDrawerActivity extends AppCompatActivity {

    // menu drawer properties
    private String[] menuItems = {"Home", "Search", "Nearby", "Profile", "Discover", "Contribute", "Settings"};
    private DrawerLayout drawerLayout;
    private ListView drawerList;
    protected int includeLayout;
    private ActionBarDrawerToggle drawerToggle;

    // click menu items listener
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
            selectItem(menuItems[pos]);
        }
    }

    // what to do on menu item select
    private void selectItem(String menuId) {
        // close current activity
        switch (menuId) {
            case "Home": {
                // home activity
                if (this.getClass().equals(HomeActivity.class)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    Intent i = new Intent(this, HomeActivity.class);
                    // clear the activity stack
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }
                break;
            }
            case "Search": {
                // search activity
                if (this.getClass().equals(TextSearchActivity.class)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    Intent i = new Intent(this, TextSearchActivity.class);
                    // clear the activity stack
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }
                break;
            }
            case "Nearby": {
                // location activity
                if (this.getClass().equals(LocationActivity.class)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    Intent i = new Intent(this, LocationActivity.class);
                    // clear the activity stack
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }
                break;
            }
            case "Profile":
                // profile activity
                if (this.getClass().equals(ProfileActivity.class)) {
                    // special case : check if not in state of menu access (not my profile), then allow to show my profile
                    if (((ProfileActivity) this).getState() == ProfileActivity.Action.ANOTHER_PROFILE) {
                        Intent i = new Intent(this, ProfileActivity.class);
                        // clear the activity stack
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                    }
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    Intent i = new Intent(this, ProfileActivity.class);
                    // clear the activity stack
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }
                break;
            case "Discover":
                // discover activity
                if (this.getClass().equals(DiscoverActivity.class)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    Intent i = new Intent(this, DiscoverActivity.class);
                    // clear the activity stack
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }
                break;
            case "Contribute": {
                // add POI activity
                // check authentication state
                if (this.getClass().equals(AddPOIActivity.class)) {
                    // special case : check if in contribute state, then allow to add
                    if (((AddPOIActivity) this).getState() == AddPOIActivity.Action.EDIT) {
                        Intent i = new Intent(this, AddPOIActivity.class);
                        // clear the activity stack
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                    }
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    Intent i = new Intent(this, AddPOIActivity.class);
                    // clear the activity stack
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }
                break;
            }
            case "Settings":
                // settings activity
                if (this.getClass().equals(SettingsActivity.class)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    Intent i = new Intent(this, SettingsActivity.class);
                    // clear the activity stack
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }
                break;
            default:
                break;
        }

        // change menu state
        drawerLayout.closeDrawer(drawerList);
    }

    /**
     * Called on activity creation
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // setup views
        setContentView(R.layout.activity_menu_drawer);
        ViewStub stub = (ViewStub) findViewById(R.id.layout_stub);
        stub.setLayoutResource(includeLayout);
        stub.inflate();

        // setup menu
        this.drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        this.drawerList = (ListView) findViewById(R.id.menu_drawer);

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
        // setup toggle listener
        drawerLayout.setDrawerListener(drawerToggle);
        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        getSupportActionBar().setHomeButtonEnabled(true);
        drawerToggle.syncState();
    }

    /**
     * Called on item click
     *
     * @param item the menu item
     * @return if item is selected
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // close drawer if it was open
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    // open the drawer
                    drawerLayout.openDrawer(GravityCompat.START);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Called after the activity is created
     *
     * @param savedInstanceState
     */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // sync drawer toggle state
        drawerToggle.syncState();
    }

    /**
     * Called when the configuration is changed
     *
     * @param conf
     */
    @Override
    public void onConfigurationChanged(Configuration conf) {
        super.onConfigurationChanged(conf);
        // sync drawer toggle state
        drawerToggle.onConfigurationChanged(conf);
    }
}
