package ch.epfl.sweng.fiktion;

import org.junit.Test;

import ch.epfl.sweng.fiktion.models.Position;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by pedro on 09/10/17.
 */

public class PositionTest {
    @Test
    public void correctlyCreatesPositionTest() {
        Position p = new Position(42, 7.3);
        assertThat(p.latitude(),is(42.0));
        assertThat(p.longitude(),is(7.3));
    }
}
