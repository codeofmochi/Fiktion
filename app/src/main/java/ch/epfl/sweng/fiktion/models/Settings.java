package ch.epfl.sweng.fiktion.models;

/** This class represents the application settings
 * Created by Rodrigo on 07.12.2017.
 */

public class Settings {

    //SearchRadius values
    public static final int DEFAULT_SEARCH_RADIUS = 20;
    public static final int MIN_SEARCH_RADIUS = 1;
    public static final int MAX_SEARCH_RADIUS = 200;

    private int searchRadius;

    public Settings(int searchRad){
        searchRadius = searchRad;
    }

    public void updateSearchRadius(int val){
        searchRadius = val;
    }

    public int getSearchRadius(){
        return searchRadius;
    }
}
