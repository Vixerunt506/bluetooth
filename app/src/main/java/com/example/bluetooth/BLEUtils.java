package com.example.bluetooth;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.util.Arrays;
import java.util.UUID;

public class BLEUtils {
    private static final String TAG = "BLEUtils";
    private static BLEUtils instance;

//    public static final UUID UUID_SERVICE = UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb");
//    //notify
//    public static final UUID UUID_CHAR_NOTIFY =  UUID.fromString("0000fff2-0000-1000-8000-00805f9b34fb");
//    public static final UUID UUID_NOTIFY_DESCRIPTOR = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    public static final UUID UUID_SERVICE = UUID.fromString("d973f2e0-b19e-11e2-9e96-0800200c9a66");
    //notify
    public static final UUID UUID_CHAR_NOTIFY =  UUID.fromString("d973f2e1-b19e-11e2-9e96-0800200c9a66");
    public static final UUID UUID_NOTIFY_DESCRIPTOR = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");


    public static BLEUtils getInstance() {
        if (instance == null) {
            instance = new BLEUtils();
        }
        return instance;
    }

    private boolean isConnected = false;


    public static boolean isAndroid12() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.S;
    }

    private ScanCallback scanCallback = null;
    private BluetoothAdapter.LeScanCallback leScanCallback = null;

    @SuppressLint("MissingPermission")
    public void stopScan() {

        BluetoothManager bluetoothManager = (BluetoothManager) App.getInstance().getApplicationContext().getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (scanCallback != null) {
                bluetoothAdapter.getBluetoothLeScanner().stopScan(scanCallback);
                scanCallback = null;
            }
        } else {
            if (leScanCallback != null) {
                bluetoothAdapter.stopLeScan(leScanCallback);
                leScanCallback = null;
            }
        }
    }

    public interface OnScanResult {
        void scanResult(BluetoothDevice bluetoothDevice);
    }

    private BluetoothGatt mBluetoothGatt;

    private void showServices(BluetoothGatt gatt) {
        Log.i(TAG, "gatt.getServices().size():" + gatt.getServices().size());
        for (BluetoothGattService service : gatt.getServices()) {
            StringBuilder allUUIDs = new StringBuilder("UUIDs={\nS=" + service.getUuid().toString());
            for (BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
                allUUIDs.append(",\nC=").append(characteristic.getUuid());
                for (BluetoothGattDescriptor descriptor : characteristic.getDescriptors()) {
                    allUUIDs.append(",\nD=").append(descriptor.getUuid());
                }
            }
            allUUIDs.append("}");
            Log.i(TAG, "onServicesDiscovered:" + allUUIDs.toString());
            Log.i(TAG, "Service discovered" + allUUIDs);
        }
    }

    Handler mHandler = new Handler(Looper.getMainLooper());

    private void toast(String msg) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(App.getAppContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("MissingPermission")
    public void bleScan(OnScanResult onScanResult) {
        stopScan();
        BluetoothManager bluetoothManager = (BluetoothManager) App.getAppContext().getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
        scanCallback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                super.onScanResult(callbackType, result);
                if (onScanResult != null && result != null) {
                    onScanResult.scanResult(result.getDevice());
                }

            }
        };
        bluetoothAdapter.getBluetoothLeScanner().startScan(scanCallback);
    }


    // Connect
    @SuppressLint("MissingPermission")
    public boolean connect(BluetoothDevice bluetoothDevice) {
        if (bluetoothDevice == null) {
            loge("bluetoothDevice = null");
            return false;
        }
        // Close previous connection
        closeConn();
        // Stop scanning
        stopScan();
        BluetoothGattCallback mBluetoothGattCallback = new BluetoothGattCallback() {
            // Callback for connection state changes
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                super.onConnectionStateChange(gatt, status, newState);
                if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_CONNECTED) {
                    mBluetoothGatt = gatt;
                    gatt.discoverServices();
                } else {
                    isConnected = false;
                    toast("Disconnected");
                    mBluetoothGatt = null;
                    if (mOnConnectListener != null) {
                        mOnConnectListener.connect(false);
                    }

                }
            }

            // Callback when services are discovered successfully
            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                super.onServicesDiscovered(gatt, status);
                mBluetoothGatt = gatt;
                showServices(gatt);
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    toast("Connected to the device!");
                    isConnected = true;
                    mBluetoothGatt = gatt;
                    if (mOnConnectListener != null) {
                        mOnConnectListener.connect(true);
                    }
                    notification();
                } else {
                    isConnected = false;
                    toast("Disconnected to the device.");
                    mBluetoothGatt = null;
                    if (mOnConnectListener != null) {
                        mOnConnectListener.connect(false);
                    }
                }
            }

            // Callback for writing to Characteristic
            @Override
            public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                super.onCharacteristicWrite(gatt, characteristic, status);
                showLog("onCharacteristicWrite", characteristic);
            }

            // Callback for reading Characteristic
            @Override
            public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                super.onCharacteristicRead(gatt, characteristic, status);
                showLog("onCharacteristicRead", characteristic);
            }

            // Callback for characteristic notification
            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                super.onCharacteristicChanged(gatt, characteristic);
                showLog("onCharacteristicChanged", characteristic);
                if (mOnNotifyListener != null) {
                    mOnNotifyListener.onNotify(characteristic.getValue());
                }
            }

            // Callback for writing to Descriptor
            @Override
            public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                super.onDescriptorWrite(gatt, descriptor, status);
                showLog("onDescriptorWrite", descriptor);
            }

            // Callback for reading Descriptor
            @Override
            public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                super.onDescriptorRead(gatt, descriptor, status);
                showLog("onDescriptorRead", descriptor);
            }
        };
        mBluetoothGatt = bluetoothDevice.connectGatt(App.getAppContext(), false, mBluetoothGattCallback, BluetoothDevice.TRANSPORT_LE);
        return true;
    }

    private void showLog(String tag, BluetoothGattCharacteristic characteristic) {
//        String msg = new String(characteristic.getValue());
        Log.e(TAG, tag + "[" + characteristic.getUuid().toString() + "]:" + Arrays.toString(characteristic.getValue()));
    }

    private void showLog(String tag, BluetoothGattDescriptor descriptor) {
        String msg = new String(descriptor.getValue());
        Log.e(TAG, tag + "[" + descriptor.getUuid().toString() + "]:" + msg);
    }

    @SuppressLint("MissingPermission")
    private void closeConn() {
        if (mBluetoothGatt != null) {
            mBluetoothGatt.disconnect();
            mBluetoothGatt.close();
        }
    }

    private OnConnectListener mOnConnectListener;
    private OnNotifyListener mOnNotifyListener;

    public void setOnConnectListener(OnConnectListener onConnectListener) {
        mOnConnectListener = onConnectListener;
    }

    private void loge(String msg) {
        Log.e(TAG, msg);
    }

    // Check if connected
    public boolean isConnect() {
        if (mBluetoothGatt == null) {
            return false;
        }
        return isConnected;
    }

    @SuppressLint("MissingPermission")
    public boolean notification() {
        BluetoothGattService service = mBluetoothGatt.getService(UUID_SERVICE);
        // Set up Characteristic notification
        // Get the characteristic that can be notified by UUID
        BluetoothGattCharacteristic characteristic = service.getCharacteristic(UUID_CHAR_NOTIFY);
        mBluetoothGatt.setCharacteristicNotification(characteristic, true);
        // Write notification switch to Descriptor attribute of Characteristic to make the Bluetooth device actively send data to App
        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID_NOTIFY_DESCRIPTOR);
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        boolean result = mBluetoothGatt.writeDescriptor(descriptor);
        return result;
    }

    public void setOnNotifyListener(OnNotifyListener onNotifyListener) {
        this.mOnNotifyListener = onNotifyListener;
    }
}
