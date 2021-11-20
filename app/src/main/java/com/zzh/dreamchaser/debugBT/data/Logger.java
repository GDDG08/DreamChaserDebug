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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
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
            Uri uri = Uri.parse(file);
            OutputStream os = context.getContentResolver().openOutputStream(uri);
            bw = new BufferedWriter(new OutputStreamWriter(os));
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
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
                for (int i = 0; i < Content.dataLen; i++) {
                    bw.write(((Var) Content.list.get(i)).getStr() + "");
                    if (i != Content.dataLen - 1)
                        bw.write(",");
                }
                bw.write("\r\n");
                bw.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void writeHeader() {
        try {
            for (int i = 0; i < Content.dataLen; i++) {
                bw.write(Content.tagList.get(i) + "");
                if (i != Content.dataLen - 1)
                    bw.write(",");
            }
            bw.write("\r\n");
            bw.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
