package pl.edu.uj.laciak.gesturedetector;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
    int gestureId = 0;
    int samplesNeeded = 0;
    int sampleNumber = 0;

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
        gestureId = db.getLatestGestureId();
        gestureId++;

        samplesNeeded = Integer.valueOf(db.getOptionValue("gestureRepeats"));


        drawingView.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                if (nameInput.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Najpierw nadaj gestowi nazwę!", Toast.LENGTH_SHORT).show();
                    return true;
                }
                if (sampleNumber == samplesNeeded) {
                    Toast.makeText(getApplicationContext(), "Jest już wystarczająca liczba próbek", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    Utility.saveNextGestureSample(getApplicationContext(), drawingView, nameInput.getText().toString(), types.getSelectedItem().toString(), gestureId);
                    Toast.makeText(getApplicationContext(), ++sampleNumber + " próbka zapisana", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sampleNumber == samplesNeeded) {
                    Toast.makeText(getApplicationContext(), "Zapisano nowy gest", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Zbyt mała liczba próbek - brakuje jeszcze" + (samplesNeeded - sampleNumber), Toast.LENGTH_LONG).show();
                }
            }
        });
    }


}
