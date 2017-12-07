package ch.epfl.sweng.fiktion.utils;

import ch.epfl.sweng.fiktion.models.Settings;
import ch.epfl.sweng.fiktion.models.User;

/**
 * Configuration class
 *
 * @author pedro
 */
public class Config {

    //SearchRadius values
    public static final int DEFAULT_SEARCH_RADIUS = 20;
    public static final int MIN_SEARCH_RADIUS = 1;
    public static final int MAX_SEARCH_RADIUS = 200;
    // boolean value for testing purposes
    public static boolean TEST_MODE = false;
    public static Settings defaultSettings = new Settings(DEFAULT_SEARCH_RADIUS);
}