package ch.epfl.sweng.fiktion.models;

/** This class represents the application settings
 * Created by Rodrigo on 07.12.2017.
 */

public class Settings {
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
