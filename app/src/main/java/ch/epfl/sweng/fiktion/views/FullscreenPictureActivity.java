package ch.epfl.sweng.fiktion.views;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import java.io.FileNotFoundException;

import ch.epfl.sweng.fiktion.R;

/**
 * Activity for the fullscreen of a photo, called from the POIActivity
 * Receives a filename of a fileInput and creates the image
 */
public class FullscreenPictureActivity extends AppCompatActivity {

    private ImageView fullScreen;
    private Bitmap bitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_picture);

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
}
