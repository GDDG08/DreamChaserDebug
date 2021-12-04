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


/**
 * 蓝牙工具类 2020/5/18
 * 功能：搜索蓝牙设备
 * 连接蓝牙串口
 * 发送串口数据
 * 接收串口数据
 *
 * @author gtf35 gtf@gtf35.top
 */
public class BLESPPUtils {
    private static boolean mEnableLogOut = false;
    private Context mContext;
    private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private OnBluetoothAction mOnBluetoothAction;
    private ConnectTask mConnectTask = new ConnectTask();

    /**
     * 搜索到新设备广播广播接收器
     */
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (mOnBluetoothAction != null) mOnBluetoothAction.onFoundDevice(device);
            }
        }
    };

    /**
     * 搜索结束广播接收器
     */
    private final BroadcastReceiver mFinishFoundReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                if (mOnBluetoothAction != null) mOnBluetoothAction.onFinishFoundDevice();
            }
        }
    };

    /**
     * 连接任务
     */
    private static class ConnectTask extends AsyncTask<String, Byte[], Void> {
        private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothSocket bluetoothSocket;
        BluetoothDevice romoteDevice;
        OnBluetoothAction onBluetoothAction;
        boolean isRunning = false;
        byte[] startFlag = {(byte) 0xfa, (byte) 0xfa, (byte) 0xfa};
        byte[] stopFlag = "@\r\n".getBytes();

        @Override
        protected Void doInBackground(String... bluetoothDevicesMac) {
            // 记录标志位，开始运行
            isRunning = true;

            // 尝试获取 bluetoothSocket
            try {
                UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
                romoteDevice = bluetoothAdapter.getRemoteDevice(bluetoothDevicesMac[0]);
                bluetoothSocket = romoteDevice.createRfcommSocketToServiceRecord(SPP_UUID);
            } catch (Exception e) {
                logD("获取Socket失败");
                isRunning = false;
                e.printStackTrace();
                return null;
            }

            // 检查有没有获取到
            if (bluetoothSocket == null) {
                onBluetoothAction.onConnectFailed("连接失败:获取Socket失败");
                isRunning = false;
                return null;
            }

            while(true) {
                // 尝试连接
                try {
                    // 等待连接，会阻塞线程
                    bluetoothSocket.connect();
                    logD("连接成功");
                    onBluetoothAction.onConnectSuccess(romoteDevice);
                    break;
                } catch (Exception connectException) {
                    connectException.printStackTrace();
                    logD("连接失败:" + connectException.getMessage());
//                    onBluetoothAction.onConnectFailed("连接失败:" + connectException.getMessage());
                    onBluetoothAction.onConnectFailed("超时，正在重试……");
//                    return null;
                }
            }

            // 开始监听数据接收
            try {
                InputStream inputStream = bluetoothSocket.getInputStream();
                byte[] result = new byte[0];
                int begin_pos = 0;
                while (isRunning) {
                    logD("looping");
                    byte[] buffer = new byte[256];
                    // 等待有数据
                    while (inputStream.available() == 0 && isRunning) {
                        if (System.currentTimeMillis() < 0) break;
                    }
                    while (isRunning) {
                        try {
                            int num = inputStream.read(buffer);
//                            logD("LEN: "+num);
                            byte[] temp = new byte[result.length + num];
                            System.arraycopy(result, 0, temp, 0, result.length);
                            System.arraycopy(buffer, 0, temp, result.length, num);
                            result = temp;
                            if (inputStream.available() == 0) break;
                        } catch (Exception e) {
                            e.printStackTrace();
                            onBluetoothAction.onConnectFailed("接收数据单次失败：" + e.getMessage());
                            break;
                        }
                    }
                    try {
                        // 返回数据
                        logD("当前累计收到的数据=>" + byte2Hex(result));
                        int startFlagSize = startFlag.length;
                        int stopFlagSize = stopFlag.length;
                        boolean shouldCallOnReceiveBytes = false;
                        logD("标志位为：" + byte2Hex(stopFlag));


                        while (true) {
                            int start = findFlag(begin_pos, result, startFlag, result.length, startFlagSize);
                            int end = findFlag(begin_pos, result, stopFlag, result.length, stopFlagSize);
                            logD("数据包搜寻:Start->" + start + ", End->" + end);

                            if (start == -1 || end == -1) {
                                break;
                            } else if (end <= start) {
                                begin_pos = start;
                                continue;
                            } else {
                                int remsgSize = end - start - startFlagSize;
                                byte[] remsg = new byte[remsgSize];
                                System.arraycopy(result, start + startFlagSize, remsg, 0, remsgSize);
                                onBluetoothAction.onReceiveBytes(remsg);
                                begin_pos = end + stopFlagSize;
                            }
                        }

                        if (begin_pos > 200) {
                            int reSize = result.length - begin_pos;
                            byte[] temp = new byte[reSize];
                            System.arraycopy(result, begin_pos, temp, 0, reSize);
                            result = temp;
                            begin_pos = 0;
                        } else if (begin_pos == 0 && result.length > 10000) {
                            result = new byte[0];
                        }

//                        for (int i = stopFlagSize - 1; i >= 0; i--) {
//                            int indexInResult = result.length - (stopFlagSize - i);
//                            if (indexInResult >= result.length || indexInResult < 0) {
//                                shouldCallOnReceiveBytes = false;
//                                logD("收到的数据比停止字符串短");
//                                break;
//                            }
//                            if (stopFlag[i] == result[indexInResult]) {
//                                logD("发现" + byte2Hex(stopFlag[i]) + "等于" + byte2Hex(result[indexInResult]));
//                                shouldCallOnReceiveBytes = true;
//                            } else {
//                                logD("发现" + byte2Hex(stopFlag[i]) + "不等于" + byte2Hex(result[indexInResult]));
//                                shouldCallOnReceiveBytes = false;
//                                break;
//                            }
//                        }
//                        if (shouldCallOnReceiveBytes) {
//                            onBluetoothAction.onReceiveBytes(result);
//                            // 清空
//                            result = new byte[0];
//                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                        onBluetoothAction.onConnectFailed("验证收到数据结束标志出错：" + e.getMessage());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                onBluetoothAction.onConnectFailed("接收数据失败：" + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onCancelled() {
            try {
                logD("AsyncTask 开始释放资源");
                isRunning = false;
                bluetoothSocket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * 发送
         *
         * @param msg 内容
         */
        void send(byte[] msg) {
            try {
                bluetoothSocket.getOutputStream().write(msg);
                onBluetoothAction.onSendBytes(msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 设置停止标志位字符串
     *
     * @param flag 停止位字符串
     */
    @SuppressWarnings("SameParameterValue")
    void setStopFlag(byte[] flag) {
        mConnectTask.stopFlag = flag;
    }

    /**
     * 蓝牙活动回调
     */
    public interface OnBluetoothAction {
        /**
         * 当发现新设备
         *
         * @param device 设备
         */
        void onFoundDevice(BluetoothDevice device);

        /**
         * 当连接成功
         */
        void onConnectSuccess(BluetoothDevice device);

        /**
         * 当连接失败
         *
         * @param msg 失败信息
         */
        void onConnectFailed(String msg);

        /**
         * 当接收到 byte 数组
         *
         * @param bytes 内容
         */
        void onReceiveBytes(byte[] bytes);

        /**
         * 当调用接口发送了 byte 数组
         *
         * @param bytes 内容
         */
        void onSendBytes(byte[] bytes);

        /**
         * 当结束搜索设备
         */
        void onFinishFoundDevice();
    }

    /**
     * 构造蓝牙工具
     *
     * @param context           上下文
     * @param onBluetoothAction 蓝牙状态改变回调
     */
    public BLESPPUtils(Context context, OnBluetoothAction onBluetoothAction) {
        mContext = context;
        mOnBluetoothAction = onBluetoothAction;
    }

    /**
     * 初始化
     */
    public void onCreate() {
        IntentFilter foundFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        mContext.registerReceiver(mReceiver, foundFilter);
        IntentFilter finishFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        mContext.registerReceiver(mFinishFoundReceiver, finishFilter);
    }

    /**
     * 销毁，释放资源
     */
    public void onDestroy() {
        try {
            logD("onDestroy，开始释放资源");
            mConnectTask.isRunning = false;
            mConnectTask.cancel(true);
            mContext.unregisterReceiver(mReceiver);
            mContext.unregisterReceiver(mFinishFoundReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 开始搜索
     */
    public void startDiscovery() {
        if (mBluetoothAdapter.isDiscovering()) mBluetoothAdapter.cancelDiscovery();
        mBluetoothAdapter.startDiscovery();
    }

    /**
     * 使用搜索到的数据连接
     *
     * @param device 设备
     */
    public void connect(BluetoothDevice device) {
        mBluetoothAdapter.cancelDiscovery();
        connect(device.getAddress());
    }

    /**
     * 使用Mac地址来连接
     *
     * @param deviceMac 要连接的设备的 MAC
     */
    void connect(String deviceMac) {
        if (mConnectTask.getStatus() == AsyncTask.Status.RUNNING && mConnectTask.isRunning) {
            if (mOnBluetoothAction != null){
                onDestroy();
                mConnectTask = new ConnectTask();

            }
//                mOnBluetoothAction.onConnectFailed("有正在连接的任务");
//                return;
        }
        mConnectTask.onBluetoothAction = mOnBluetoothAction;
        try {
            mConnectTask.execute(deviceMac);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送 byte 数组到串口
     *
     * @param bytes 要发送的数据
     */
    public void send(byte[] bytes) {
        if (mConnectTask != null) mConnectTask.send(bytes);
    }

    /**
     * 获取用户是否打开了蓝牙
     */
    public boolean isBluetoothEnable() {
        return mBluetoothAdapter.isEnabled();
    }

    /**
     * 开启蓝牙
     */
    public void enableBluetooth() {
        mBluetoothAdapter.enable();
    }

    /**
     * 启用日志输出
     */
    @SuppressWarnings("unused")
    public static void setEnableLogOut() {
        mEnableLogOut = true;
    }

    /**
     * 打印日志
     */
    private static void logD(String msg) {
        if (mEnableLogOut) Log.d("BLEUTILS", msg);
    }
}
