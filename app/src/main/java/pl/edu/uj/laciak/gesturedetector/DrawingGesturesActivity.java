package pl.edu.uj.laciak.gesturedetector;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import pl.edu.uj.laciak.gesturedetector.db.PrivateDatabase;

/**
 * Created by darek on 26.08.17.
 */

public class DrawingGesturesActivity extends Activity {
    private PrivateDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawing_gestures);
        db = new PrivateDatabase(getApplicationContext());
        Button newGestureButton = findViewById(R.id.newDrawingGestureButton);
        Button makeGestureButton = findViewById(R.id.makeDrawingGestureButton);
        Button showGesturesButton = findViewById(R.id.showDrawingGesturesButton);
        Button goBackButton = findViewById(R.id.backDrawing);

        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        newGestureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DrawingGesturesActivity.this, NewDrawingActivity.class);
                DrawingGesturesActivity.this.startActivity(intent);
            }
        });

        showGesturesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DrawingGesturesActivity.this, ShowDrawingActivity.class);
                DrawingGesturesActivity.this.startActivity(intent);
            }
        });

        makeGestureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DrawingGesturesActivity.this, MakeGestureActivity.class);
                DrawingGesturesActivity.this.startActivity(intent);
            }
        });
        /*try {
            String response = new IOAsyncTask().execute("test",db.getServerUrl(),"test").get();
            Toast.makeText(getApplicationContext(),response,Toast.LENGTH_SHORT).show();
            finish();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }*/
    }
}
