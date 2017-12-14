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
    private Map<String, Map<String, Bitmap>> poiBitmaps = new TreeMap<>();
    private Map<String, Bitmap> userProfileBitmaps = new TreeMap<>();
    private Map<String, Bitmap> userBannerBitmaps = new TreeMap<>();

    /**
     * {@inheritDoc}
     */
    @Override
    public void uploadPOIBitmap(Bitmap bitmap, String poiName, UploadPhotoListener listener) {
        if (!poiBitmaps.containsKey(poiName)) {
            poiBitmaps.put(poiName, new TreeMap<String, Bitmap>());
        }
        Map<String, Bitmap> poiPhotos = poiBitmaps.get(poiName);
        poiPhotos.put(poiName + poiPhotos.size(), bitmap);
        listener.onSuccess();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void getPOIPhotoNames(String poiName, int numberOfPhotos, GetPhotoNamesListener listener) {
        if (poiBitmaps.containsKey(poiName)) {
            Map<String, Bitmap> poiPhotos = poiBitmaps.get(poiName);
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
        if (poiBitmaps.containsKey(poiName)) {
            Map<String, Bitmap> poiPhotos = poiBitmaps.get(poiName);
            if (poiPhotos.containsKey(photoName)) {
                listener.onNewValue(poiPhotos.get(photoName));
                return;
            }
        }
        listener.onFailure();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void uploadUserBitmap(Bitmap bitmap, String userId, UserPhotoType type, UploadPhotoListener listener) {
        switch (type) {
            case PROFILE:
                userProfileBitmaps.put(userId, bitmap);
                listener.onSuccess();
                break;
            case BANNER:
                userBannerBitmaps.put(userId, bitmap);
                listener.onSuccess();
                break;
            default:
                listener.onFailure();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void downloadUserBitmap(String userId, UserPhotoType type, DownloadBitmapListener listener) {
        Bitmap b;
        switch (type) {
            case PROFILE:
                b = userProfileBitmaps.get(userId);
                break;
            case BANNER:
                b = userBannerBitmaps.get(userId);
                break;
            default:
                b = null;
                break;
        }
        if (b == null)
            listener.onFailure();
        else
            listener.onNewValue(b);
    }
}
