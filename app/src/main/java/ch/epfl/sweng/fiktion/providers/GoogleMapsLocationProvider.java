package ch.epfl.sweng.fiktion.providers;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import ch.epfl.sweng.fiktion.android.AndroidPermissions;
import ch.epfl.sweng.fiktion.android.AndroidServices;
import ch.epfl.sweng.fiktion.models.PointOfInterest;

import static ch.epfl.sweng.fiktion.providers.Providers.database;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_BLUE;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.defaultMarker;

/**
 * A Google Maps implementation for current location
 * Created by dialexo on 17.10.17.
 */

public class GoogleMapsLocationProvider extends LocationProvider {

    // Google Maps object
    private GoogleMap gmap;
    // My location marker
    private Marker myLocationMarker;
    // Helper boolean to detect first location change
    private boolean firstLocationChange = true;

    /**
     * Helper getter to know if GPS started tracking
     *
     * @return true if GPS is tracking, false otherwise
     */
    public boolean hasLocation() {
        return !this.firstLocationChange;
    }

    /**
     * Helper internal method to update the current location and its marker
     *
     * @param newLocation A new Location to replace the old one
     */
    private void updateLocation(Location newLocation) {
        // update location
        location = newLocation;
        LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());

        // update position marker
        if (firstLocationChange) {
            // update camera
            gmap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
            gmap.moveCamera(CameraUpdateFactory.zoomTo(15));
            // update first time status
            firstLocationChange = false;
        } else {
            // remove old marker
            myLocationMarker.remove();
        }
        // update location marker
        myLocationMarker = gmap.addMarker(
                new MarkerOptions().position(latlng).title("My position")
        );
    }

    /**
     * To be called onMapReady callback in desired UI
     *
     * @param created A GoogleMap given by onMapReady callback
     */
    public void mapReady(Activity ctx, GoogleMap created) {
        this.gmap = created;

        // enable zoom controls
        gmap.getUiSettings().setZoomControlsEnabled(true);

        // check permissions
        if (ContextCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            AndroidPermissions.promptLocationPermission(ctx);
        } else {
            // check location enable and ask otherwise
            AndroidServices.promptLocationEnable(ctx);

            // enable my position
            gmap.setMyLocationEnabled(true);

            // listen on location change
            gmap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                @Override
                public void onMyLocationChange(Location newLocation) {
                    updateLocation(newLocation);
                }
            });

        }
    }

    /**
     * Function to show nearby POIs on map
     * @param radius radius to search in km
     */
    public void showNearPOIs(final int radius) {
        gmap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location newLocation) {
                // first update position
                updateLocation(newLocation);
                // find nearest pois
                database.findNearPois(getPosition(), radius, new DatabaseProvider.FindNearPoisListener() {
                    @Override
                    public void onNewValue(PointOfInterest poi) {
                        // write new marker
                        gmap.addMarker(new MarkerOptions()
                                .position(new LatLng(poi.position().latitude(), poi.position().longitude()))
                                .title(poi.name())
                                .icon(defaultMarker(HUE_BLUE)));
                    }

                    @Override
                    public void onFailure() {
                    }
                });
            }
        });
    }
}
