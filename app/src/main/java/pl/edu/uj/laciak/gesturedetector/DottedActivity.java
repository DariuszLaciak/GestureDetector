package pl.edu.uj.laciak.gesturedetector;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class DottedActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dotted);
        Button newGestureButton = findViewById(R.id.newCircleGestureButton);
        Button makeGestureButton = findViewById(R.id.makeCircleGestureButton);
        Button showGesturesButton = findViewById(R.id.showCircleGesturesButton);
        Button goBackButton = findViewById(R.id.backcCrcle);

        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        newGestureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DottedActivity.this, DottedGestureActivity.class);
                DottedActivity.this.startActivity(intent);
            }
        });

        showGesturesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DottedActivity.this, ShowCircleGesturesActivity.class);
                DottedActivity.this.startActivity(intent);
            }
        });

        makeGestureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DottedActivity.this, MakeCircleGestureActivity.class);
                DottedActivity.this.startActivity(intent);
            }
        });
    }
}
