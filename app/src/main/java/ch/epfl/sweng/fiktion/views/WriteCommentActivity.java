package ch.epfl.sweng.fiktion.views;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import ch.epfl.sweng.fiktion.R;

import static ch.epfl.sweng.fiktion.providers.DatabaseSingleton.database;

public class WriteCommentActivity extends AppCompatActivity {

    private EditText comment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_comment);

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

    }
}
