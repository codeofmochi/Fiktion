package ch.epfl.sweng.fiktion.providers;

import android.location.Location;

/** This class when we want to handle the android gps
 * Created by Rodrigo on 11.12.2017.
 */

public class GPSLocationProvider extends LocationProvider {

    /**
     * Returns the distance between the given point latitude and longitude and the GPS location
     *
     * @param lat2  latitude of the position we want to compare with GPS location
     * @param long2 longitude of the position we want to compare with GPS location
     * @return the distance
     */
    public double distanceFromMyLocation(double lat2, double long2) {
        Location myLocation = super.getLocation();
        double theta = myLocation.getLongitude() - long2;
        double dist = Math.sin(Math.toRadians(myLocation.getLatitude())) * Math.sin(Math.toRadians(lat2))
                + Math.cos(Math.toRadians(myLocation.getLatitude())) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
        return 111.18957696 * Math.toDegrees(Math.acos(dist));
    }
}
