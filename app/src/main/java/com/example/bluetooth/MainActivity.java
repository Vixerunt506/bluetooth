package com.example.bluetooth;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Spinner mSp;
    private List<String> lists;
    private ArrayAdapter<String> arrayAdapter;
    private Button mBt;
    private Toolbar mTb;
    private DrawerLayout mDl;
    private PopupWindow pW;
    private Boolean isNFCConnected;
    private View overlayView;
    private Boolean isBluetoothConnected;
    private View bTpopupView;
    private View nfcpopupView;
    private Button nfcpopupButton;
    private Button bTpopupButton;;

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
                        // Bluetooth is not enabled, prompt the user to enable it
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
                        // Bluetooth is enabled, show the Bluetooth popup window
                        showBluetoothPopupWindow();
                    }
                }
            }
        });
    }

    private void initView() {
        mSp = findViewById(R.id.mainactivity_sp);
        mBt = findViewById(R.id.mainactivity_bt);
        mDl = findViewById(R.id.drawer_layout);
        lists = new ArrayList<>();
        lists.add("NFC");
        lists.add("Bluetooth");
        //适配器
        arrayAdapter = new ArrayAdapter<String>(
                MainActivity.this, android.R.layout.simple_expandable_list_item_1, lists
        );
        mSp.setAdapter(arrayAdapter);
    }

    private void showBluetoothPopupWindow() {
        int popupWidth = 880;
        int popupHeight = 1800;

        // Create the popup window
        pW = new PopupWindow(
                bTpopupView,
                popupWidth,
                popupHeight,
                true);

        // Check if the popupWindow is not null before attempting to show
        if (pW != null) {
            // Show the popup window
            View parentView = findViewById(android.R.id.content);
            pW.showAtLocation(parentView, Gravity.CENTER, 0, 0);
            // Set a listener to dismiss the popup window when the "Cancel" button is clicked
            bTpopupButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pW.dismiss(); // Dismiss the popup window
                }
            });
        }
        overlayView.setVisibility(View.VISIBLE);
        pW.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                // Hide the overlay view when the PopupWindow is dismissed
                View overlayView = findViewById(R.id.overlayView);
                overlayView.setVisibility(View.GONE);
            }
        });
    }
}