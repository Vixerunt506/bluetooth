package com.example.bluetooth;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Spinner mSp;
    private List<String> lists;
    private ArrayAdapter<String> arrayAdapter;
    private Button mBt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        setView();
    }

    private void setView() {
        mBt.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View v) {
                String selectedValue = (String) mSp.getSelectedItem();

                if ("NFC".equals(selectedValue)) {
                    NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(MainActivity.this);
                    if (!nfcAdapter.isEnabled()) {
                        // NFC is not enabled, prompt the user to enable it
                        Toast.makeText(MainActivity.this, "Turn on NFC", Toast.LENGTH_SHORT).show();
                    }
                } else if ("Bluetooth".equals(selectedValue)) {
                    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                    if (bluetoothAdapter == null) {
                        // Device does not support Bluetooth
                        Toast.makeText(MainActivity.this, "Bluetooth is not supported on this device", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!bluetoothAdapter.isEnabled()) {
                        // Bluetooth is not enabled, prompt the user to enable it
                        Toast.makeText(MainActivity.this, "Turn on Bluetooth", Toast.LENGTH_SHORT).show();
                    } else {
                        // Bluetooth is enabled, proceed to Pairing activity
                        Intent intent = new Intent(MainActivity.this, BLEPairing.class);
                        startActivity(intent);
                    }
                }
            }
        });
    }

    private void initView() {
        mSp = findViewById(R.id.mainactivity_sp);
        mBt = findViewById(R.id.mainactivity_bt);
        lists = new ArrayList<>();
        lists.add("NFC");
        lists.add("Bluetooth");
        arrayAdapter = new ArrayAdapter<String>(
                MainActivity.this, android.R.layout.simple_expandable_list_item_1, lists
        );
        mSp.setAdapter(arrayAdapter);
    }
}