package com.zzh.dreamchaser.debugBT.connect;

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

    public static void connect(String mac){
        DeviceHandle extDevice = DeviceList.getDeviceHandle(mac);
        if (extDevice!=null){
            extDevice.connect();
        }else{
            DeviceHandle devc = new DeviceHandle(targetDevices.size(),mac, oba);
            DeviceList.targetDevices.add(devc);
            devc.connect();

        }
    }

    public static DeviceHandle getDeviceHandle(String mac){
        for (DeviceHandle device: targetDevices)
            if (device.deviceMAC.equals(mac))
                return device;
        return null;
    }
}
