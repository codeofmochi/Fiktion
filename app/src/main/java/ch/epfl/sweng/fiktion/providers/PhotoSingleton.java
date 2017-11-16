package ch.epfl.sweng.fiktion.providers;

/**
 * Created by pedro on 16/11/17.
 */

public class PhotoSingleton {
    public static PhotoProvider photoProvider = new FirebasePhotoProvider();

    private PhotoSingleton() {
    }
}
