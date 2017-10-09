package ch.epfl.sweng.fiktion;

/**
 * Created by pedro on 09/10/17.
 */

public class PointOfInterest {
    private final String name;
    private final Position position;
    PointOfInterest(String name, Position position) {
        this.name = name;
        this.position = position.copy();
    }
    public String name() {
        return name;
    }
    public Position position() {
        return position.copy();
    }
}
