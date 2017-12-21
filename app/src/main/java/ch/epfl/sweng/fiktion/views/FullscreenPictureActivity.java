package ch.epfl.sweng.fiktion.views;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import ch.epfl.sweng.fiktion.R;

/**
 * Activity for the fullscreen of a photo, called from the POIActivity
 * Receives a filename of a fileInput and creates the image
 */
public class FullscreenPictureActivity extends AppCompatActivity {

    private ImageView fullScreen;
    private Bitmap bitmap;
    private android.support.v7.app.ActionBar actionBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_picture);

        // find action bar
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        fullScreen = (ImageView) findViewById(R.id.fullScreen);
        Intent from = getIntent();
        String fileName = from.getStringExtra("Photo");
        try {
            bitmap = BitmapFactory.decodeStream(this.openFileInput(fileName));
            fullScreen.setImageBitmap(bitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Triggered by action bar
     *
     * @param item the caller item
     * @return parent value
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // close this activity
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * show the bitmap in fullscreen
     *
     * @param ctx    the activity of the caller
     * @param bitmap the bitmap we want to see in fullscreen
     */
    public static void showBitmapInFullscreen(Context ctx, Bitmap bitmap) {
        String fileName = "image";
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            FileOutputStream fo = ctx.openFileOutput(fileName, Context.MODE_PRIVATE);
            fo.write(bytes.toByteArray());
            //close file
            fo.close();
        } catch (Exception e) {
            e.printStackTrace();
            fileName = null;
        }
        Intent intent = new Intent(ctx, FullscreenPictureActivity.class);
        intent.putExtra("Photo", fileName);
        ctx.startActivity(intent);
    }
}
