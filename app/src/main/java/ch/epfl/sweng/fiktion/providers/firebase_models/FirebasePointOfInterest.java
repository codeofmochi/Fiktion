package ch.epfl.sweng.fiktion.providers.firebase_models;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import ch.epfl.sweng.fiktion.models.PointOfInterest;
import ch.epfl.sweng.fiktion.models.Position;
import ch.epfl.sweng.fiktion.providers.firebase_models.FirebasePosition;

import static ch.epfl.sweng.fiktion.providers.FirebaseDatabaseProvider.decode;
import static ch.epfl.sweng.fiktion.providers.FirebaseDatabaseProvider.encode;

/**
 * A point of interest implementation for Firebase
 *
 * @author pedro
 */
@SuppressWarnings("WeakerAccess") // fields need to be public so that firebase can access them
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
        name = encode(poi.name());
        position = new FirebasePosition(poi.position());
        description = encode(poi.description());
        rating = poi.rating();
        for (String fiction : poi.fictions()) {
            fictions.put(encode(fiction), true);
        }
        country = encode(poi.country());
        city = encode(poi.city());
    }

    /**
     * Returns the real version PointOfInterest
     *
     * @return the point of interest
     */
    public PointOfInterest toPoi() {
        Set<String> poiFictions = new TreeSet<>();
        for (String fiction : fictions.keySet()) {
            poiFictions.add(decode(fiction));
        }
        return new PointOfInterest(decode(name),
                position.toPosition(),
                poiFictions,
                decode(description),
                rating,
                decode(country),
                decode(city));
    }
}
