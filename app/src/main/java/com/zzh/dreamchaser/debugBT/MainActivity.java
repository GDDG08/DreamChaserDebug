package com.zzh.dreamchaser.debugBT;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import static com.zzh.dreamchaser.debugBT.tool.byteCov.*;
import static com.zzh.dreamchaser.debugBT.ui.main.PlaceholderFragment.dAdapter;
import static com.zzh.dreamchaser.debugBT.ui.main.PlaceholderFragment.lvd;
import static com.zzh.dreamchaser.debugBT.ui.main.PlaceholderFragment.switch1;
import static com.zzh.dreamchaser.debugBT.ui.main.PlaceholderFragment.textView_fps;

import com.zzh.dreamchaser.debugBT.connect.BLESPPUtils;
import com.zzh.dreamchaser.debugBT.connect.ConnectLock;
import com.zzh.dreamchaser.debugBT.data.Content;
import com.zzh.dreamchaser.debugBT.data.ContentUpdate;
import com.zzh.dreamchaser.debugBT.data.Logger;
import com.zzh.dreamchaser.debugBT.ui.main.PlaceholderFragment;
import com.zzh.dreamchaser.debugBT.ui.main.SectionsPagerAdapter;
import com.zzh.dreamchaser.debugBT.databinding.ActivityMainBinding;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity implements BLESPPUtils.OnBluetoothAction {

    @SuppressLint("StaticFieldLeak")
    public static BLESPPUtils mBLESPPUtils;
    private ArrayList<BluetoothDevice> mDevicesList = new ArrayList<>();
    private DeviceDialogCtrl mDeviceDialogCtrl;

    boolean first_flag = true;
    public static final String PREFS_NAME = "com.zzh.dreamchaser.debugBT.color";

    public Content mContent;
    public ContentUpdate mContentupdate = new ContentUpdate();
    private static final int UPDATE = 0;

    public static Logger mLogger;

    static {
        mLogger = new Logger();
    }
//    private TimerTask refresh_task = new TimerTask() {
//        @Override
//        public void run() {
//            runOnUiThread(()->{
//                dAdapter.notifyDataSetChanged();
//                lvd.postInvalidate();
//            });
//        }
//
//    };

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == UPDATE) {
                MainActivity.onDataUpdate();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        com.zzh.dreamchaser.debugBT.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = binding.viewPager;
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = binding.tabs;
        tabs.setupWithViewPager(viewPager);

        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //Toast.makeText(MainActivity.this,tab.getPosition()+"",Toast.LENGTH_LONG).show();
                switch (tab.getPosition()) {
                    case PlaceholderFragment.Page_Info - 1:
                        break;
                    case PlaceholderFragment.Page_Tools - 1:
                        if (first_flag) {
//                            PlaceholderFragment.binding2.radioButton2.setChecked(true);
                            first_flag = false;
                        }
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        FloatingActionButton fab = binding.fab;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "正在连接", Snackbar.LENGTH_LONG).show();
//                mBLESPPUtils.connect("00:21:13:00:71:c3");
//                Toast.makeText(MainActivity.this,"正在连接",Toast.LENGTH_LONG);
                mDeviceDialogCtrl.show();
            }
        });


        initPermissions();
        initColors(this);
        mBLESPPUtils = new BLESPPUtils(MainActivity.this, this);
        mBLESPPUtils.setEnableLogOut();
//        mBLESPPUtils.setStopFlag("@\r\n".getBytes());
        if (!mBLESPPUtils.isBluetoothEnable()) mBLESPPUtils.enableBluetooth();
        mBLESPPUtils.onCreate();
        mDeviceDialogCtrl = new DeviceDialogCtrl(this);

