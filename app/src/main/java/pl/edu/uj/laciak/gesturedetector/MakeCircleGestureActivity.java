package pl.edu.uj.laciak.gesturedetector;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MakeCircleGestureActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_circle_gesture);
        Button back = findViewById(R.id.makeCircleBackButton);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
