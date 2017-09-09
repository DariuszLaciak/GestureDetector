package pl.edu.uj.laciak.gesturedetector;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MakeGestureActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_gesture);
        Button backButton = findViewById(R.id.makeGestureBackButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
