package ch.epfl.sweng.fiktion.providers;

import org.json.JSONException;
import org.json.JSONObject;

import ch.epfl.sweng.fiktion.models.PointOfInterest;
import ch.epfl.sweng.fiktion.models.Position;

/**
 * Created by serdar on 11/16/2017.
 * Creates POI object from a JSON representation
 */

public class PoiJsonParser
{
    public PointOfInterest parse(JSONObject jsonObject)
    {
        if (jsonObject == null)
            return null;
        String name = jsonObject.optString("name");
        System.out.println("name = " + name);

        // Set default values for latitude and longitude.
        // If they are not initialized properly in parsing, POI object build will fail
        double latitude = -1000;
        double longitude = -1000;
        try {
            JSONObject position = jsonObject.getJSONObject("position");
            latitude = position.optDouble("latitude");
            System.out.println("latitude = " + latitude);
            longitude = position.optDouble("longitude");
            System.out.println("longitude = " + longitude);
        } catch (JSONException e) {
            System.out.println(e.getLocalizedMessage());
        }
        //double latitude = jsonObject.optDouble("latitude");
        //System.out.println("latitude = " + latitude);
        // double longitude = jsonObject.optDouble("longitude");
        // System.out.println("longitude = " + longitude);
        if (name != null  && latitude < 180 && latitude > -180 && longitude < 180 && longitude > -180)
            return new PointOfInterest(name, new Position(latitude, longitude),null,null,0,null,null);
        return null;
    }
}
