package ch.epfl.sweng.fiktion;

import org.junit.Test;

import java.util.Set;
import java.util.TreeSet;

import ch.epfl.sweng.fiktion.models.PointOfInterest;
import ch.epfl.sweng.fiktion.models.Position;
import ch.epfl.sweng.fiktion.providers.FirebaseDatabaseProvider;
import ch.epfl.sweng.fiktion.providers.FirebasePointOfInterest;

import static junit.framework.Assert.assertTrue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Created by pedro on 22/11/17.
 */

public class FirebaseEncodingTest {
    private FirebaseDatabaseProvider db = new FirebaseDatabaseProvider(null, null, null);

    @Test
    public void encodeAndDecodeTest() {
        String sample = "a/b//c.d..e[f[[g]h]]i$j$$k#l##m%n%%o";
        String encoded = "a%Sb%S%Sc%Pd%P%Pe%Of%O%Og%Ch%C%Ci%Dj%D%Dk%Hl%H%Hm%%n%%%%o";
        assertThat(db.encode(sample), is(encoded));
        assertThat(db.decode(encoded), is(sample));
    }

    @Test
    public void multiplePercentEncodingTest() {
        String sample = "%%%%%";
        String encoded = "%%%%%%%%%%";
        assertThat(db.encode(sample), is(encoded));
        assertThat(db.decode(encoded), is(sample));
    }

    @Test
    public void createFirebasePOITest() {
        String name = "#hipster/";
        Position pos = new Position(0, 1);

        Set<String> fictions = new TreeSet<>();
        fictions.add("#fiction");
        fictions.add("come@my$house");
        fictions.add("a+[1-[2+3]] then c");
        String description = "A. Lot. Of. Dots. In. My. Description";

        int rating = 0;
        String country = "100% swiss";
        String city = "@city %%";

        PointOfInterest poi = new PointOfInterest(name, pos, fictions, description, rating, country, city);
        FirebasePointOfInterest fPOI = new FirebasePointOfInterest(poi);
        PointOfInterest returnedPOI = fPOI.toPoi();

        assertThat(returnedPOI.name(), is(poi.name()));
        assertThat(returnedPOI.position(), is(poi.position()));
        assertTrue(returnedPOI.fictions().containsAll(poi.fictions()) && poi.fictions().containsAll(returnedPOI.fictions()));
        assertThat(returnedPOI.description(), is(poi.description()));
        assertThat(returnedPOI.rating(), is(poi.rating()));
        assertThat(returnedPOI.country(), is(poi.country()));
        assertThat(returnedPOI.city(), is(poi.city()));
    }
}
