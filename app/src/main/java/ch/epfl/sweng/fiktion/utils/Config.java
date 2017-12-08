package ch.epfl.sweng.fiktion.utils;

import ch.epfl.sweng.fiktion.models.Settings;

/**
 * Configuration class
 *
 * @author pedro
 */
public class Config {

    // boolean value for testing purposes
    public static boolean TEST_MODE = false;
    public static Settings settings = new Settings(Settings.DEFAULT_SEARCH_RADIUS);
}