//        ContentUpdate.start_tim(refresh_task);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBLESPPUtils.onDestroy();
        mLogger.stop();
    }

    private void initPermissions() {
        if (ContextCompat.checkSelfPermission(this, "android.permission-group.LOCATION") != 0) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            "android.permission.ACCESS_FINE_LOCATION",
                            "android.permission.ACCESS_COARSE_LOCATION",
                            "android.permission.ACCESS_WIFI_STATE",
                            "android.permission.WRITE_EXTERNAL_STORAGE"
                    },
                    1
            );
        }
    }

    public static void initColors(Context mContext) {
        SharedPreferences colorInfo = mContext.getSharedPreferences(MainActivity.PREFS_NAME, MODE_PRIVATE);
        if (!colorInfo.getBoolean("Init", false)) {
            SharedPreferences.Editor editor = colorInfo.edit();//获取Editor
            for (int i = 0; i < 3; i++) {
                int def = Color.rgb(0, 0, 0);
                switch (i) {
                    case 3:
                        def = Color.rgb(100, 0, 0);
                        break;
                    case 2:
                        def = Color.rgb(50, 50, 0);
                        break;
                    case 1:
                        def = Color.rgb(0, 100, 0);
                        break;
                }
                editor.putInt("light" + i, def);
            }
            editor.putBoolean("Init", true);
            editor.apply();
        }
    }

    @Override
    public void onFoundDevice(BluetoothDevice device) {

//        Toast.makeText(MainActivity.this, device.getName(),Toast.LENGTH_LONG).show();
        if (device.getName() == null || !device.getName().contains("RoboMaster"))
            return;
        // 判断是不是重复的
        for (int i = 0; i < mDevicesList.size(); i++) {
            if (mDevicesList.get(i).getAddress().equals(device.getAddress())) return;
        }
        // 添加，下次有就不显示了
        mDevicesList.add(device);
        // 添加条目到 UI 并设置点击事件
        mDeviceDialogCtrl.addDevice(device, new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                BluetoothDevice clickDevice = (BluetoothDevice) v.getTag();
                postShowToast("开始连接:" + clickDevice.getName());
//                mLogTv.setText(mLogTv.getText() + "\n" + "开始连接:" + clickDevice.getName());
                mBLESPPUtils.connect(clickDevice);
            }
        });
    }

    @Override
    public void onConnectSuccess(BluetoothDevice device) {
        postShowToast("连接成功", new DoSthAfterPost() {
            @SuppressLint("SetTextI18n")
            @Override
            public void doIt() {
//                mLogTv.setText(
//                        mLogTv.getText() + "\n连接成功:" + device.getName() + " | " + device.getAddress()
//                );
                ConnectLock.HandShake.start();
                mDeviceDialogCtrl.dismiss();
            }
        });
    }

    @Override
    public void onConnectFailed(String msg) {
        postShowToast("连接失败:" + msg);
    }

    int count = 0;
    boolean first_rec = true;
    boolean onLogging = false;

    @Override
    public void onReceiveBytes(byte[] bytes) {
        if (first_rec) {
            first_rec = false;
            return;
        }

//        Log.e("BLE","Receiving----->"+new String(bytes)+"");
        Log.e("BLE", "Receiving----->" + count++ + "--" + byte2Hex(bytes) + "");
        switch (bytes[0]) {
            case (byte) 0xff:
//                delete(mContent);
                ConnectLock.HandShake.stop();
//                Toast.makeText(MainActivity.this,"握手成功",Toast.LENGTH_LONG).show();
                mContent = new Content();
                mContent.CreatContent(bytes);
                onLogging = true;
//                ContentUpdate.start_tim(refresh_task);
                mLogger.writeHeader();
                BLsend(i82Byte(0xf1));
                break;
            case (byte) 0x01:
            case (byte) 0x02:
            case (byte) 0x03:
                if (onLogging) {
                    mContent.Update(bytes);
                    mLogger.runOnCall();
                    Message msg = new Message();
                    msg.what = UPDATE;
                    handler.sendMessage(msg);
//                    runOnUiThread(new ContentUpdate());
                }
                break;
        }
    }

    private static long time_fps = new Date().getTime();
    private static int count_fps = 0;

    private static void onDataUpdate() {
        count_fps++;
        if (count_fps > 100) {
            long time_now = new Date().getTime();
            textView_fps.setText((float) 1000 * count_fps / (time_now - time_fps) + "");
            count_fps = 0;
            time_fps = time_now;
        }

        dAdapter.notifyDataSetChanged();
        lvd.postInvalidate();
    }

    @Override
    public void onSendBytes(byte[] bytes) {
        Log.e("BLE", "Sending----->" + byte2Hex(bytes));
    }

    @Override
    public void onFinishFoundDevice() {
        Toast.makeText(this, "搜索已暂停", Toast.LENGTH_SHORT).show();
    }

    /**
     * 设备选择对话框控制
     */
    private class DeviceDialogCtrl {
        private LinearLayout mDialogRootView;
        private ProgressBar mProgressBar;
        private AlertDialog mConnectDeviceDialog;

        DeviceDialogCtrl(Context context) {
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
        void show() {
            mBLESPPUtils.startDiscovery();

            mConnectDeviceDialog.show();
            mConnectDeviceDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnLongClickListener(v -> {
                mConnectDeviceDialog.dismiss();
                return false;
            });
            mConnectDeviceDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                mConnectDeviceDialog.dismiss();
                finish();
            });
            mConnectDeviceDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(v -> {
                mDialogRootView.removeAllViews();
//                    mProgressBar.setProgress(50,true);
                mProgressBar.setIndeterminate(true);
                mDialogRootView.addView(mProgressBar);
                mDevicesList.clear();
                mBLESPPUtils.startDiscovery();
            });
        }

        /**
         * 取消对话框
         */
        void dismiss() {
            mConnectDeviceDialog.dismiss();
        }

        /**
         * 添加一个设备到列表
         *
         * @param device          设备
         * @param onClickListener 点击回调
         */
        private void addDevice(final BluetoothDevice device, final View.OnClickListener onClickListener) {
            runOnUiThread(() -> {
                TextView devTag = new TextView(MainActivity.this);
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

    private void postShowToast(final String msg) {
        postShowToast(msg, null);
    }

    private void postShowToast(final String msg, final DoSthAfterPost doSthAfterPost) {
        runOnUiThread(() -> {
            Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
            if (doSthAfterPost != null) doSthAfterPost.doIt();
        });
    }

    private interface DoSthAfterPost {
        void doIt();
    }

    public static void BLsend(String str) {
        mBLESPPUtils.send(str.getBytes());
    }

    public static void BLsend(byte[] b) {
        mBLESPPUtils.send(b);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == Activity.RESULT_OK) {//是否选择，没选择就不会继续
                Uri uri = data.getData();//得到uri，后面就是将uri转化成file的过程。
                mLogger.file = uri.toString();

                mLogger.start(this);
                Toast.makeText(this, mLogger.file.toString(), Toast.LENGTH_SHORT).show();
                SharedPreferences.Editor info_edit = getSharedPreferences(MainActivity.PREFS_NAME, MODE_PRIVATE).edit();
                info_edit.putString("LogFile", mLogger.file.toString());
                info_edit.apply();
                switch1.setChecked(true);
            }
        }
    }
}