package com.zzh.dreamchaser.debugBT.connect;

import android.util.Log;

import static com.zzh.dreamchaser.debugBT.tool.byteCov.i82Byte;
import static com.zzh.dreamchaser.debugBT.tool.myLog.logD;

public class HandShake {
    private Thread thread;
    private boolean running = false;

    public HandShake(DeviceHandle dh) {
        thread = new Thread(() -> {
            while (running) {
                logD("Connect:" + dh.deviceName + "尝试握手");
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                dh.sendData(i82Byte(0xf2));
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                dh.sendData(i82Byte(0xff));

            }
        }, "HandShake");
    }

    public void start() {
        running = true;
        thread.start();
    }

    public void stop() {
        running = false;
    }
}
