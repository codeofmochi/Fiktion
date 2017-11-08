package ch.epfl.sweng.fiktion;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ch.epfl.sweng.fiktion.models.PointOfInterest;
import ch.epfl.sweng.fiktion.models.Position;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by pedro on 09/10/17.
 */

@SuppressWarnings("DefaultFileTemplate")
public class PointOfInterestTest {

    @Test
    public void correctlyCreatesPOITest() {
        String name = "Eiffel Tower";
        Position pos = new Position(3.5, 4.2);
        List<String> fictions = new ArrayList<>(Arrays.asList("fiction1", "fiction2", "fiction3"));
        String description = "random description";
        int rating = 4;
        String country = "France";
        String city = "Paris";
        PointOfInterest poi = new PointOfInterest("Eiffel Tower", pos, fictions, description, rating, country, city);
        assertThat(poi.name(), is("Eiffel Tower"));
        assertThat(poi.position(), is(pos));
        for (String f: fictions) {
            assertTrue(poi.fictions().contains(f));
        }
        assertThat(poi.description(), is(description));
        assertThat(poi.rating(), is(rating));
        assertThat(poi.country(), is(country));
        assertThat(poi.city(), is(city));
        PointOfInterest poi2 = new PointOfInterest("Eiffel Tower", null, null ,null , 0,null, null);
        assertTrue(poi.equals(poi2));
        PointOfInterest poi3 = new PointOfInterest("another poi", null, null ,null , 0,null, null);
        assertFalse(poi.equals(poi3));
    }
}
