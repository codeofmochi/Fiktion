package ch.epfl.sweng.fiktion.utils;

/**
 * Created by pedro on 08/12/17.
 */

public class Mutable<E> {
    private E value;

    public Mutable(E value) {
        this.value = value;
    }

    public Mutable() {

    }

    public E get() {
        return value;
    }

    public void set(E newValue) {
        this.value = newValue;
    }
}
