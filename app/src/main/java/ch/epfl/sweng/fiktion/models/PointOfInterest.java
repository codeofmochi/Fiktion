package ch.epfl.sweng.fiktion.models;

/**
 * Created by pedro on 09/10/17.
 */

public class PointOfInterest {
    private final String name;
    private final Position position;
    public PointOfInterest(String name, Position position) {
        this.name = name;
        this.position = position;
    }
    public String name() {
        return name;
    }
    public Position position() {
        return position;
    }

    @Override
    public boolean equals(Object that) {
        return that != null &&
                that instanceof PointOfInterest &&
                name.equals(((PointOfInterest) that).name);

    }
}
