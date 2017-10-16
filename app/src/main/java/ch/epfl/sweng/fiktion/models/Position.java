package ch.epfl.sweng.fiktion.models;

/**
 * a position
 *
 * @author pedro
 */
public class Position {
    private final double latitude, longitude;

    /**
     * Constructs a position with a lagitude and longitude coordinate
     *
     * @param latitude  the lagitude
     * @param longitude the longitude
     */
    public Position(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * Returns the position latitude
     *
     * @return the latitude
     */
    public double latitude() {
        return latitude;
    }

    /**
     * Returns the position longitude
     *
     * @return the longitude
     */
    public double longitude() {
        return longitude;
    }
}
