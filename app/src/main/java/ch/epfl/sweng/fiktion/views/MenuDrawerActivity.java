package ch.epfl.sweng.fiktion.views;

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

/**
 * A parent class for activities that implement the left menu drawer
 * Created by dialexo on 26.10.17.
 */

public class MenuDrawerActivity extends AppCompatActivity {

    // menu drawer properties
    private String[] menuItems = {"Home", "Nearby", "Profile", "Discover", "Contribute", "Settings"};
    private DrawerLayout drawerLayout;
    private ListView drawerList;
    protected int includeLayout;
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
                } else {
                    Intent i = new Intent(this, HomeActivity.class);
                    // every time we get back to home, we clear the activity stack
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }
                break;
            }
            case 1: {
                // location activity
                if (this.getClass().equals(LocationActivity.class)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    Intent i = new Intent(this, LocationActivity.class);
                    // every time we get back to home, we clear the activity stack
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
                if (this.getClass().equals(AddPOIActivity.class)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    Intent i = new Intent(this, AddPOIActivity.class);
                    // every time we get back to home, we clear the activity stack
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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

        // change menu state
        drawerList.setItemChecked(pos, true);
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
