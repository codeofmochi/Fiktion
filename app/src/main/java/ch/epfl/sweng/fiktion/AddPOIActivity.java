package ch.epfl.sweng.fiktion;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Random;

public class AddPOIActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_poi);
    }

    private Database db = new Database();

    public void addPOI(View view) {
        // Get the text from the plain text
        String poiName = ((EditText) findViewById(R.id.poiName)).getText().toString();
        Random rand = new Random();
        Position pos = new Position(rand.nextDouble(),rand.nextDouble());
        PointOfInterest poi = new PointOfInterest(poiName, pos);
        db.addPOI(poi);
        ((TextView)findViewById(R.id.addConfirm)).setText("Point of interest added");
    }
}
