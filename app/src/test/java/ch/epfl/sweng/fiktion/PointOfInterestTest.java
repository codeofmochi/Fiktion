package ch.epfl.sweng.fiktion;

import org.junit.Test;

import ch.epfl.sweng.fiktion.models.PointOfInterest;
import ch.epfl.sweng.fiktion.models.Position;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by pedro on 09/10/17.
 */

@SuppressWarnings("DefaultFileTemplate")
public class PointOfInterestTest {

    @Test
    public void correctlyCreatesPOITest() {
        Position pos = new Position(3.5, 4.2);
        PointOfInterest poi = new PointOfInterest("Eiffel Tower", pos);
        assertThat(poi.name(), is("Eiffel Tower"));
        assertThat(poi.position(), is(pos));
    }
}
