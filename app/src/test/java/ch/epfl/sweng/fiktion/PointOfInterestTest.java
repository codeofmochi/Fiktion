package ch.epfl.sweng.fiktion;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by pedro on 09/10/17.
 */

public class PointOfInterestTest {

    @Test
    public void correctlyCreatesPOITest() {
        Position pos = new Position(3.5,4.2);
        PointOfInterest poi = new PointOfInterest("Eiffel Tower", pos);
        assertThat(poi.name(), is("Eiffel Tower"));
        assertThat(poi.position(), is(pos));
    }

}
