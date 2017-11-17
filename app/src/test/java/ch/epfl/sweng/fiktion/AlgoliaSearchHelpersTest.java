package ch.epfl.sweng.fiktion;


import junit.framework.AssertionFailedError;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.sweng.fiktion.models.PointOfInterest;
import ch.epfl.sweng.fiktion.models.Position;
import ch.epfl.sweng.fiktion.providers.PoiJsonParser;
import ch.epfl.sweng.fiktion.providers.SearchResultsJsonParser;

/**
 * Created by serdar on 11/14/2017.
 * Test class for Algolia search by text methods and their helpers (mainly for JSON parsing)
 */

@RunWith(MockitoJUnitRunner.class)
public class AlgoliaSearchHelpersTest {

    @Mock
    JSONObject poiRep;

    @Mock
    JSONObject invalidJSON;



    // Test whether PoiJsonParser class parses a valid JSON object correctly to
    //      the corresponding POI object correctly
    //      HOWEVER, we only parsed name and position this time
    @Test
    public void poiJsonParserTest() {
        PoiJsonParser parser = new PoiJsonParser();
        String jsonText = "{\n" +
                "  \"name\": \"Eiffel tower\",\n" +
                "  \"position\": {\n" +
                "    \"latitude\": 48.8584,\n" +
                "    \"longitude\": 2.2945\n" +
                "  }\n" +
                "}";
        String invalidJsonText = "{I am invalid and I know it :)}";
        try {
            poiRep = new JSONObject(jsonText);
            invalidJSON = new JSONObject(invalidJsonText);
            PointOfInterest resultPoi = parser.parse(poiRep);
            PointOfInterest invalidPoi = parser.parse(invalidJSON);
            PointOfInterest nullPoi = parser.parse(null);

            // The test shouldn't fail
            if (BuildConfig.DEBUG && !(resultPoi.name().equals("Eiffel tower") &&
                    resultPoi.position().latitude() == 48.8584 &&
                    resultPoi.position().longitude() == 2.2945))
                throw new AssertionFailedError();

            // The test will fail if the parsed POI is NOT null,
            // We are giving an invalid string for creating JSON object
            if (BuildConfig.DEBUG && invalidPoi != null)
                throw new AssertionFailedError();

            // A null POI should be the output for a null JSON object
            // Otherwise, the test will fail.
            if (BuildConfig.DEBUG && nullPoi != null)
                throw new AssertionFailedError();

        } catch (Exception e) {
            e.printStackTrace();
            throw new AssertionFailedError();
        }
    }

