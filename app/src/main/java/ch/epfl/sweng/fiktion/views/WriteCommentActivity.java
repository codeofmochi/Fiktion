package ch.epfl.sweng.fiktion.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.Date;

import ch.epfl.sweng.fiktion.R;
import ch.epfl.sweng.fiktion.models.Comment;

public class WriteCommentActivity extends AppCompatActivity {

    private EditText comment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_comment);

        Intent i = getIntent();
        String poiName = i.getStringExtra("POI_NAME");

        Button send = (Button) findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (comment != null) {
                    uploadComment(comment.toString());
                } else {
                    return;
                }
            }
        });
    }

    private void uploadComment(String text) {
        Comment review = new Comment(text, "", new Date());
    }
}
