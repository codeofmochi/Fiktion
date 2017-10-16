package ch.epfl.sweng.fiktion.models;

/**
 * Created by pedro on 09/10/17.
 */

public class Position {
    private final double latitude, longitude;

    public Position(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double latitude() {
        return latitude;
    }

    public double longitude() {
        return longitude;
    }
}
