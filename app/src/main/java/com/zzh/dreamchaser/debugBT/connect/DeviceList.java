package com.zzh.dreamchaser.debugBT.connect;

import android.app.Activity;

import com.zzh.dreamchaser.debugBT.R;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DeviceList {
    public static ArrayList<DeviceHandle> targetDevices = new ArrayList<>();
    public static ExecutorService executorSvcConnect = Executors.newSingleThreadExecutor();
    public static ExecutorService executorSvcRecv = Executors.newScheduledThreadPool(5);
    private static BLESPPUtils.OnBluetoothAction oba;

    public static void setOnBluetoothAction(BLESPPUtils.OnBluetoothAction oba0) {
        oba = oba0;
    }

    public static BLESPPUtils.OnBluetoothAction getOnBluetoothAction() {
        return oba;
    }

    public static DeviceHandle getDeviceHandle(String mac) {
        for (DeviceHandle device : targetDevices)
            if (device.deviceMAC.equals(mac))
                return device;
        return null;
    }

    public static DeviceHandle getDeviceHandle(int id) {
        for (DeviceHandle device : targetDevices)
            if (device.id == id)
                return device;
        return null;
    }

    public static void connect(Activity activity, String mac) {
        removeDevice("DEMO");
        DeviceHandle extDevice = getDeviceHandle(mac);
        if (extDevice != null) {
            extDevice.connect();
        } else {
            DeviceHandle devc = new DeviceHandle(targetDevices.size(), mac, oba, activity);
            targetDevices.add(devc);
            devc.connect();
        }
    }

    public static void demo(Activity activity, String mac) {
        DeviceHandle extDevice = getDeviceHandle(mac);
        if (extDevice == null) {
            DeviceHandle devc = new DeviceHandle(targetDevices.size(), mac, null, activity);
            targetDevices.add(devc);
        }
    }

    public static void removeDevice(String mac) {
        DeviceHandle dh = getDeviceHandle(mac);
        if (dh != null) {
            dh.onDisConnect();
            targetDevices.remove(dh);
        }
    }

    public static void removeAll() {
        for (DeviceHandle dh : targetDevices) {
            dh.onDisConnect();
            targetDevices.remove(dh);
        }
    }
}
