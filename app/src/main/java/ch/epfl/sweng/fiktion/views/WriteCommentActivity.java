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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_comment);

        Intent i = getIntent();
        poiName = i.getStringExtra(POIPageActivity.POI_NAME);
    }

    public void uploadComment(View view) {

        String text = ((EditText) findViewById(R.id.comment)).getText().toString();
        Comment review = new Comment(text, "testID", java.util.Calendar.getInstance().getTime(), 0);

        DatabaseProvider.getInstance().addComment(review, poiName, new DatabaseProvider.AddCommentListener() {
            @Override
            public void onSuccess() {
                ((EditText) findViewById(R.id.comment)).setText("");
            }

            @Override
            public void onFailure() {
            }
        });
    }

    public void getComments(View view) {
        // just a call to check the comments in the logs
        DatabaseProvider.getInstance().getComments(poiName, new DatabaseProvider.GetCommentsListener() {
            @Override
            public void onNewValue(Comment comment) {
                Log.d("comments", "onNewValue: " + comment.getText());
            }

            @Override
            public void onFailure() {
                Log.d("comments", "onFailure: ");
            }
        });
    }
}
