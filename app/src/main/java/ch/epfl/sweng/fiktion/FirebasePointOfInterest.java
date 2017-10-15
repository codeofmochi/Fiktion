package ch.epfl.sweng.fiktion;

/**
 * Created by pedro on 15/10/17.
 */

public class FirebasePointOfInterest {
    public String name;
    public FirebasePosition position;
    public FirebasePointOfInterest() {}
    public FirebasePointOfInterest(PointOfInterest poi) {
        this.name = poi.name();
        this.position = new FirebasePosition(poi.position());
    }
}
