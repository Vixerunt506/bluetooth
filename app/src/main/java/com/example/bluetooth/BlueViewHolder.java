package com.example.bluetooth;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class BlueViewHolder extends RecyclerView.ViewHolder {
    public TextView tv_name;
    public TextView tv_address;
    public BlueViewHolder(@NonNull View itemView) {
        super(itemView);
        tv_name = itemView.findViewById(R.id.tv_name);
        tv_address = itemView.findViewById(R.id.tv_address);
    }
}
