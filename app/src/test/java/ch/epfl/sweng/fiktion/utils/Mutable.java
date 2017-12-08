package ch.epfl.sweng.fiktion.utils;

/**
 * Object with one public and modifiable field
 *
 * @author pedro
 */
public class Mutable<E> {
    private E value;

    public Mutable() {
    }

    public Mutable(E value) {
        this.value = value;
    }

    public E get() {
        return value;
    }

    public void set(E value) {
        this.value = value;
    }
}
