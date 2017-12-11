package ch.epfl.sweng.fiktion.listeners;

/**
 * Listener for the modification of an object
 *
 * @param <V> the type of the object
 * @author pedro
 */
public interface Modify<V> {
    /**
     * What to do with the modified object
     *
     * @param value the modified object
     */
    void onModifiedValue(V value);
}
