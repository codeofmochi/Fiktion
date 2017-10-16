package ch.epfl.sweng.fiktion.providers;

import ch.epfl.sweng.fiktion.models.Position;

/**
 * Created by pedro on 15/10/17.
 */

public class FirebasePosition {
    public double latitude, longitude;

    public FirebasePosition() {
    }

    public FirebasePosition(Position pos) {
        latitude = pos.latitude();
        longitude = pos.longitude();
    }
}