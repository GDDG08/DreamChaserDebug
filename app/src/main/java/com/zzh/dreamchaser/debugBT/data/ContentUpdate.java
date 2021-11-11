package com.zzh.dreamchaser.debugBT.data;

import static com.zzh.dreamchaser.debugBT.ui.main.PlaceholderFragment.dAdapter;
import static com.zzh.dreamchaser.debugBT.ui.main.PlaceholderFragment.lvd;

public class ContentUpdate implements Runnable {
    Thread t = new Thread(this, "update_lvd");
    boolean isRunning = true;
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
