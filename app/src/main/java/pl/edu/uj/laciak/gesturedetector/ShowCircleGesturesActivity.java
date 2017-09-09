package pl.edu.uj.laciak.gesturedetector;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;

import java.util.ArrayList;

import pl.edu.uj.laciak.gesturedetector.db.PrivateDatabase;
import pl.edu.uj.laciak.gesturedetector.utility.DrawingItem;
import pl.edu.uj.laciak.gesturedetector.utility.GridViewAdapter;
import pl.edu.uj.laciak.gesturedetector.view.CircleDrawingView;

public class ShowCircleGesturesActivity extends Activity {
    Button back;
    GridView gridView;
    PrivateDatabase db;
    ArrayList<DrawingItem> data_;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_circle_gestures);
        back = findViewById(R.id.backShowCircle);
        gridView = findViewById(R.id.gridViewCircle);
        db = new PrivateDatabase(this);
        data_ = new ArrayList();

        Cursor circleGestures = db.getCircleGestures();

        circleGestures.moveToFirst();
        int iter = 0;
        do {


        } while (circleGestures.moveToNext());

        CircleDrawingView circleDrawingView = new CircleDrawingView(this);
        Log.d("CIRCLW!", circleDrawingView.getChildCount() + "");

        GridViewAdapter gridviewAdapter = new GridViewAdapter(getApplicationContext(), R.layout.drawing_item, data_);
        gridView.setAdapter(gridviewAdapter);


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String name = data.getStringExtra("name");
        String operation = data.getStringExtra("operation");
        Bitmap bm = data.getParcelableExtra("bm");
        int id = data.getIntExtra("id", 0);
        DrawingItem item = new DrawingItem(id, name, bm, operation);
        data_.add(requestCode, item);
    }
}
