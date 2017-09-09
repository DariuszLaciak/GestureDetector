package pl.edu.uj.laciak.gesturedetector;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

import pl.edu.uj.laciak.gesturedetector.db.PrivateDatabase;
import pl.edu.uj.laciak.gesturedetector.utility.Utility;
import pl.edu.uj.laciak.gesturedetector.view.DrawingView;

public class NewDrawingActivity extends Activity {
    DrawingView drawingView;
    Button cancel;
    Button save;
    Spinner types;
    TextView nameInput;
    PrivateDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_drawing);
        drawingView = findViewById(R.id.newDrawingArea);
        cancel = findViewById(R.id.cancelNewDrawingButton);
        save = findViewById(R.id.saveNewDrawingButton);
        types = findViewById(R.id.newDrawingSpinner);
        nameInput = findViewById(R.id.newDrawingNameInput);
        db = new PrivateDatabase(this);
        Utility.fillSpinner(db, this, types);


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String savePath = saveBitmap(drawingView);
                String name = nameInput.getText().toString();
                String action = types.getSelectedItem().toString();
                Log.d("DBG", savePath);
                Log.d("DBG", name);
                Log.d("DBG", action);
                db.saveNewDrawingGesure(name, action, savePath);
                finish();
            }
        });
    }

    private String saveBitmap(DrawingView view) {
        OutputStream os = null;
        String filename = "Drawing_" + SystemClock.elapsedRealtime() + ".jpg";
        try {
            os = openFileOutput(filename, MODE_PRIVATE);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (Utility.getBitmapFromView(view).compress(Bitmap.CompressFormat.JPEG, 100, os)) {
            Toast.makeText(this, "Pomyślnie dodano gest", Toast.LENGTH_SHORT).show();
        }
        try {
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return filename;
    }
}
