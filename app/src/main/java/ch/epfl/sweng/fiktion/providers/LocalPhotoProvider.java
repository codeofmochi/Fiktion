package ch.epfl.sweng.fiktion.providers;

import android.graphics.Bitmap;

import java.util.Map;
import java.util.TreeMap;

/**
 * local photo provider for testing
 *
 * @author pedro
 */
public class LocalPhotoProvider extends PhotoProvider {
    private Map<String, Map<String, Bitmap>> bitmaps = new TreeMap<>();

    /**
     * {@inheritDoc}
     */
    @Override
    public void uploadPOIBitmap(Bitmap bitmap, String poiName, UploadPhotoListener listener) {
        if (!bitmaps.containsKey(poiName)) {
            bitmaps.put(poiName, new TreeMap<String, Bitmap>());
        }
        Map<String, Bitmap> poiPhotos = bitmaps.get(poiName);
        poiPhotos.put(poiName + poiPhotos.size(), bitmap);
        listener.onSuccess();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void getPOIPhotoNames(String poiName, int numberOfPhotos, GetPhotoNamesListener listener) {
        if (bitmaps.containsKey(poiName)) {
            Map<String, Bitmap> poiPhotos = bitmaps.get(poiName);
            if (numberOfPhotos <= ALL_PHOTOS) {
                for (String s : poiPhotos.keySet()) {
                    listener.onNewValue(s);
                }
            } else {
                int i = 0;
                for (String s : poiPhotos.keySet()) {
                    if (i >= numberOfPhotos) {
                        return;
                    }
                    listener.onNewValue(s);
                    ++i;
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void downloadPOIBitmap(String poiName, String photoName, DownloadBitmapListener listener) {
        if (bitmaps.containsKey(poiName)) {
            Map<String, Bitmap> poiPhotos = bitmaps.get(photoName);
            if (poiPhotos.containsKey(photoName)) {
                listener.onNewValue(poiPhotos.get(photoName));
                return;
            }
        }
        listener.onFailure();
    }
}
