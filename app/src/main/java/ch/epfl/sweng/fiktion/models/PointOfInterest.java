package ch.epfl.sweng.fiktion.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A point of interest
 *
 * @author pedro
 */
public class PointOfInterest {
    private final String name;
    private final Position position;
    private final List<String> fictions;
    private final String description;
    private final int rating;
    private final String country;
    private final String city;

    /**
     * Constructs a point of interest
     *
     * @param name        the name of the fiction
     * @param position    the position of the fiction
     * @param fictions    fictions associated to the point of interest
     * @param description a description of the point of interest
     * @param rating      the rating score of the point of interest
     */
    public PointOfInterest(String name, Position position, List<String> fictions, String description, int rating, String country, String city) {
        this.name = name;
        this.position = position;
        this.fictions = fictions;
        this.description = description;
        this.rating = rating;
        this.country = country;
        this.city = city;
    }

    /**
     * Returns the point of interest name
     *
     * @return the name
     */
    public String name() {
        return name;
    }

    /**
     * Returns the point of interest position
     *
     * @return the position
     */
    public Position position() {
        return position;
    }

    /**
     * Returns the fictions associated to the point of interest
     *
     * @return a list of the fictions
     */
    public List<String> fictions() {
        return Collections.unmodifiableList(new ArrayList<>(fictions));
    }

    /**
     * Returns the description of the point of interest
     *
     * @return the description text
     */
    public String description() {
        return description;
    }

    /**
     * Returns the rating of the point of interest
     *
     * @return the rating
     */
    public int rating() {
        return rating;
    }

    /**
     * Returns the country of the point of interest
     *
     * @return the country
     */
    public String country() {
        return country;
    }

    /**
     * Returns the city of the point of interest
     *
     * @return the city
     */
    public String city() {
        return city;
    }

    /**
     * Verifies poi equality by name
     *
     * @param that the object we compare with
     * @return true if the pois have the same name, false otherwise
     */
    @Override
    public boolean equals(Object that) {
        return that != null &&
                that instanceof PointOfInterest &&
                name.equals(((PointOfInterest) that).name);

    }
}
