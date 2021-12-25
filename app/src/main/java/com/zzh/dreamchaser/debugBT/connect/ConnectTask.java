package com.zzh.dreamchaser.debugBT.connect;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;

import java.util.UUID;

import static com.zzh.dreamchaser.debugBT.tool.myLog.logD;

public class ConnectTask implements Runnable {
    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private BluetoothSocket bluetoothSocket;
    private BluetoothDevice romoteDevice;
    private BLESPPUtils.OnDeviceConnectAction onDeviceConnectAction;
    private String deviceMac;
    public ConnectTask(String deviceMac, BLESPPUtils.OnDeviceConnectAction onDeviceConnectAction) {
        this.deviceMac = deviceMac;
        this.onDeviceConnectAction = onDeviceConnectAction;
    }

    @Override
    public void run() {

        // 尝试获取 bluetoothSocket
        try {
            UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
            romoteDevice = bluetoothAdapter.getRemoteDevice(deviceMac);
            bluetoothSocket = romoteDevice.createRfcommSocketToServiceRecord(SPP_UUID);
        } catch (Exception e) {
            logD("获取Socket失败");
            e.printStackTrace();
            return;
        }

        // 检查有没有获取到
        if (bluetoothSocket == null) {
            onDeviceConnectAction.onConnectFailed("连接失败:获取Socket失败");
            return;
        }

        while (true) {
            // 尝试连接
            try {
                // 等待连接，会阻塞线程
                bluetoothSocket.connect();
                logD("连接成功");
                onDeviceConnectAction.onConnectSuccess(romoteDevice, bluetoothSocket);
                break;
            } catch (Exception connectException) {
                connectException.printStackTrace();
                logD("连接失败:" + connectException.getMessage());
//                    onBluetoothAction.onConnectFailed("连接失败:" + connectException.getMessage());
//                onDeviceConnectAction.onConnectFailed("超时，正在重试……");
//                    return null;
            }
        }
        return;
    }

    public void shutDown() {
        try {
            logD("AsyncTask 开始释放资源");
            bluetoothSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
