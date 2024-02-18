package com.example.bluetooth;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class BLEPairing extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    private static final String TAG = "BluetoothPairActivity";
    private TextView tv_info;
    private RecyclerView rv_device;
    private SwipeRefreshLayout swipeRefreshLayout;

    private static final int REQUEST_CODE_OPEN_GPS = 1;
    private static final int REQUEST_CODE_PERMISSION_LOCATION = 2;
    private BLEListAdapter bleListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble);

        tv_info = findViewById(R.id.tv_info);
        rv_device = findViewById(R.id.rv_device);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout); // Initialise refreshing layout
        swipeRefreshLayout.setOnRefreshListener((SwipeRefreshLayout.OnRefreshListener) this);
        rv_device.setLayoutManager(new LinearLayoutManager(this));
        bleListAdapter = new BLEListAdapter();
        rv_device.setAdapter(bleListAdapter);
        bleListAdapter.setOnItemClickListener(new BLEListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, BluetoothDevice bluetoothDevice) {
                connect(bluetoothDevice);
            }
        });

        checkPermissions();

        BLEUtils.getInstance().setOnNotifyListener(new OnNotifyListener() {
            @Override
            public void onNotify(byte[] bytes) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv_info.setText(sdf.format(new Date()) + " :" + Arrays.toString(bytes));
                    }
                });
            }
        });
    }

    // Restart scanning when swipe refreshing
    @Override
    public void onRefresh() {
        startScan();
    }

    // Connect
    private void connect(final BluetoothDevice bluetoothDevice) {
        toast("Connecting...");

        BLEUtils.getInstance().setOnConnectListener(new OnConnectListener() {
            @Override
            public void connect(boolean isConnect) {
                // Connection status
//                if (isConnect) {
//                    toast("Connected successfully");
//                } else {
//                    toast("Connection failed");
//                }
            }
        });
        BLEUtils.getInstance().connect(bluetoothDevice);
    }

    private void checkPermissions() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!bluetoothAdapter.isEnabled()) {
            toast("Turn on Bluetooth.");
            openBlueDialog();
            return;
        }

        List<String> permissions = new ArrayList<String>();
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        if (BLEUtils.isAndroid12()) {
            permissions.add(Manifest.permission.BLUETOOTH_SCAN);
            permissions.add(Manifest.permission.BLUETOOTH_CONNECT);
        }
        List<String> permissionDeniedList = new ArrayList<>();
        for (String permission : permissions) {
            int permissionCheck = ContextCompat.checkSelfPermission(this, permission);
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                onPermissionGranted(permission);
            } else {
                permissionDeniedList.add(permission);
            }
        }
        if (!permissionDeniedList.isEmpty()) {
            String[] deniedPermissions = permissionDeniedList.toArray(new String[permissionDeniedList.size()]);
            ActivityCompat.requestPermissions(this, deniedPermissions, REQUEST_CODE_PERMISSION_LOCATION);
        } else {
            startScan();
        }
    }

    private void startScan() {
        bleListAdapter.clearBluetoothDevice();
        BLEUtils.getInstance().bleScan(new BLEUtils.OnScanResult() {
            @Override
            public void scanResult(BluetoothDevice bluetoothDevice) {
                bleListAdapter.addBluetoothDevice(bluetoothDevice);
            }
        });
        swipeRefreshLayout.setRefreshing(false);
    }

    // Open Bluetooth dialog
    private void openBlueDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Hint")
                .setMessage("Please turn on Bluetooth first.")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Turn On", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
                        List<String> permissions = new ArrayList<String>();
                        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
                        if (BLEUtils.isAndroid12()) {
                            permissions.add(Manifest.permission.BLUETOOTH_SCAN);
                            permissions.add(Manifest.permission.BLUETOOTH_CONNECT);

                            List<String> permissionDeniedList = new ArrayList<>();
                            for (String permission : permissions) {
                                int permissionCheck = ContextCompat.checkSelfPermission(BLEPairing.this, permission);
                                if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                                    onPermissionGranted(permission);
                                } else {
                                    permissionDeniedList.add(permission);
                                }
                            }
                            if (!permissionDeniedList.isEmpty()) {
                                String[] deniedPermissions = permissionDeniedList.toArray(new String[0]);
                                ActivityCompat.requestPermissions(BLEPairing.this, deniedPermissions, REQUEST_CODE_PERMISSION_LOCATION);
                            } else {
                                bluetoothManager.getAdapter().enable();
                            }
                        } else {
                            bluetoothManager.getAdapter().enable();
                        }
                    }
                })
                .setCancelable(false)
                .show();
    }

    // Permission callback
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.i(TAG, "requestCode:" + requestCode + "  ");
        if (requestCode == REQUEST_CODE_PERMISSION_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // User granted permission
                checkPermissions();
            } else {
                Toast.makeText(BLEPairing.this, "Permission request failed, unable to use the function normally", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void onPermissionGranted(String permission) {
        switch (permission) {
            case Manifest.permission.ACCESS_FINE_LOCATION:
                if (!checkGPSIsOpen()) {
                    new AlertDialog.Builder(this)
                            .setTitle("Hint")
                            .setMessage("Current device needs to turn on location function for Bluetooth scanning.")
                            .setNegativeButton("Cancel",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    })
                            .setPositiveButton("Go to Settings",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                            startActivityForResult(intent, REQUEST_CODE_OPEN_GPS);
                                        }
                                    })
                            .setCancelable(false)
                            .show();
                }
                break;
        }
    }


    private boolean checkGPSIsOpen() {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager == null) {
            return false;
        }
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BLEUtils.getInstance().setOnConnectListener(null);
        BLEUtils.getInstance().stopScan();
    }

    public void toast(String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(BLEPairing.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }
}