package com.example.bluetooth;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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

    @NonNull
    @Override
    public BLEViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BLEViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_ble, parent, false));
    }

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

    @Override
    public int getItemCount() {
        return mDeviceList == null ? 0 : mDeviceList.size();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void clearBluetoothDevice() {
        mDeviceList.clear();
        addressList.clear();
        notifyDataSetChanged();
    }


    public interface OnItemClickListener {
        void onItemClick(View view, BluetoothDevice bluetoothDevice);
    }

}