package ch.epfl.sweng.fiktion.utils;

import ch.epfl.sweng.fiktion.models.Settings;
import ch.epfl.sweng.fiktion.models.User;

/**
 * Configuration class
 *
 * @author pedro
 */
public class Config {

    public static final int DEFAULT_SEARCH_RADIUS = 20;
    public static boolean TEST_MODE = false;
    public static Settings defaultSettings = new Settings(DEFAULT_SEARCH_RADIUS);
}