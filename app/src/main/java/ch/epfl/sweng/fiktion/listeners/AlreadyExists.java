package ch.epfl.sweng.fiktion.listeners;

/**
 * Listener for premature existence
 *
 * @author pedro
 */
public interface AlreadyExists {
    /**
     * What to do if it already exists
     */
    void onAlreadyExists();
}
