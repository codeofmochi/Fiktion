package ch.epfl.sweng.fiktion.listeners;

/**
 * Listener for the retrieval of an object
 *
 * @param <V> the type of the object
 * @author pedro
 */
public interface Get<V> {
    /**
     * What to do with the retrieved object
     *
     * @param value the retrieved object
     */
    void onNewValue(V value);
}
