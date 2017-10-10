package ch.epfl.sweng.fiktion;

/**
 * Created by pedro on 09/10/17.
 */

public class PointOfInterest {
    public String name = "";
    public Position position = new Position(0,0);
    public PointOfInterest() {}
    public PointOfInterest(String name, Position position) {
        this.name = name;
        this.position = position;
    }
}
