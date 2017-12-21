package ch.epfl.sweng.fiktion.views.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.LruCache;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import ch.epfl.sweng.fiktion.listeners.Failure;
import ch.epfl.sweng.fiktion.listeners.Get;
import ch.epfl.sweng.fiktion.providers.PhotoProvider;

/**
 * Handles photo manipulation
 *
 * @author pedro
 */
public class PhotoController {
    // LRU cache
    // only use 1/8 of memory
    private static final int CACHE_SIZE = (int) Runtime.getRuntime().maxMemory() / 8;
    private static LruCache<String, Bitmap> bitmapCache = new LruCache<String, Bitmap>(CACHE_SIZE) {
        protected int sizeOf(String key, Bitmap value) {
            return value.getByteCount();
        }
    };

    /**
     * Listener for the retrieval of bitmaps
     */
    public interface GetBitmapListener extends Get<Bitmap>, Failure {
    }

    /**
     * get the bitmaps associated to a point of interest
     *
     * @param ctx             the activity context
     * @param poiName         the name of the poi
     * @param numberOfBitmaps the maximum number of bitmaps to retrieve (PhotoProvider.ALL_PHOTOS for all photos)
     * @param listener        a listener that listens for the results
     */
    public static void getPOIBitmaps(final Context ctx, final String poiName, int numberOfBitmaps, final GetBitmapListener listener) {
        // get the photo names
        PhotoProvider.getInstance().getPOIPhotoNames(poiName, numberOfBitmaps, new PhotoProvider.GetPhotoNamesListener() {
            @Override
            public void onNewValue(final String photoName) {
                // check if it is in the cache
                Bitmap bitmap = bitmapCache.get(photoName);
                if (bitmap != null) {
                    listener.onNewValue(bitmap);
                    return;
                }

                try {
                    // try to get the bitmap from storage
                    FileInputStream fileInputStream = ctx.openFileInput(photoName);
                    Bitmap b = BitmapFactory.decodeStream(fileInputStream);
                    fileInputStream.close();
                    listener.onNewValue(b);

                    // put the bitmap in the cache
                    bitmapCache.put(photoName, b);
                } catch (IOException e) {
                    // download the bitmap with the photo name
                    PhotoProvider.getInstance().downloadPOIBitmap(poiName, photoName, new PhotoProvider.DownloadBitmapListener() {
                        @Override
                        public void onNewValue(Bitmap b) {
                            listener.onNewValue(b);

                            // put the bitmap in the cache
                            bitmapCache.put(photoName, b);

                            // put the bitmap in the internal storage
                            writeToInternalStorage(ctx, b, photoName);
                        }

                        @Override
                        public void onFailure() {
                            // if one download fails, ignore it
                        }
                    });
                }
            }

            @Override
            public void onFailure() {
                listener.onFailure();
            }
        });
    }

    /**
     * get the profile photo of a user, inform the listener of the result
     *
     * @param ctx      the activity context
     * @param userId   the id of the user
     * @param listener the listener
     */
    public static void getUserProfilePicture(Context ctx, String userId, GetBitmapListener listener) {
        getUserPicture(ctx, userId, PhotoProvider.UserPhotoType.PROFILE, listener);
    }

    /**
     * get the banner photo of a user, inform the listener of the result
     *
     * @param ctx      the activity context
     * @param userId   the id of the user
     * @param listener the listener
     */
    public static void getUserBannerPicture(Context ctx, String userId, GetBitmapListener listener) {
        getUserPicture(ctx, userId, PhotoProvider.UserPhotoType.BANNER, listener);
    }

    private static void getUserPicture(final Context ctx, String userId, final PhotoProvider.UserPhotoType type, final GetBitmapListener listener) {
        final String photoName;
        switch (type) {
            case PROFILE:
                photoName = userId + "profile.jpg";
                break;
            case BANNER:
                photoName = userId + "banner.jpg";
                break;
            default:
                listener.onFailure();
                return;
        }

        // check if it is in the cache
        Bitmap bitmap = bitmapCache.get(photoName);
        if (bitmap != null) {
            listener.onNewValue(bitmap);
            return;
        }

        try {
            // try to get the bitmap from storage
            FileInputStream fileInputStream = ctx.openFileInput(photoName);
            Bitmap b = BitmapFactory.decodeStream(fileInputStream);
            fileInputStream.close();
            listener.onNewValue(b);

            // put the bitmap in the cache
            bitmapCache.put(photoName, b);
        } catch (IOException e) {
            // download the bitmap
            PhotoProvider.getInstance().downloadUserBitmap(userId, type, new PhotoProvider.DownloadBitmapListener() {
                @Override
                public void onNewValue(Bitmap b) {
                    listener.onNewValue(b);

                    // put the bitmap in the cache
                    bitmapCache.put(photoName, b);

                    // put the bitmap in the internal storage
                    writeToInternalStorage(ctx, b, photoName);
                }

                @Override
                public void onFailure() {
                    listener.onFailure();
                }
            });
        }
    }

    private static void writeToInternalStorage(Context ctx, Bitmap b, String photoName) {
        try {
            ByteArrayOutputStream photoBytes = new ByteArrayOutputStream();
            b.compress(Bitmap.CompressFormat.JPEG, 100, photoBytes);
            FileOutputStream fileOutputStream = ctx.openFileOutput(photoName, Context.MODE_PRIVATE);
            fileOutputStream.write(photoBytes.toByteArray());
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}