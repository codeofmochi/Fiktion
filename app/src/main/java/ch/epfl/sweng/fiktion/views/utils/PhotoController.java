package ch.epfl.sweng.fiktion.views.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

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

    public interface GetBitmapsListener extends Get<Bitmap>, Failure {
    }

    public static void getPOIBitmaps(final Context ctx, final String poiName, int numberOfBitmaps, final GetBitmapsListener listener) {
        // get the photo names
        PhotoProvider.getInstance().getPOIPhotoNames(poiName, numberOfBitmaps, new PhotoProvider.GetPhotoNamesListener() {
            @Override
            public void onNewValue(final String photoName) {
                try {
                    // try to get the bitmap from storage
                    FileInputStream fileInputStream = ctx.openFileInput(photoName);
                    Bitmap b = BitmapFactory.decodeStream(fileInputStream);
                    fileInputStream.close();
                    listener.onNewValue(b);
                    Log.d("mylogs", "onNewValue: HIT");
                } catch (IOException e) {
                    Log.d("mylogs", "onNewValue: MISS");
                    // download the bitmap with the photo name
                    PhotoProvider.getInstance().downloadPOIBitmap(poiName, photoName, new PhotoProvider.DownloadBitmapListener() {
                        @Override
                        public void onNewValue(Bitmap b) {
                            listener.onNewValue(b);

                            // put the bitmap in storage
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
}
