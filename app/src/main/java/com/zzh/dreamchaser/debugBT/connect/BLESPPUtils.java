package com.zzh.dreamchaser.debugBT.connect;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.icu.text.UFormat;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.Formatter;
import java.util.UUID;

import static com.zzh.dreamchaser.debugBT.tool.byteCov.*;
import static com.zzh.dreamchaser.debugBT.tool.myLog.logD;

public class BLESPPUtils {

    private Context mContext;
    private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private OnBluetoothAction mOnBluetoothAction;

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (mOnBluetoothAction != null) mOnBluetoothAction.onFoundDevice(device);
            }
        }
    };

    private final BroadcastReceiver mFinishFoundReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
                if (mOnBluetoothAction != null) mOnBluetoothAction.onFinishFoundDevice();
            }
        }
    };

    private final BroadcastReceiver connectLostReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                DeviceHandle devc = DeviceList.getDeviceHandle(device.getAddress());
                if (devc != null) {
                    devc.onDisConnect();
                }
            }
        }
    };

    public interface OnBluetoothAction {
        void onFoundDevice(BluetoothDevice device);

        void onFinishFoundDevice();

        void onConnectSuccess(BluetoothDevice device, BluetoothSocket socket);

        void onConnectFailed(String msg);

        void onReceiveBytes(int id, byte[] bytes);

        void onSendBytes(int id, byte[] bytes);

    }

    public interface OnDeviceConnectAction {
        void onConnectSuccess(BluetoothDevice device, BluetoothSocket socket);

        void onConnectFailed(String msg);
    }

    public interface OnDeviceRecvAction {
        void onReceiveBytes(byte[] bytes);

        void onRecvFailed(String msg);
    }

    public BLESPPUtils(Context context, OnBluetoothAction onBluetoothAction) {
        mContext = context;
        mOnBluetoothAction = onBluetoothAction;
    }

    public void onCreate() {
        IntentFilter foundFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        mContext.registerReceiver(mReceiver, foundFilter);
        IntentFilter finishFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        mContext.registerReceiver(mFinishFoundReceiver, finishFilter);
        IntentFilter connectivityFilter = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        mContext.registerReceiver(connectLostReceiver, connectivityFilter);
    }

    public void onDestroy() {
        try {
            logD("onDestroy，开始释放资源");
//            mConnectTask.isRunning = false;
//            mConnectTask.cancel(true);
            mContext.unregisterReceiver(mReceiver);
            mContext.unregisterReceiver(mFinishFoundReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startDiscovery() {
        if (mBluetoothAdapter.isDiscovering()) mBluetoothAdapter.cancelDiscovery();
        mBluetoothAdapter.startDiscovery();
    }

//    public void connect(BluetoothDevice device) {
//        mBluetoothAdapter.cancelDiscovery();
//        connect(device.getAddress());
//    }
//
//    public void connect(String deviceMac) {
//        ConnectTask mConnectTask2 = new ConnectTask(mOnBluetoothAction);
//        /*if (mConnectTask2.getStatus() == AsyncTask.Status.RUNNING && mConnectTask2.isRunning) {
//            if (mOnBluetoothAction != null){
//                onDestroy();
//                mConnectTask2 = new ConnectTask(mOnBluetoothAction);
//
//            }
////                mOnBluetoothAction.onConnectFailed("有正在连接的任务");
////                return;
//        }*/
////        mConnectTask2.onBluetoothAction = mOnBluetoothAction;
//        try {
//            mConnectTask2.execute(deviceMac);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void send(byte[] bytes) {
//        if (mConnectTask != null) mConnectTask.send(bytes);
//    }

    public boolean isBluetoothEnable() {
        return mBluetoothAdapter.isEnabled();
    }

    public void enableBluetooth() {
        mBluetoothAdapter.enable();
    }


}
