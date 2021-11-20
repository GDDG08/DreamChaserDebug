package com.zzh.dreamchaser.debugBT.data;

import com.zzh.dreamchaser.debugBT.MainActivity;

import java.util.Timer;
import java.util.TimerTask;

import static com.zzh.dreamchaser.debugBT.ui.main.PlaceholderFragment.dAdapter;
import static com.zzh.dreamchaser.debugBT.ui.main.PlaceholderFragment.lvd;

public class ContentUpdate implements Runnable {
    Thread t = new Thread(this, "update_lvd");
    boolean isRunning = true;

    private static Timer timer = new Timer();
    public static void start_tim(TimerTask task){
        timer.schedule(task, 1000, 10);
    }

    public void start() {
        isRunning = true;
        t.start();

    }
    public void stop() {
        isRunning = false;
    }
    @Override
    public void run() {
//        while(isRunning) {
            dAdapter.notifyDataSetChanged();
            lvd.postInvalidate();
//            try {
//                Thread.sleep(100);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
    }
}
