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
import ch.epfl.sweng.fiktion.models.Position;

/**
 * Created by serdar on 10/27/2017.
 */

public class AlgoliaSearchProvider extends SearchProvider{
    private final String applicationID = "2CQXZ238JH";
    private final String apiKey = "1617c6689ddda8643dddbad6496479ed";
    private final String indexName = "pointsofinterest";
    private Client client;
    private Index index;
    private Query query;
    private SearchResultsJsonParser resultsJsonParser;

    // Constructor
    // Initializes Algolia connection and pre-build the search query
    public AlgoliaSearchProvider(){
        this.client = new Client(applicationID, apiKey);
        this.index =  client.getIndex(indexName);
        this.query = new Query();
        this.query.setAttributesToRetrieve("name", "latitude", "longitude");
        this.resultsJsonParser = new SearchResultsJsonParser();
    }

    // Creates POI object from a JSON representation
    public class PoiJsonParser
    {
        public PointOfInterest parse(JSONObject jsonObject)
        {
            if (jsonObject == null)
                return null;
            String name = jsonObject.optString("name");
            double latitude = jsonObject.optDouble("latitude");
            double longitude = jsonObject.optDouble("longitude");
            if (name != null  && latitude < 180 && latitude > -180 && longitude < 180 && longitude > -180)
                return new PointOfInterest(name, new Position(latitude, longitude));
            return null;
        }
    }

    // Deserialize POI's from their JSON representations on result array
    public class SearchResultsJsonParser
    {
        private PoiJsonParser poiParser = new PoiJsonParser();
        public List<PointOfInterest> parseResults(JSONObject jsonObject)
        {
            if (jsonObject == null)
                return null;
            List<PointOfInterest> results = new ArrayList<>();
            JSONArray hits = jsonObject.optJSONArray("hits");
            if (hits == null)
                return null;
            for (int i = 0; i < hits.length(); ++i) {
                JSONObject hit = hits.optJSONObject(i);
                if (hit == null)
                    continue;
                PointOfInterest pointOfInterest = poiParser.parse(hit);
                if (pointOfInterest == null)
                    continue;
                results.add(pointOfInterest);
            }
            return results;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addPoi(PointOfInterest pointOfInterest, final DatabaseProvider.AddPoiListener listener) {
        List<JSONObject> array = new ArrayList<JSONObject>();
        try {
            // try to serialize input POI object into JSON message and add it to previously
            // specified Algolia index
            array.add(new JSONObject().put("name", pointOfInterest.name()).put("latitude", pointOfInterest.position().latitude())
                    .put("longitude", pointOfInterest.position().longitude()));
            index.addObjectsAsync(new JSONArray(array), null);

        }
        catch (JSONException e) {
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
                    List<PointOfInterest> results = resultsJsonParser.parseResults(jsonObject);
                    for (int i=0; i < results.size(); i++) {
                        listener.onSuccess(results.get(i));
                    }
                }
                // When there is an exception
                else if ( e != null)
                    listener.onFailure();
                // When no POI with matching name is found in Algolia
                else
                    listener.onDoesntExist();
            }
        });
    }
}
