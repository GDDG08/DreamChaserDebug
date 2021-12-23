package com.zzh.dreamchaser.debugBT.view;


import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.zzh.dreamchaser.debugBT.MainActivity;
import com.zzh.dreamchaser.debugBT.R;
import com.zzh.dreamchaser.debugBT.connect.BLESPPUtils;

import java.util.ArrayList;

public class DeviceDialog{
    private LinearLayout mDialogRootView;
    private ProgressBar mProgressBar;
    private AlertDialog mConnectDeviceDialog;

    private Activity mContext;
    private BLESPPUtils mBLESPPUtils;


    public DeviceDialog(Activity context, BLESPPUtils mBLESPPUtils) {
        this.mContext = context;
        this.mBLESPPUtils = mBLESPPUtils;
        // 搜索进度条
        mProgressBar = new ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal);
        mProgressBar.setLayoutParams(
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        50
                )
        );
        mProgressBar.setIndeterminate(true);

        // 根布局
        mDialogRootView = new LinearLayout(context);
        mDialogRootView.setOrientation(LinearLayout.VERTICAL);
        mDialogRootView.addView(mProgressBar);
        mDialogRootView.setMinimumHeight(700);

        // 容器布局
        ScrollView scrollView = new ScrollView(context);
        scrollView.addView(mDialogRootView,
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        700
                )
        );

        // 构建对话框
        mConnectDeviceDialog = new AlertDialog
                .Builder(context)
                .setNegativeButton("刷新", null)
                .setPositiveButton("退出", null)
                .create();
        mConnectDeviceDialog.setTitle("选择RoboMaster调试器");
        mConnectDeviceDialog.setView(scrollView);
        mConnectDeviceDialog.setCancelable(true);
    }

    /**
     * 显示并开始搜索设备
     */
    public void show() {
        mBLESPPUtils.startDiscovery();

        mConnectDeviceDialog.show();
        mConnectDeviceDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnLongClickListener(v -> {
            mConnectDeviceDialog.dismiss();
            return false;
        });
        mConnectDeviceDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            mConnectDeviceDialog.dismiss();
            mContext.finish();
        });
        mConnectDeviceDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(v -> {
            mDialogRootView.removeAllViews();
//                    mProgressBar.setProgress(50,true);
            mProgressBar.setIndeterminate(true);
            mDialogRootView.addView(mProgressBar);
            MainActivity.mDevicesList.clear();
            mBLESPPUtils.startDiscovery();
        });
    }

    public void dismiss() {
        mConnectDeviceDialog.dismiss();
    }

    /**
     * 添加一个设备到列表
     *
     * @param device          设备
     * @param onClickListener 点击回调
     */
    public void addDevice(final BluetoothDevice device, final View.OnClickListener onClickListener) {
        mContext.runOnUiThread(() -> {
            TextView devTag = new TextView(mContext);
            devTag.setClickable(true);
            devTag.setPadding(20, 20, 20, 20);
            devTag.setBackgroundResource(R.drawable.rect_round_button_ripple);
            devTag.setText(device.getName() + "\nMAC:" + device.getAddress());
            devTag.setTextColor(Color.WHITE);
            devTag.setOnClickListener(onClickListener);
            devTag.setTag(device);
            devTag.setLayoutParams(
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    )
            );
            ((LinearLayout.LayoutParams) devTag.getLayoutParams()).setMargins(
                    20, 20, 20, 20);
            mDialogRootView.addView(devTag);
        });
    }
}
