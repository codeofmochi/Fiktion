package ch.epfl.sweng.fiktion;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

public class FindNearestPoisActivity extends AppCompatActivity {

    private int p = 50;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_nearest_pois);

        SeekBar searchRadius = (SeekBar)findViewById(R.id.searchRadius);
        final TextView radiusSelect = (TextView)findViewById(R.id.radiusSelect);

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

    public int getRadius() {
        return p;
    }

    public void goToDisplayNearestPois(View view) {
        Intent intent = new Intent(this, DisplayNearestPoisActivity.class);
        startActivity(intent);
    }

}
