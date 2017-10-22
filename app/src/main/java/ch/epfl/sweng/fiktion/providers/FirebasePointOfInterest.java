package ch.epfl.sweng.fiktion.providers;

import ch.epfl.sweng.fiktion.models.PointOfInterest;

/**
 * A point of interest implementation for Firebase
 *
 * @author pedro
 */
class FirebasePointOfInterest {
    public String name;
    public FirebasePosition position;

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
        this.name = poi.name();
        this.position = new FirebasePosition(poi.position());
    }

    /**
     * Returns the real version PointOfInterest
     *
     * @return the point of interest
     */
    public PointOfInterest toPoi() {
        return new PointOfInterest(name, position.toPosition());
    }
}
