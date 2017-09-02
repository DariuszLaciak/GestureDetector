package pl.edu.uj.laciak.gesturedetector;

import android.app.Activity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import pl.edu.uj.laciak.gesturedetector.db.PrivateDatabase;
import pl.edu.uj.laciak.gesturedetector.utility.IOAsyncTask;
import pl.edu.uj.laciak.gesturedetector.utility.NetworkState;
import pl.edu.uj.laciak.gesturedetector.utility.Utility;

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
        final Button cancelButton = findViewById(R.id.cancelButton);
        final TextView portText = findViewById(R.id.port_adrress);
        final EditText portInput = findViewById(R.id.portInput);
        final Button testButton = findViewById(R.id.testOptions);

        db = new PrivateDatabase(this);

        String address = db.getOptionValue("address");
        String connType = db.getOptionValue("connection");
        String portNumber = db.getOptionValue("port");
        /*Log.d("connType", connType);
        Log.d("address", address);*/
        if (address == null) {
            address = "";
        }
        if (portNumber == null) {
            portNumber = "";
        }
        editText.setText(address);
        portInput.setText(portNumber);
        if (connType == null || connType.equals(getResources().getString(R.string.ip_address))) {
            method.setSelection(0);
        } else {
            method.setSelection(1);
        }



        method.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String[] options = getResources().getStringArray(R.array.connection_type);
                String selected = adapterView.getItemAtPosition(i).toString();

                if(selected.equals(options[0])){ // Wi-Fi
                    displayText.setText(R.string.ip_address);
                    editText.setVisibility(View.VISIBLE);
                    editText.setInputType(InputType.TYPE_CLASS_PHONE);
                    portText.setVisibility(View.VISIBLE);
                    portInput.setVisibility(View.VISIBLE);
                    handleNetworkConnection(Utility.checkWifiOnAndConnected(OptionsActivity.this));
                }
                else { // Bluetooth
                    displayText.setText(R.string.bluetooth_address);
                    editText.setVisibility(View.VISIBLE);
                    editText.setInputType(InputType.TYPE_CLASS_TEXT);
                    portText.setVisibility(View.INVISIBLE);
                    portInput.setVisibility(View.INVISIBLE);
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
                String connTypeId = displayText.getText().toString();
                String portNumber = portInput.getText().toString();
                if(db.getOptionValue("address") != null){
                    db.updateOption("address",address);
                }
                else {
                    db.saveOptionToPrivateDb("address",address);
                }
                if(db.getOptionValue("connection") != null){
                    db.updateOption("connection", connTypeId);
                }
                else {
                    db.saveOptionToPrivateDb("connection", connTypeId);
                }
                if (db.getOptionValue("port") != null) {
                    db.updateOption("port", portNumber);
                } else {
                    db.saveOptionToPrivateDb("port", portNumber);
                }
                Toast.makeText(view.getContext(), "Pomyślnie zapisano ustawienia", Toast.LENGTH_LONG).show();
                finish();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(OptionsActivity.this, testConnectionToServer(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String testConnectionToServer() {
        Map<String, String> map = new HashMap<>();
        map.put("action", "test");
        map.put("url", db.getServerUrl());
        try {
            String response = new IOAsyncTask().execute(map).get();
            if (response.equals("success")) {
                return "Połączenie aktywne";
            } else {
                return "Połączenie nieaktywne. Sprawdź adres";
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return "Błąd serwera";
    }

    private void handleNetworkConnection(NetworkState state) {
        switch (state) {
            case WIFI_DISABLED:
                Toast.makeText(OptionsActivity.this, "Wyłączone Wi-Fi", Toast.LENGTH_LONG).show();
                Utility.buildConnectToWifiPopup(OptionsActivity.this);
                break;
            case WIFI_CONNECTED:
                Toast.makeText(OptionsActivity.this, "Połączenie Wi-Fi aktywne", Toast.LENGTH_LONG).show();
                break;
            case WIFI_DISCONNECTED:
                Toast.makeText(OptionsActivity.this, "Nie jesteś połączony do sieci Wi-Fi", Toast.LENGTH_LONG).show();
                Utility.createConnectToNetworkDialog(OptionsActivity.this);
                break;
        }
    }


}
