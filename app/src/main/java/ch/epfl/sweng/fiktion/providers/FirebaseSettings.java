package ch.epfl.sweng.fiktion.providers;

import ch.epfl.sweng.fiktion.models.Settings;
import ch.epfl.sweng.fiktion.utils.Config;

/** Settings implementation for firebase
 */

public class FirebaseSettings {
    public int searchRadius = Config.DEFAULT_SEARCH_RADIUS;

    public FirebaseSettings(){}

    public FirebaseSettings(Settings settings){
        searchRadius = settings.getSearchRadius();
    }

    public Settings toSettings(){
        return new Settings(searchRadius);
    }
}
