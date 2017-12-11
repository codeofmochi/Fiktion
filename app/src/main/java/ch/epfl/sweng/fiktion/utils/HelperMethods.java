package ch.epfl.sweng.fiktion.utils;

/** Class with all helper methods
 * Created by Rodrigo on 11.12.2017.
 */

public class HelperMethods {

    /**
     * Returns the distance between two points with their latitude and longitude coordinates
     *
     * @param lat1  latitude of the first position
     * @param long1 longitude of the first position
     * @param lat2  latitude of the second position
     * @param long2 longitude of the second position
     * @return the distance
     */
    public static double dist(double lat1, double long1, double lat2, double long2) {
        double theta = long1 - long2;
        double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
        return 111.18957696 * Math.toDegrees(Math.acos(dist));
    }}
