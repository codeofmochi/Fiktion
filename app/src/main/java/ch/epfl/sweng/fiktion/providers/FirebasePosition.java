package ch.epfl.sweng.fiktion.providers;

import ch.epfl.sweng.fiktion.models.Position;

/**
 * A position implementation for Firebase
 *
 * @author pedro
 */
public class FirebasePosition {
    public double latitude, longitude;

    /**
     * Default constructor for calls to DataSnapshot.getValue(FirebasePosition.class)
     */
    public FirebasePosition() {
    }

    /**
     * Constructs a Firebase position
     *
     * @param pos a position
     */
    public FirebasePosition(Position pos) {
        latitude = pos.latitude();
        longitude = pos.longitude();
    }

    /**
     * Returns the real version Position
     *
     * @return the position
     */
    public Position toPosition() {
        return new Position(latitude, longitude);
    }
}