package pl.edu.uj.laciak.gesturedetector;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import pl.edu.uj.laciak.gesturedetector.db.PrivateDatabase;
import pl.edu.uj.laciak.gesturedetector.utility.IOAsyncTask;
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
        fillSpinner();


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


    private void fillSpinner() {
        Map<String, String> map = new HashMap<>();
        map.put("action", "getActions");
        map.put("url", db.getServerUrl());
        try {
            String response = new IOAsyncTask().execute(map).get();
            Log.d("Server", response);
            JSONParser parser = new JSONParser();
            JSONArray ja = (JSONArray) parser.parse(response);
            List<String> items = new ArrayList<>();
            if (ja != null) {
                for (Object o : ja) {
                    items.add(o.toString());
                }
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, items);
            types.setAdapter(adapter);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private String saveBitmap(DrawingView view) {
        OutputStream os = null;
        String filename = "Drawing_" + SystemClock.elapsedRealtime() + ".jpg";
        try {
            os = openFileOutput(filename, MODE_PRIVATE);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (getBitmapFromView(view).compress(Bitmap.CompressFormat.JPEG, 100, os)) {
            Toast.makeText(this, "Pomyślnie dodano gest", Toast.LENGTH_SHORT).show();
        }
        try {
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return filename;
    }

    public Bitmap getBitmapFromView(View view) {
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null)
            bgDrawable.draw(canvas);
        else
            canvas.drawColor(Color.WHITE);
        view.draw(canvas);
        return returnedBitmap;
    }
}
