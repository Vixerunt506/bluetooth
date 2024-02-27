/**
 * The BLEViewHolder class represents a single item view in the RecyclerView used by the BLEListAdapter.
 * This class holds references to the views that display the name and address of a Bluetooth device.
 */
package com.example.bluetooth;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class BLEViewHolder extends RecyclerView.ViewHolder {
    public TextView tv_name;
    public TextView tv_address;
    public BLEViewHolder(@NonNull View itemView) {
        super(itemView);
        tv_name = itemView.findViewById(R.id.tv_name);
        tv_address = itemView.findViewById(R.id.tv_address);
    }
}
