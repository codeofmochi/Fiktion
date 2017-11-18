package ch.epfl.sweng.fiktion.providers;

import java.util.Map;
import java.util.TreeMap;

import ch.epfl.sweng.fiktion.models.PointOfInterest;
import ch.epfl.sweng.fiktion.models.Position;

/**
 * A point of interest implementation for Firebase
 *
 * @author pedro
 */
public class FirebasePointOfInterest {
    public String name = "";
    public FirebasePosition position = new FirebasePosition(new Position(0, 0));
    public Map<String, Boolean> fictions = new TreeMap<>();
    public String description = "";
    public int rating = 0;
    public String country = "";
    public String city = "";

    /**
     * Default constructor for calls to DataSnapshot.getValue(FirebasePointOfInterest.class)
     */
    public FirebasePointOfInterest() {
    }

    /**
     * Constructs a Firebase point of position
     *
     * @param poi a point of interest
     */
    public FirebasePointOfInterest(PointOfInterest poi) {
        name = poi.name();
        position = new FirebasePosition(poi.position());
        description = poi.description();
        rating = poi.rating();
        for (String fiction : poi.fictions()) {
            fictions.put(fiction, true);
        }
        country = poi.country();
        city = poi.city();
    }

    /**
     * Returns the real version PointOfInterest
     *
     * @return the point of interest
     */
    PointOfInterest toPoi() {
        return new PointOfInterest(name, position.toPosition(), fictions.keySet(), description, rating, country, city);
    }
}
