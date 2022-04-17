package com.zzh.dreamchaser.debugBT.connect;

import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;

import java.io.InputStream;

import static com.zzh.dreamchaser.debugBT.tool.byteCov.byte2Hex;
import static com.zzh.dreamchaser.debugBT.tool.byteCov.findFlag;
import static com.zzh.dreamchaser.debugBT.tool.myLog.logD;

public class RecvTask implements Runnable {
    BluetoothSocket bluetoothSocket;
    BLESPPUtils.OnDeviceRecvAction onDeviceRecvAction;
    boolean isRunning = false;
    byte[] startFlag = {(byte) 0xfa, (byte) 0xfa, (byte) 0xfa};
    byte[] stopFlag = "@\r\n".getBytes();
    private int byteLen = 0;

    public RecvTask(BluetoothSocket bluetoothSocket, BLESPPUtils.OnDeviceRecvAction onDeviceRecvAction) {
        this.bluetoothSocket = bluetoothSocket;
        this.onDeviceRecvAction = onDeviceRecvAction;
    }

    public void setByteLen(int byteLen) {
        this.byteLen = byteLen;
    }

    @Override
    public void run() {
        // 记录标志位，开始运行
        isRunning = true;
        // 开始监听数据接收
        try {
            InputStream inputStream = bluetoothSocket.getInputStream();
            int startFlagSize = startFlag.length;
            int stopFlagSize = stopFlag.length;
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
                        onDeviceRecvAction.onRecvFailed("接收数据单次失败：" + e.getMessage());
                        break;
                    }
                }
                try {
                    // 返回数据
                    logD("当前累计收到的数据=>" + byte2Hex(result));

                    boolean shouldCallOnReceiveBytes = false;
//                    logD("标志位为：" + byte2Hex(stopFlag));


                    while (true) {
                        int start = findFlag(begin_pos, result, startFlag, result.length, startFlagSize);
                        if (start==-1) {
                            logD("数据包搜寻:Start->" + start + ", End->break" );
                            break;
                        }
                        int end = findFlag(start + byteLen + startFlagSize + 1, result, stopFlag, result.length, stopFlagSize);
                        logD("数据包搜寻:Start->" + start + ", End->" + end);

                        if (end == -1) {
                            break;
                        } else if (end <= start) {
                            begin_pos = start;
                            continue;
                        } else {
                            int remsgSize = end - start - startFlagSize;
                            byte[] remsg = new byte[remsgSize];
                            System.arraycopy(result, start + startFlagSize, remsg, 0, remsgSize);
                            onDeviceRecvAction.onReceiveBytes(remsg);
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

                } catch (Exception e) {
                    e.printStackTrace();
                    onDeviceRecvAction.onRecvFailed("验证收到数据结束标志出错：" + e.getMessage());
                }
                Thread.sleep(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            onDeviceRecvAction.onRecvFailed("接收数据失败：" + e.getMessage());
        }
    }

    public void shutDown() {
        try {
            logD("RecvTask 开始释放资源");
            isRunning = false;
            bluetoothSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
