package pl.edu.uj.laciak.gesturedetector;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import pl.edu.uj.laciak.gesturedetector.db.PrivateDatabase;

public class GesturesSettings extends Activity {
    Button save;
    private PrivateDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestures_settings);
        db = new PrivateDatabase(getApplicationContext());
        save = findViewById(R.id.saveGestureOptions);
        String numberFromDb = db.getOptionValue("gestureRepeats");
        EditText value = findViewById(R.id.numberOfRepeatsValue);
        int numberOfRepeats = numberFromDb == null ? 0 : Integer.valueOf(numberFromDb);
        value.setText(String.valueOf(numberOfRepeats));

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.saveOptionToPrivateDb("gestureRepeats", value.getText().toString());
                finish();
            }
        });
    }
}
