package pl.edu.uj.laciak.gesturedetector;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

import pl.edu.uj.laciak.gesturedetector.db.PrivateDatabase;
import pl.edu.uj.laciak.gesturedetector.utility.DrawingItem;
import pl.edu.uj.laciak.gesturedetector.utility.GridViewAdapter;
import pl.edu.uj.laciak.gesturedetector.utility.Utility;

public class ShowDrawingActivity extends Activity implements AdapterView.OnItemClickListener {
    PrivateDatabase db;
    ArrayList<DrawingItem> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_drawing);
        db = new PrivateDatabase(this);

        Button back = findViewById(R.id.backShowDrawing);
        Cursor allGestures = db.getDrawingDestures();
        /*GridLayout grid = findViewById(R.id.gridImages);

        prepareGrid(grid, allGestures);
        allGestures.close();*/
        allGestures.moveToFirst();
        data = new ArrayList<>();
        if (allGestures.getCount() != 0) {
            do {
                Bitmap bm = Utility.resize(getBitmapFromFile(allGestures.getString(1)), 220, 220);
                data.add(new DrawingItem(allGestures.getInt(0), allGestures.getString(2), bm, allGestures.getString(3)));
            }
            while (allGestures.moveToNext());
            GridView gridView = findViewById(R.id.gridView);
            gridView.setOnItemClickListener(this);

            GridViewAdapter gridviewAdapter = new GridViewAdapter(getApplicationContext(), R.layout.drawing_item, data);
            gridView.setAdapter(gridviewAdapter);
        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }



    private Bitmap getBitmapFromFile(String filename) {
        InputStream is = null;
        Bitmap bitmap = null;
        try {
            is = openFileInput(filename);
            bitmap = BitmapFactory.decodeStream(is);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            return null;
        }
        return bitmap;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        String message = "Operacja: " + data.get(i).getOperation();
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
