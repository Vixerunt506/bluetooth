package com.example.bluetooth;

public class BlueDevice {
    public String name;
    public String address;
    public int state;
    public BlueDevice(String name, String address, int bondState) {
        this.name = name;
        this.address = address;
        this.state = bondState;
    }
}
