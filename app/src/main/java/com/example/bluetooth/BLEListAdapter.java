/**
 * The BLEListAdapter class provides an adapter for managing the list of Bluetooth devices displayed in the UI.
 *
 * Functionalities:
 * - Addition of Bluetooth Devices: Bluetooth devices can be dynamically added to the list while avoiding duplicates.
 * - View Inflation and ViewHolder Creation: Inflate the item view layout and creates a ViewHolder to represent each item in the list.
 * - Data Binding: Binds Bluetooth device data to the corresponding ViewHolder to display device information.
 * - Click Handling: Defines click listeners for each item in the list to handle user interactions, such as selecting a device.
 */
package com.example.bluetooth;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bluetooth.BLEViewHolder;

import java.util.ArrayList;
import java.util.List;

public class BLEListAdapter extends RecyclerView.Adapter<BLEViewHolder> {
    private OnItemClickListener onItemClickListener;
    private List<BluetoothDevice> mDeviceList = new ArrayList<>();

    private List<String> addressList = new ArrayList<>();


    // Add to Bluetooth list
    public void addBluetoothDevice(BluetoothDevice bluetoothDevice) {
        String address = bluetoothDevice.getAddress();
        @SuppressLint("MissingPermission") String name = bluetoothDevice.getName();
        if (!TextUtils.isEmpty(name)) {
            name = name.trim();
        }
        if (!TextUtils.isEmpty(name) && !addressList.contains(address)) {
            addressList.add(address);
            mDeviceList.add(bluetoothDevice);
            notifyDataSetChanged();
        }
    }

    // Inflate the item view layout and creates a ViewHolder.
    @NonNull
    @Override
    public BLEViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BLEViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_ble, parent, false));
    }

    // Bind the Bluetooth device data to the ViewHolder.
    // Set click listener for each item in the list.
    @SuppressLint("MissingPermission")
    @Override
    public void onBindViewHolder(@NonNull BLEViewHolder holder, int position) {
        BluetoothDevice device = mDeviceList.get(position);
        holder.tv_name.setText(device.getName());
        holder.tv_address.setText(device.getAddress());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(v, mDeviceList.get(holder.getAdapterPosition()));
                }
            }
        });
    }

    // Return the total number of items in the list.
    @Override
    public int getItemCount() {
        return mDeviceList == null ? 0 : mDeviceList.size();
    }

    // Set the item click listener for handling click events on RecyclerView items.
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    // Clear the list of Bluetooth devices.
    public void clearBluetoothDevice() {
        mDeviceList.clear();
        addressList.clear();
        notifyDataSetChanged();
    }


    // Definition for a callback to be invoked when an item in the RecyclerView is clicked.
    public interface OnItemClickListener {
        void onItemClick(View view, BluetoothDevice bluetoothDevice);
    }
}