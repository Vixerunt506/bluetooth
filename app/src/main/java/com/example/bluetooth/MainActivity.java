/**
 * The MainActivity class represents the main entry point of the Bluetooth application.
 * This activity allows users to choose between NFC and Bluetooth for device communication.
 * It proceeds to the BLEPairing activity upon selecting Bluetooth as the communication method.
 */
package com.example.bluetooth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.bluetooth.BLEPairing;
import com.example.bluetooth.fragment.FAQ;
import com.example.bluetooth.fragment.GetInTouch;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //private Spinner mSp;
    private List<String> lists;
    private ArrayAdapter<String> arrayAdapter;
    private Button mBt;
    private Toolbar mTb;
    private DrawerLayout mDl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        setView();
    }

    private void setView() {
        mTb.setNavigationIcon(R.drawable.navigation);
        mTb.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDl.openDrawer(GravityCompat.START);
            }
        });

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                mDl.closeDrawer(GravityCompat.START); // Close the drawer when an item is selected
                if (item.getItemId() == R.id.nav_menu_getintouch) {
                    // Handle the Screenshot item click
                    // Example: Start a new activity or load a different layout
                    Intent gitintent = new Intent(MainActivity.this, GetInTouch.class);
                    startActivity(gitintent);
                } else if (item.getItemId() == R.id.nav_menu_history) {
                    // Handle the History item click
                    // Example: Start a new activity or load a different layout
                    // Intent intent = new Intent(MainActivity.this, YourHistoryActivity.class);
                    // startActivity(intent);
                } else if (item.getItemId() == R.id.nav_menu_faq) {
                    Intent faqIntent = new Intent(MainActivity.this, FAQ.class);
                    startActivity(faqIntent);
                } else if (item.getItemId() == R.id.nav_menu_updates) {
                    // Handle the Current version item click
                    // Example: Start a new activity or load a different layout
                    // Intent intent = new Intent(MainActivity.this, YourUpdatesActivity.class);
                    // startActivity(intent);
                }
                return true;
            }
        });

        mBt.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View v) {

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

//                String selectedValue = (String) mSp.getSelectedItem();
//                if ("NFC".equals(selectedValue)) {
//                    NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(MainActivity.this);
//                    if (!nfcAdapter.isEnabled()) {
//                        // NFC is not enabled, prompt the user to enable it
//                        Toast.makeText(MainActivity.this, "Turn on NFC", Toast.LENGTH_SHORT).show();
//                    }
//                } else if ("Bluetooth".equals(selectedValue)) {
//                    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//                    if (bluetoothAdapter == null) {
//                        // Device does not support Bluetooth
//                        Toast.makeText(MainActivity.this, "Bluetooth is not supported on this device", Toast.LENGTH_SHORT).show();
//                        return;
//                    }
//                    if (!bluetoothAdapter.isEnabled()) {
//                        // Bluetooth is not enabled, prompt the user to enable it
//                        Toast.makeText(MainActivity.this, "Turn on Bluetooth", Toast.LENGTH_SHORT).show();
//                    } else {
//                        // Bluetooth is enabled, proceed to Pairing activity
//                        Intent intent = new Intent(MainActivity.this, BLEPairing.class);
//                        startActivity(intent);
//                    }
//                }
            }
        });
    }

    private void initView() {
        //mSp = findViewById(R.id.mainactivity_sp);
        mBt = findViewById(R.id.mainactivity_bt);
//        lists = new ArrayList<>();
//        lists.add("NFC");
//        lists.add("Bluetooth");
//        arrayAdapter = new ArrayAdapter<String>(
//                MainActivity.this, android.R.layout.simple_expandable_list_item_1, lists
//        );
        //mSp.setAdapter(arrayAdapter);
        mTb = findViewById(R.id.toolbar);
        mDl = findViewById(R.id.drawer_layout);
    }
}