    @Test
    public void searchResultsJsonParserTest() {
        SearchResultsJsonParser resultsJsonParser = new SearchResultsJsonParser();
        String invalidJsonText = "{I am invalid and I know it :)}";
        String sampleSearchResult = "{\n" +
                "  \"hits\": [\n" +
                "    {\n" +
                "      \"name\": \"Eiffel tower\",\n" +
                "      \"position\": {\n" +
                "        \"latitude\": 48.8584,\n" +
                "        \"longitude\": 2.2945\n" +
                "      },\n" +
                "      \"objectID\": \"33478251\",\n" +
                "      \"_highlightResult\": {\n" +
                "        \"name\": {\n" +
                "          \"value\": \"Eiffel <em>tower</em>\",\n" +
                "          \"matchLevel\": \"full\",\n" +
                "          \"fullyHighlighted\": false,\n" +
                "          \"matchedWords\": [\n" +
                "            \"tower\"\n" +
                "          ]\n" +
                "        }\n" +
                "      },\n" +
                "      \"_rankingInfo\": {\n" +
                "        \"nbTypos\": 0,\n" +
                "        \"firstMatchedWord\": 1,\n" +
                "        \"proximityDistance\": 0,\n" +
                "        \"userScore\": 0,\n" +
                "        \"geoDistance\": 0,\n" +
                "        \"geoPrecision\": 1,\n" +
                "        \"nbExactWords\": 0,\n" +
                "        \"words\": 1,\n" +
                "        \"filters\": 0\n" +
                "      }\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"Tallinn TV Tower\",\n" +
                "      \"position\": {\n" +
                "        \"latitude\": 59.4712,\n" +
                "        \"longitude\": 24.8875\n" +
                "      },\n" +
                "      \"objectID\": \"33478341\",\n" +
                "      \"_highlightResult\": {\n" +
                "        \"name\": {\n" +
                "          \"value\": \"Tallinn TV <em>Tower</em>\",\n" +
                "          \"matchLevel\": \"full\",\n" +
                "          \"fullyHighlighted\": false,\n" +
                "          \"matchedWords\": [\n" +
                "            \"tower\"\n" +
                "          ]\n" +
                "        }\n" +
                "      },\n" +
                "      \"_rankingInfo\": {\n" +
                "        \"nbTypos\": 0,\n" +
                "        \"firstMatchedWord\": 2,\n" +
                "        \"proximityDistance\": 0,\n" +
                "        \"userScore\": 3,\n" +
                "        \"geoDistance\": 0,\n" +
                "        \"geoPrecision\": 1,\n" +
                "        \"nbExactWords\": 0,\n" +
                "        \"words\": 1,\n" +
                "        \"filters\": 0\n" +
                "      }\n" +
                "    }\n" +
                "  ],\n" +
                "  \"nbHits\": 2,\n" +
                "  \"page\": 0,\n" +
                "  \"nbPages\": 1,\n" +
                "  \"hitsPerPage\": 10,\n" +
                "  \"processingTimeMS\": 1,\n" +
                "  \"exhaustiveNbHits\": true,\n" +
                "  \"query\": \"tower\",\n" +
                "  \"params\": \"query=tower&hitsPerPage=10&page=0&attributesToRetrieve=*&highlightPreTag=%3Cais-highlight-0000000000%3E&highlightPostTag=%3C%2Fais-highlight-0000000000%3E&getRankingInfo=1&facets=%5B%5D&tagFilters=\",\n" +
                "  \"index\": \"pointsofinterest\",\n" +
                "  \"serverUsed\": \"c9-eu-3.algolia.net\",\n" +
                "  \"parsedQuery\": \"tower\",\n" +
                "  \"timeoutCounts\": false,\n" +
                "  \"timeoutHits\": false\n" +
                "}";
        List<PointOfInterest> expectedList = new ArrayList<>();
        PointOfInterest eiffelTower = new PointOfInterest("Eiffel tower", new Position(48.8584,2.2945),null,null,0,null,null);
        PointOfInterest tallinnTvTower = new PointOfInterest("Tallinn TV Tower", new Position(59.4712,24.8875),null,null,0,null,null);

        expectedList.add(eiffelTower);
        expectedList.add(tallinnTvTower);

        // First check for null JSON object
        List<PointOfInterest> nullList = resultsJsonParser.parseResults(null);

        if (BuildConfig.DEBUG && nullList != null)
            throw new AssertionFailedError();

        // Then check for invalid JSON object
        // The output should again be a null list of POIs
        try {
            invalidJSON = new JSONObject(invalidJsonText);
            List<PointOfInterest> againNullList = resultsJsonParser.parseResults(invalidJSON);

            if (BuildConfig.DEBUG && againNullList != null)
                throw new AssertionFailedError();

        } catch (JSONException e) {
            System.out.println(e.getLocalizedMessage());
        }

        // Finally check for a valid search result
        try {
            poiRep = new JSONObject(sampleSearchResult);
            List<PointOfInterest> sampleResultList = resultsJsonParser.parseResults(poiRep);

            if (BuildConfig.DEBUG && !(sampleResultList.equals(expectedList)))
                throw new AssertionFailedError();

        } catch (JSONException e){
            System.out.println(e.getLocalizedMessage());
        }

    }
}
