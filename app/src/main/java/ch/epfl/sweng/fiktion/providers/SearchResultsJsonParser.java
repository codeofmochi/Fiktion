package ch.epfl.sweng.fiktion.providers;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.sweng.fiktion.models.PointOfInterest;

/**
 * Created by serdar on 11/16/2017.
 */

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
