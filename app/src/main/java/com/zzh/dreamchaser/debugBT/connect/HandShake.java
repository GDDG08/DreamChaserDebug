package com.zzh.dreamchaser.debugBT.connect;

import android.util.Log;

import static com.zzh.dreamchaser.debugBT.tool.byteCov.i82Byte;

public class HandShake {
    private Thread thread;
    private boolean running = false;

    public HandShake(DeviceHandle dh) {
        thread = new Thread(() -> {
            while (running) {
                Log.d("Connect:", dh.deviceName + "尝试握手");
                dh.sendData(i82Byte(0xf2));
                dh.sendData(i82Byte(0xff));
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
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
