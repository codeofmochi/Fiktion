package ch.epfl.sweng.fiktion.providers;

import android.graphics.Bitmap;

/**
 * Photo provider
 *
 * @author Pedro Da Cunha
 */
public abstract class PhotoProvider {

    /**
     * Listener that listens the status of a photo upload
     */
    public interface UploadPhotoListener {

        /**
         * what to do if the upload succeeds
         */
        void onSuccess();

        /**
         * what to do if the upload fails
         */
        void onFailure();

        /**
         * what to do with the progress
         *
         * @param progress the progress
         */
        void updateProgress(double progress);

    }

    /**
     * Listener that listens the download of (a) bitmap(s)
     */
    public interface DownloadBitmapListener {

        /**
         * what to do with a new downloaded bitmap
         *
         * @param b
         */
        void onNewPhoto(Bitmap b);

        /**
         * what to do if the download fails
         */
        void onFailure();
    }

    /**
     * uploads a bitmap for a poi to the cloud and inform the listener of the result
     *
     * @param bitmap   the bitmap
     * @param poiName  the name of the poi
     * @param listener the listener
     */
    public abstract void uploadPOIBitmap(Bitmap bitmap, String poiName, UploadPhotoListener listener);

    /**
     * download the photos associated to a poi, convert them into a bitmap and send them to the
     * listener
     *
     * @param poiName  the name of the poi
     * @param listener the listener
     */
    public abstract void downloadPOIBitmaps(String poiName, DownloadBitmapListener listener);
}
