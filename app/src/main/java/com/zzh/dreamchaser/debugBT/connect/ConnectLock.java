package com.zzh.dreamchaser.debugBT.connect;

import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

import static com.zzh.dreamchaser.debugBT.MainActivity.BLsend;
import static com.zzh.dreamchaser.debugBT.tool.byteCov.i82Byte;

public class ConnectLock {
//    public static boolean Connected = false;
//    public static boolean onLogging = false;
//    public static class KeepAlive {
//        private static Timer timer = new Timer();
//        private static TimerTask task = new TimerTask() {
//            @Override
//            public void run() {
////                runOnUiThread(new ContentUpdate());
//            }
//        };
//        public static void start(){
//            timer.schedule(task, 1000, 1000);
//        }
//        public static void stop(){
//            timer.cancel();
//        }
//
//    }
    public static class HandShake {
        private static Thread thread;
        private static boolean running = false;
        public static void start() {
            running = true;
            thread = new Thread(() -> {
                while (running) {
                    Log.d("Connect:","尝试握手");
                    BLsend(i82Byte(0xf2));
                    BLsend(i82Byte(0xff));
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }, "HandShake");
            thread.start();
        }

        public static void stop() {
            running = false;
//            Thread t = thread;
//            thread = null;
//
//            try {
//                // Wait for the thread to exit
//                if (t != null && t.isAlive())
//                    t.join();
//            } catch (Exception e) {
//            }
        }
    }
//    public static void setOnLogging(boolean onLogging) {
//        ConnectLock.onLogging = onLogging;
//    }
//
//    public static boolean isConnected() {
//        return Connected;
//    }
//
//    public static boolean isOnLogging() {
//        return onLogging;
//    }
//
//    public static void setConnected(boolean connected) {
//        Connected = connected;
//    }
}
