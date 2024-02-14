package com.example.bluetooth;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Pairing extends AppCompatActivity {

    private static final String TAG = "BluetoothPairActivity";
    private TextView tv_info;
    private RecyclerView rv_device;
    private BluetoothAdapter mBluetooth;
    private BlueListAdapter mListAdapter;
    private List<BlueDevice> mDeviceList = new ArrayList<>(); // List Bluetooth devices
    private Handler mHandler = new Handler(Looper.myLooper());
    private int mOpenCode = 1; // Allow scanning of Bluetooth devices returns result code

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        initBluetooth(); // 初始化蓝牙适配器
        tv_info = findViewById(R.id.tv_info);
        rv_device = findViewById(R.id.rv_device);
        rv_device.setLayoutManager(new LinearLayoutManager(this));

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ? new String[]{
                    Manifest.permission.BLUETOOTH_ADVERTISE,
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            } : new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, 0);
            return;
        }
        initBlueDevice();
    }

    private void initBluetooth() {
        mBluetooth = BluetoothAdapter.getDefaultAdapter();
        if (mBluetooth == null) {
            Toast.makeText(this, "Bluetooth is not supported on this device", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initBlueDevice() {
        mDeviceList.clear();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (mListAdapter == null) {
            mListAdapter = new BlueListAdapter(mDeviceList);
            rv_device.setAdapter(mListAdapter);
        } else {
            mListAdapter.notifyDataSetChanged();
        }
        mListAdapter.setOnItemClickListener(new OnItemClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onItemClick(View view, BlueDevice device) {
                BluetoothDevice bluetoothDevice = mBluetooth.getRemoteDevice(device.address);
                Log.d(TAG, "getBondState=" + bluetoothDevice.getBondState() + ", item.state=" + device.state);
                if (bluetoothDevice.getBondState() == BluetoothDevice.BOND_NONE) { // Note bonded yet
                    bluetoothDevice.createBond();
                } else if (bluetoothDevice.getBondState() == BluetoothDevice.BOND_BONDED) {
                    new ConnectThread(bluetoothDevice).start();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                finish();
                return;
            }
        }
        initBlueDevice();
    }

    private Runnable mDiscoverable = new Runnable() {
        public void run() {
            if (mBluetooth.isEnabled()) {
                // Prompt selection dialogue box for whether to allow scanning of Bluetooth devices
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                if (ActivityCompat.checkSelfPermission(Pairing.this, Manifest.permission.BLUETOOTH_ADVERTISE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                startActivityForResult(intent, mOpenCode);
            } else {
                mHandler.postDelayed(this, 1000);
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == mOpenCode) {
            // Refresh after a delay of 50 milliseconds
            mHandler.postDelayed(mRefresh, 50);
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Allow application to be found by other devices",
                        Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Do not allow application to be found by other devices",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Refresh task
    private Runnable mRefresh = new Runnable() {
        @Override
        public void run() {
            beginDiscovery();
            mHandler.postDelayed(this, 30 * 1000);
        }
    };

    // Discovery task
    @SuppressLint("MissingPermission")
    private void beginDiscovery() {
        if (!mBluetooth.isDiscovering()) {
            initBlueDevice();
            tv_info.setText("Searching Bluetooth device...");
            tv_info.setTextSize(20);
            mBluetooth.startDiscovery();
        }
    }

    // Cancel discovery task
    @SuppressLint("MissingPermission")
    private void cancelDiscovery() {
        mHandler.removeCallbacks(mRefresh);
        tv_info.setText("Searching cancelled");
        tv_info.setTextSize(20);
        if (mBluetooth.isDiscovering()) {
            mBluetooth.cancelDiscovery();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mHandler.postDelayed(mRefresh, 50);
        IntentFilter discoveryFilter = new IntentFilter();
        discoveryFilter.addAction(BluetoothDevice.ACTION_FOUND);
        registerReceiver(discoveryReceiver, discoveryFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        cancelDiscovery();
        unregisterReceiver(discoveryReceiver);
    }

    // Return results
    private BroadcastReceiver discoveryReceiver = new BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, "onReceive action=" + action);
            // Discovered devices
            if (action.equals(BluetoothDevice.ACTION_FOUND)) { // Found new devices
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.d(TAG, "name=" + device.getName());
                refreshDevice(device, device.getBondState()); // Added discovered devices to list
            } else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
                tv_info.setText("Searching completed");
                tv_info.setTextSize(20);
            } else if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) { // Bonding situation changed
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState() == BluetoothDevice.BOND_BONDING) {
                    tv_info.setText("Connecting..." + device.getName());
                    tv_info.setTextSize(20);
                } else if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                    tv_info.setText("Connection completed" + device.getName());
                    tv_info.setTextSize(20);
                    mHandler.postDelayed(mRefresh, 50);
                } else if (device.getBondState() == BluetoothDevice.BOND_NONE) {
                    tv_info.setText("Connection cancelled" + device.getName());
                    tv_info.setTextSize(20);
                    refreshDevice(device, device.getBondState());
                }
            }
        }
    };

    // Refresh devices list
    @SuppressLint("MissingPermission")
    private void refreshDevice(BluetoothDevice device, int state) {
        int i;
        for (i = 0; i < mDeviceList.size(); i++) {
            BlueDevice item = mDeviceList.get(i);
            if (item.address.equals(device.getAddress())) {
                item.state = state;
                mDeviceList.set(i, item);
                break;
            }
        }
        if (i >= mDeviceList.size()) {
            mDeviceList.add(new BlueDevice(device.getName(), device.getAddress(), device.getBondState()));
        }
        mListAdapter.notifyDataSetChanged();
    }


    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        @SuppressLint("MissingPermission")
        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket
            // because mmSocket is final.
            BluetoothSocket tmp = null;
            mmDevice = device;

            try {
                // Get a BluetoothSocket to connect with the given BluetoothDevice.
                UUID uuid = UUID.fromString("ed0ef62e-9b0d-11e4-89d3-123b93f75cba");
                tmp = device.createRfcommSocketToServiceRecord(uuid);
                tmp.connect();
            } catch (IOException e) {
                Log.e(TAG, "Socket's create() method failed", e);
            }
            mmSocket = tmp;
        }

        @SuppressLint("MissingPermission")
        public void run() {
            // Cancel discovery because it slows down the connection.
            mBluetooth.cancelDiscovery();

            try {
                // Connect to device through the socket. This call blocks until it succeeds or throws an exception.
                mmSocket.connect();
            } catch (IOException connectException) {
                // Unable to connect, close the socket and return.
                try {
                    mmSocket.close();
                } catch (IOException closeException) {
                    Log.e(TAG, "Could not close the client socket", closeException);
                }
                return;
            }

            // The connection attempt succeeded.
            Toast.makeText(Pairing.this, "Connected！", Toast.LENGTH_SHORT).show();
        }

        // Closes the client socket and causes the thread to finish.
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the client socket", e);
            }
        }
    }
}