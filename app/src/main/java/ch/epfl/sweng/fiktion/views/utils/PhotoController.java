package ch.epfl.sweng.fiktion.views.utils;

import android.graphics.Bitmap;

import ch.epfl.sweng.fiktion.listeners.Failure;
import ch.epfl.sweng.fiktion.listeners.Get;
import ch.epfl.sweng.fiktion.providers.PhotoProvider;

/**
 * Handles photo manipulation
 *
 * @author pedro
 */
public class PhotoController {

    public interface GetBitmapsListener extends Get<Bitmap>, Failure {
    }

    public static void getPOIBitmaps(final String poiName, int numberOfBitmaps, final GetBitmapsListener listener) {
        PhotoProvider.getInstance().getPOIPhotoNames(poiName, numberOfBitmaps, new PhotoProvider.GetPhotoNamesListener() {
            @Override
            public void onNewValue(String photoName) {
                // TODO check if the photo is in the cache

                // download the bitmap with the photo name
                PhotoProvider.getInstance().downloadPOIBitmap(poiName, photoName, new PhotoProvider.DownloadBitmapListener() {
                    @Override
                    public void onNewValue(Bitmap b) {
                        listener.onNewValue(b);

                        // TODO put it in storage
                    }

                    @Override
                    public void onFailure() {
                        // if one download fails, ignore it
                    }
                });
            }

            @Override
            public void onFailure() {
                listener.onFailure();
            }
        });
    }
}
