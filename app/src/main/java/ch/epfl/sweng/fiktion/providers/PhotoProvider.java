package ch.epfl.sweng.fiktion.providers;

import android.graphics.Bitmap;

import ch.epfl.sweng.fiktion.listeners.Failure;
import ch.epfl.sweng.fiktion.listeners.Get;
import ch.epfl.sweng.fiktion.listeners.Success;
import ch.epfl.sweng.fiktion.utils.Config;

/**
 * Photo provider
 *
 * @author Pedro Da Cunha
 */
public abstract class PhotoProvider {

    private static PhotoProvider photoProvider;

    public static int ALL_PHOTOS = 0;

    /**
     * return the photo provider instance
     *
     * @return the photo provider
     */
    public static PhotoProvider getInstance() {
        if (photoProvider == null) {
            if (Config.TEST_MODE)
                photoProvider = new LocalPhotoProvider();
            else
                photoProvider = new FirebasePhotoProvider();
        }
        return photoProvider;
    }

    public static void setInstance(PhotoProvider photoProvider) {
        PhotoProvider.photoProvider = photoProvider;
    }

    public static void destroyInstance() {
        photoProvider = null;
    }

    /**
     * Listener that listens the status of a photo upload
     */
    public interface UploadPhotoListener extends Success, Failure {
        /**
         * what to do with the progress
         *
         * @param progress the progress
         */
        void updateProgress(double progress);
    }

    public interface GetPhotoNamesListener extends Get<String>, Failure {
    }

    /**
     * Listener that listens the download of (a) bitmap(s)
     */
    public interface DownloadBitmapListener extends Get<Bitmap>, Failure {
    }

    /**
     * uploads a bitmap for a poi to the cloud and inform the listener of the result
     *
     * @param bitmap   the bitmap
     * @param poiName  the name of the poi
     * @param listener the listener
     */
    public abstract void uploadPOIBitmap(Bitmap bitmap, String poiName, UploadPhotoListener listener);

    public abstract void getPOIPhotoNames(String poiName, int numberOfPhotos, GetPhotoNamesListener listener);

    /**
     * download the bitmaps associated to a poi and send them to the listener
     *
     * @param poiName   the name of the poi
     * @param photoName the name of the photo
     * @param listener  the listener
     */
    public abstract void downloadPOIBitmap(String poiName, String photoName, DownloadBitmapListener listener);
}
