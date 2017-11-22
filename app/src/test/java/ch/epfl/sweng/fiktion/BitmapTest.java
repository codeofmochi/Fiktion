package ch.epfl.sweng.fiktion;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.junit.Test;

/**
 * Tests for Bitmap functions
 * Created by dialexo on 23.11.17.
 */

public class BitmapTest {

    @Test
    public void cropAndScaleBitmapOnHorizontal() {
        Bitmap b = Bitmap.createBitmap(400, 200, Bitmap.Config.ARGB_8888);
    }
}
