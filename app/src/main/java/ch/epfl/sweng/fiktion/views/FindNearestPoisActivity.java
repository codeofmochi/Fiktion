package ch.epfl.sweng.fiktion.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import ch.epfl.sweng.fiktion.R;

public class FindNearestPoisActivity extends AppCompatActivity {
    public static final String RADIUS_KEY = "ch.epfl.sweng.fiktion.radius";
    private int p = 50;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_nearest_pois);
        SeekBar searchRadius = (SeekBar) findViewById(R.id.searchRadius);
        final TextView radiusSelect = (TextView) findViewById(R.id.radiusSelect);

        searchRadius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                radiusSelect.setText(String.valueOf(progress));
                p = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    public void startDisplayNearestPoisActivity(View view) {
        Intent intent = new Intent(this, DisplayNearestPoisActivity.class);
        intent.putExtra(RADIUS_KEY, p);
        startActivity(intent);
    }

}
