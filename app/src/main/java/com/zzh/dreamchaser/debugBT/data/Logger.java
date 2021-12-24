package com.zzh.dreamchaser.debugBT.data;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.zzh.dreamchaser.debugBT.MainActivity;
import com.zzh.dreamchaser.debugBT.connect.DeviceHandle;
import com.zzh.dreamchaser.debugBT.connect.DeviceList;
import com.zzh.dreamchaser.debugBT.ui.main.PlaceholderFragment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;

public class Logger {
    public boolean onLogging = false;
    public String file = null;
    public BufferedWriter bw;

    public void init(Activity activity) {
        SharedPreferences info = activity.getSharedPreferences(MainActivity.PREFS_NAME, MODE_PRIVATE);
        String addr = info.getString("LogFile", null);
        if (addr != null) {
            AlertDialog.Builder msg = new AlertDialog.Builder(activity)
                    .setTitle("使用上次文件?")
                    .setMessage(addr)
                    .setCancelable(true)
                    .setPositiveButton("继续",
                            (dialog, which) -> {
                                file = addr;
                                start(activity);
                                PlaceholderFragment.switch1.setChecked(true);
                                String filename[] = file.split("/");
                                PlaceholderFragment.textView_file.setText(filename[filename.length - 1]);
                            })
                    .setNegativeButton("新建文件",
                            (dialog, which) -> {
                                selectPath(activity);
                            });
            msg.show();
        } else {
            selectPath(activity);
        }
    }

    private void selectPath(Activity activity) {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/csv");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        intent.putExtra(Intent.EXTRA_TITLE, sdf.format(new Date().getTime()) + ".csv");
        activity.startActivityForResult(intent, 0);
    }

    public void start(Context context) {
        try {
            File f = new File(file);
            bw = new BufferedWriter(new FileWriter(f, true));
//            Uri uri = Uri.parse(file);
////            Format
//            OutputStream os = context.getContentResolver().openOutputStream(uri);
//            bw = new BufferedWriter(new OutputStreamWriter(os));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            onLogging = true;
        }
    }

    public void stop() {
        onLogging = false;
        if (bw != null) {
            try {
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void runOnCall() {

        if (onLogging) {
            try {
                for (DeviceHandle dh : DeviceList.targetDevices)
                    if (dh.mContent != null)
                        for (int i = 0; i < dh.mContent.dataLen; i++) {
                            bw.write(((Var) dh.mContent.list.get(i)).getStr() + "");
                            if (i != dh.mContent.dataLen - 1)
                                bw.write(",");
                        }
                //Todo:time
                bw.write("\r\n");
                bw.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void writeHeader() {
        try {
            for (DeviceHandle dh : DeviceList.targetDevices)
                if (dh.mContent != null)
                    for (int i = 0; i < dh.mContent.dataLen; i++) {
                        bw.write(dh.deviceName+"-"+dh.mContent.tagList.get(i) + ",");
                    }
            bw.write("\r\n");
            bw.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
