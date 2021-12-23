package com.zzh.dreamchaser.debugBT.tool;

import android.util.Log;

public class myLog {
    private static boolean mEnableLogOut = false;

    public static void setEnableLogOut() {
        mEnableLogOut = true;
    }
    public static void logD(String msg) {
        if (mEnableLogOut) Log.d("BLEUTILS", msg);
    }
}
