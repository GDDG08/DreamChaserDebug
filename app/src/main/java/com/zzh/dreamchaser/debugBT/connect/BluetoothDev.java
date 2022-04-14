package com.zzh.dreamchaser.debugBT.connect;

public class BluetoothDev {
    private String address, name;

    public BluetoothDev(String name, String address){
        this.name = name;
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public String getName() {
        return name;
    }
}
