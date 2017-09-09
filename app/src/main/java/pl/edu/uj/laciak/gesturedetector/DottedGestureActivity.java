package pl.edu.uj.laciak.gesturedetector;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import pl.edu.uj.laciak.gesturedetector.db.PrivateDatabase;
import pl.edu.uj.laciak.gesturedetector.utility.Utility;
import pl.edu.uj.laciak.gesturedetector.view.CircleDrawingView;

public class DottedGestureActivity extends Activity implements CircleDrawingView.OnTouchListener {

    CircleDrawingView gridView;
    //ArrayList<CircleItem> data;
    Spinner spinner;
    EditText name;
    Button save;
    Button back;
    PrivateDatabase db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dotted_gesture);
        db = new PrivateDatabase(this);
        gridView = findViewById(R.id.circlesGestureView);
        //data = generateCircles();
        //gridView.setAdapter(new CircleGridViewAdapter(getApplicationContext(),R.layout.circle,data));
        //gridView.setNumColumns(6);
        spinner = findViewById(R.id.newCircleOperationSpinner);
        name = findViewById(R.id.newCircleNameInput);
        save = findViewById(R.id.newCircleGestureSaveButton);
        back = findViewById(R.id.newCircleGestureBackButton);

        Utility.fillSpinner(db, this, spinner);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String gestureName = name.getText().toString();
                String operation = spinner.getSelectedItem().toString();
                String points = gridView.getFinalPoints().toString();
                Log.d("punkty!", points);
                db.saveNewCircleGesure(gestureName, operation, points);
                Toast.makeText(getApplicationContext(), "Pomyślnie dodano gest", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    /*private ArrayList<CircleItem> generateCircles(){
        ArrayList<CircleItem> list = new ArrayList<>();
        for(int i = 0 ; i < 36 ; ++i){
            list.add(new CircleItem(i));
        }
        return list;
    }*/

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return super.onTouchEvent(motionEvent);
    }
}
