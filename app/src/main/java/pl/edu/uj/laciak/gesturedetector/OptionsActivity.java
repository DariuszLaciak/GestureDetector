package pl.edu.uj.laciak.gesturedetector;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import pl.edu.uj.laciak.gesturedetector.db.PrivateDatabase;

public class OptionsActivity extends Activity {
    private PrivateDatabase db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        Spinner method = findViewById(R.id.computer_connection);
        final TextView displayText = findViewById(R.id.optionsMenuText);
        final EditText editText = findViewById(R.id.optionsMenuInput);
        final Button saveButton = findViewById(R.id.saveOptionsButton);
        db = new PrivateDatabase(this);



        method.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String[] options = getResources().getStringArray(R.array.connection_type);
                String address = db.getOptionValue("address");
                String connType = db.getOptionValue("connection");
                int connTypeId = 0;
                if(address == null){
                    address = "";
                }
                if(connType == null){
                    connTypeId = R.string.ip_address;
                }
                else {
                    connTypeId = Integer.valueOf(connType);
                }

                if(connTypeId == R.string.bluetooth_address){
                    adapterView.setSelection(1);
                }
                else {
                    adapterView.setSelection(0);
                }

                String selected = adapterView.getItemAtPosition(i).toString();

                if(selected.equals(options[0])){ // Wi-Fi
                    displayText.setText(R.string.ip_address);
                    editText.setVisibility(View.VISIBLE);
                    editText.setInputType(InputType.TYPE_CLASS_PHONE);
                }
                else { // Bluetooth
                    displayText.setText(R.string.bluetooth_address);
                    editText.setVisibility(View.VISIBLE);
                    editText.setInputType(InputType.TYPE_CLASS_TEXT);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                editText.setVisibility(View.INVISIBLE);
                displayText.setText("");
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String address = editText.getText().toString();
                int connTypeId = displayText.getId();
                if(db.getOptionValue("address") != null){
                    db.updateOption("address",address);
                }
                else {
                    db.saveOptionToPrivateDb("address",address);
                }
                if(db.getOptionValue("connection") != null){
                    db.updateOption("connection",connTypeId+"");
                }
                else {
                    db.saveOptionToPrivateDb("connection",connTypeId+"");
                }
                Toast.makeText(view.getContext(),"Pomyślnie zapisano ustawienia",Toast.LENGTH_LONG);
            }
        });

    }


}
