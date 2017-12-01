package ch.epfl.sweng.fiktion.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import ch.epfl.sweng.fiktion.R;
import ch.epfl.sweng.fiktion.models.Comment;
import ch.epfl.sweng.fiktion.providers.DatabaseProvider;


public class WriteCommentActivity extends AppCompatActivity {

    private String poiName;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_comment);

        Intent i = getIntent();
        poiName = i.getStringExtra(POIPageActivity.POI_NAME);
        userId = i.getStringExtra(POIPageActivity.USER_ID);
    }

    public void uploadComment(View view) {

        String text = ((EditText) findViewById(R.id.comment)).getText().toString();
        if(text.isEmpty()) {
            ((EditText) findViewById(R.id.comment)).setError("You can't add an empty comment");
        } else {
            Comment review = new Comment(text, userId, java.util.Calendar.getInstance().getTime(), 0);

            DatabaseProvider.getInstance().addComment(review, poiName, new DatabaseProvider.AddCommentListener() {
                @Override
                public void onSuccess() {
                    finish();
                }

                @Override
                public void onFailure() {
                }
            });


        }
    }
}
