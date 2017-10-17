package ch.epfl.sweng.fiktion.models;

/**
 * a point of interest
 *
 * @author pedro
 */
public class PointOfInterest {
    private final String name;
    private final Position position;

    /**
     * Constructs a point of interest with a name and a position
     * @param name the name

     * @param position the position
     */
    public PointOfInterest(String name, Position position) {
        this.name = name;
        this.position = position;
    }

    /**
     * Returns the point of interest name
     * @return the name
     */
    public String name() {
        return name;
    }

    /**
     * Returns the point of interest position
     * @return the position
     */
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
