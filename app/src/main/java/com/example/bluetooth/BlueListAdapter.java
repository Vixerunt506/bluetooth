package com.example.bluetooth;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class BlueListAdapter extends RecyclerView.Adapter<BlueViewHolder> {
    private OnItemClickListener onItemClickListener;
    private List<BlueDevice> mDeviceList;
    public BlueListAdapter(List<BlueDevice> deviceList) {
        mDeviceList = deviceList;
    }

    @NonNull
    @Override
    public BlueViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BlueViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_device, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BlueViewHolder holder, int position) {
        BlueDevice device = mDeviceList.get(position);
        holder.tv_name.setText(device.name);
        holder.tv_address.setText(device.address);
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
}