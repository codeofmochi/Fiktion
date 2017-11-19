package ch.epfl.sweng.fiktion.utils;

/**
 * Object with one public and modifiable field
 *
 * @author pedro
 */
public class Mutable<E> {
    public E value;

    public Mutable() {
    }

    public Mutable(E value) {
        this.value = value;
    }
}
