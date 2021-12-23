package com.zzh.dreamchaser.debugBT.connect;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.zzh.dreamchaser.debugBT.connect.DeviceList.executorSvcConnect;
import static com.zzh.dreamchaser.debugBT.connect.DeviceList.executorSvcRecv;
import static com.zzh.dreamchaser.debugBT.tool.myLog.logD;

public class DeviceHandle {


    private int id = -1;
    private boolean isConnected = false;
    public String deviceMAC, deviceName;
    public ConnectTask connectTask;
    public RecvTask recvTask;

    BluetoothSocket bluetoothSocket;
    BLESPPUtils.OnBluetoothAction onBluetoothAction;
    BLESPPUtils.OnDeviceConnectAction onDeviceConnectAction = new BLESPPUtils.OnDeviceConnectAction() {
        @Override
        public void onConnectSuccess(BluetoothDevice device, BluetoothSocket socket) {
            deviceName = device.getName();
            bluetoothSocket = socket;
            isConnected = true;
            recvTask = new RecvTask(socket, onDeviceRecvAction);
            executorSvcRecv.submit(recvTask);
            onBluetoothAction.onConnectSuccess(device, socket);
        }

        @Override
        public void onConnectFailed(String msg) {
            onBluetoothAction.onConnectFailed(msg);
        }
    };
    BLESPPUtils.OnDeviceRecvAction onDeviceRecvAction = new BLESPPUtils.OnDeviceRecvAction() {
        @Override
        public void onReceiveBytes(byte[] bytes) {
            onBluetoothAction.onReceiveBytes(id, bytes);
        }

        @Override
        public void onRecvFailed(String msg) {
            onBluetoothAction.onConnectFailed(msg);
        }
    };

    public DeviceHandle(int id, String deviceMAC, BLESPPUtils.OnBluetoothAction onBluetoothAction) {
        this.id = id;
        this.deviceMAC = deviceMAC;
        this.onBluetoothAction = onBluetoothAction;
    }

    public void connect() {
        if (!checkIsConnected()) {
            connectTask = new ConnectTask(deviceMAC, onDeviceConnectAction);
//        new Thread(connectTask).run();
            executorSvcConnect.submit(connectTask);
        }
    }

    public void onDisConnect() {
        isConnected = false;
        recvTask.shutDown();
        try {
            bluetoothSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        onBluetoothAction.onConnectFailed(deviceName+"("+deviceMAC + ")\n已断开连接！");
    }

    public boolean checkIsConnected() {
        logD("connect:" + isConnected);
        return isConnected;
    }


    public void sendData(byte[] msg) {
        if (!checkIsConnected()) {
            logD("DeviceHandle--->" + "Sending failed as no connection found.");
            return;
        }
        try {
            bluetoothSocket.getOutputStream().write(msg);
            onBluetoothAction.onSendBytes(id, msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //@Todo:
}
