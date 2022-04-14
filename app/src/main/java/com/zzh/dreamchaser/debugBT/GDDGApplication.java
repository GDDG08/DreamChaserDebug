package com.zzh.dreamchaser.debugBT;

import android.app.Application;

import com.zzh.dreamchaser.debugBT.tool.CrashHandler;
import com.zzh.dreamchaser.debugBT.tool.VersionControl;

import org.xutils.x;

public class GDDGApplication extends Application {

    public static VersionControl verCtrl;

    @Override
    public void onCreate() {
        super.onCreate();
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());
        x.Ext.init(this);

        verCtrl = new VersionControl(getApplicationContext());
    }
}
