package ch.epfl.sweng.fiktion.providers;

import com.algolia.search.saas.AlgoliaException;
import com.algolia.search.saas.Client;
import com.algolia.search.saas.CompletionHandler;
import com.algolia.search.saas.Index;
import com.algolia.search.saas.Query;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.sweng.fiktion.models.PointOfInterest;

/**
 * Created by serdar on 10/27/2017.
 * Enables search by text
 */

public class AlgoliaSearchProvider extends SearchProvider {
    private Index index;
    private Query query;

    // Constructor
    // Initializes Algolia connection and pre-build the search query
    public AlgoliaSearchProvider() {
        String applicationID = "2CQXZ238JH";
        String apiKey = "1617c6689ddda8643dddbad6496479ed";
        Client client = new Client(applicationID, apiKey);
        String indexName = "pointsofinterest";
        this.index = client.getIndex(indexName);
        this.query = new Query();
        this.query.setAttributesToRetrieve("name", "latitude", "longitude");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addPoi(PointOfInterest pointOfInterest, final DatabaseProvider.AddPoiListener listener) {
        List<JSONObject> array = new ArrayList<>();
        try {
            // try to serialize input POI object into JSON message and add it to previously
            // specified Algolia index
            array.add(new JSONObject().put("name", pointOfInterest.name()).put("latitude", pointOfInterest.position().latitude())
                    .put("longitude", pointOfInterest.position().longitude()));
            index.addObjectsAsync(new JSONArray(array), null);

        } catch (JSONException e) {
            // Notify when adding the POI fails
            listener.onFailure();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void searchByText(String name, final DatabaseProvider.PoiSearchByTextListener listener) {
        // Set query to input
        query.setQuery(name);

        // Execute query on Algolia index
        index.searchAsync(query, new CompletionHandler() {
            @Override
            public void requestCompleted(JSONObject jsonObject, AlgoliaException e) {
                // If there is no error and we have poi's in our result JSON message,
                // that means our search is successful and thus, return search results
                // to calling method
                if (jsonObject != null && e == null) {
                    //results = resultsJsonParser.parseResults(jsonObject);
                    List<PointOfInterest> results = parseResults(jsonObject);
                    for (int i = 0; i < results.size(); i++) {
                        listener.onSuccess(results.get(i));
                    }
                }
                // When there is an exception
                else if (e != null)
                    listener.onFailure();
                    // When no POI with matching name is found in Algolia
                else
                    listener.onDoesntExist();
            }
        });
    }

    /**
     * Serialize a POI into a JSON object
     *
     * @param poi A Point of Interest to serialize
     * @return the POI in JSON object
     */
    private JSONObject serializePOI(PointOfInterest poi) {
        assert (poi != null);

        JSONObject obj = new JSONObject();

        return obj;
    }

    /**
     * Parse Algolia's results to a list of POI IDs
     *
     * @param jsonObject A JSON array which is the result of Algolia's search
     * @return a list of POIs IDs as Strings
     */
    private List<String> parseResults(JSONObject jsonObject) {
        assert (jsonObject != null);

        List<String> results = new ArrayList<>();
        JSONArray hits = jsonObject.optJSONArray("hits");

        // list is empty
        if (hits == null) return new ArrayList<>();

        // iterate on JSON array and find POI name
        for (int i = 0; i < hits.length(); ++i) {
            JSONObject hit = hits.optJSONObject(i);
            if (hit != null) {
                String poi = hit.optString("name");
                if (poi != null) {
                    results.add(poi);
                }
            }
        }

        return results;
    }
}
