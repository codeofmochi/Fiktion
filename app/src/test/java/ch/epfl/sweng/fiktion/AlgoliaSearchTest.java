package ch.epfl.sweng.fiktion;

import org.junit.Before;
import org.junit.Test;

import ch.epfl.sweng.fiktion.models.PointOfInterest;
import ch.epfl.sweng.fiktion.providers.AlgoliaSearchProvider;

/**
 * Created by serdar on 11/16/2017.
 * Tests searching functionality of Algolia module
 */

public class AlgoliaSearchTest {

    private AlgoliaSearchTest.Result result;

    public enum Result {SUCCESS, ALREADYEXISTS, DOESNTEXIST, FAILURE, NOTHING}

    private void setResult(AlgoliaSearchTest.Result result) {
        this.result = result;
    }

    @Before
    public void setup() {
        AlgoliaSearchProvider searchProvider = new AlgoliaSearchProvider();
    }

    @Test
    public void addPoiTest() {
    }

    @Test
    public void getPoiTest() {

    }
}
