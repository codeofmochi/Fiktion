package ch.epfl.sweng.fiktion;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by pedro on 09/10/17.
 */

public class PositionTest {
    @Test
    public void correctly_creates_Position() {
        Position p = new Position(42, 7.0);
        assertThat(p.x(),is(42.0));
        assertThat(p.y(),is(7.0));
    }

    @Test
    public void dist_Position() {
        Position p1 = new Position(3.0,5.0);
        Position p2 = new Position(7.0,8.0);
        assertThat(p1.dist(p2), is(5.0));
    }
}
