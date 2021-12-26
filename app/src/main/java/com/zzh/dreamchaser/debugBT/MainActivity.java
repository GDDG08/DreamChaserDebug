package com.zzh.dreamchaser.debugBT;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

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
import static com.zzh.dreamchaser.debugBT.tool.myLog.setEnableLogOut;
import static com.zzh.dreamchaser.debugBT.ui.main.PlaceholderFragment.dAdapter;
import static com.zzh.dreamchaser.debugBT.ui.main.PlaceholderFragment.lvd;
import static com.zzh.dreamchaser.debugBT.ui.main.PlaceholderFragment.switch1;
import static com.zzh.dreamchaser.debugBT.ui.main.PlaceholderFragment.textView_fps;

import com.zzh.dreamchaser.debugBT.connect.BLESPPUtils;
import com.zzh.dreamchaser.debugBT.connect.DeviceHandle;
import com.zzh.dreamchaser.debugBT.connect.DeviceList;
import com.zzh.dreamchaser.debugBT.data.Content;
//import com.zzh.dreamchaser.debugBT.data.ContentUpdate;
import com.zzh.dreamchaser.debugBT.data.Logger;
import com.zzh.dreamchaser.debugBT.tool.FileUtils;
import com.zzh.dreamchaser.debugBT.ui.main.PlaceholderFragment;
import com.zzh.dreamchaser.debugBT.ui.main.SectionsPagerAdapter;
import com.zzh.dreamchaser.debugBT.databinding.ActivityMainBinding;
import com.zzh.dreamchaser.debugBT.view.DeviceDialog;

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
    private DeviceDialog mDeviceDialogCtrl;
    public static ArrayList<BluetoothDevice> mDevicesList = new ArrayList<>();

    boolean first_flag = true;
    public static final String PREFS_NAME = "com.zzh.dreamchaser.debugBT.color";

    //    public ContentUpdate mContentupdate = new ContentUpdate();
    private static final int UPDATE = 0;

    public static Logger mLogger;

    static {
        mLogger = new Logger();
    }

//    @SuppressLint("HandlerLeak")
//    private Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            DeviceList.targetDevices.get(msg.what).onUIUpdate();
//        }
//    };

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
                PlaceholderFragment.switch2.setChecked(false);
                mDeviceDialogCtrl.show();
            }
        });


        initPermissions();
        mBLESPPUtils = new BLESPPUtils(MainActivity.this, this);
        setEnableLogOut();
//        mBLESPPUtils.setStopFlag("@\r\n".getBytes());
        if (!mBLESPPUtils.isBluetoothEnable()) mBLESPPUtils.enableBluetooth();
        mBLESPPUtils.onCreate();
        mDeviceDialogCtrl = new DeviceDialog(this, mBLESPPUtils);
        DeviceList.setOnBluetoothAction(this);
    }

    private void initPermissions() {
        if (ContextCompat.checkSelfPermission(this, "android.permission-group.LOCATION") != 0) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            "android.permission.ACCESS_FINE_LOCATION",
                            "android.permission.ACCESS_COARSE_LOCATION",
                            "android.permission.ACCESS_WIFI_STATE",
                            "android.permission.WRITE_EXTERNAL_STORAGE",
                            "android.permission.READ_EXTERNAL_STORAGE"
                    },
                    1
            );
        }
        if (Build.VERSION.SDK_INT >= 23) {
            int REQUEST_CODE_PERMISSION_STORAGE = 1;
            String[] permissions = {
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            };

            for (String str : permissions) {
                if (this.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                    this.requestPermissions(permissions, REQUEST_CODE_PERMISSION_STORAGE);
                    return;
                }
            }
        }
    }

    @Override
    public void onFoundDevice(BluetoothDevice device) {

//        Toast.makeText(MainActivity.this, device.getName(),Toast.LENGTH_LONG).show();
        if (!(device.getName() == null || device.getName().contains("Robo") || device.getName().contains("RM")))
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
//                mBLESPPUtils.connect(clickDevice);
                DeviceList.connect(MainActivity.this, clickDevice.getAddress());
            }
        });
    }

    @Override
    public void onConnectSuccess(BluetoothDevice device, BluetoothSocket socket) {
        Log.d("DOUBLE", "连接成功" + device.getName() + device.getAddress());
        postShowToast(device.getName() + "(" + device.getAddress() + ")\n连接成功!", () -> {
//            mDeviceDialogCtrl.dismiss();
        });
    }

    @Override
    public void onConnectFailed(String deviceMac, String msg) {
        postShowToast("连接失败:" + msg);
        postShowToast(msg, () -> {
            new AlertDialog.Builder(this)
                    .setTitle("是否重连")
                    .setMessage(msg)
                    .setNegativeButton("取消", (view, which) -> {
                        DeviceList.removeDevice(deviceMac);
//                                    DeviceList.targetDevices.remove(DeviceList.getDeviceHandle(deviceMac));
                    })
                    .setPositiveButton("重试", (view, which) -> {
                        DeviceList.getDeviceHandle(deviceMac).connect();
                    })
                    .show();
        });

    }

    @Override
    public void onReceiveBytes(int id, byte[] bytes) {
        Log.d("Receiving1----->", "设备" + id + ":" + new String(bytes));
        switch (bytes[0]) {
            case (byte) 0xff:

                break;
            case (byte) 0x01:
            case (byte) 0x02:
            case (byte) 0x03:
            default:
                mLogger.runOnCall();
//                Message msg = new Message();
//                msg.what = id;
//                handler.sendMessage(msg);
                DeviceList.targetDevices.get(id).onUIUpdate();

                break;
        }
    }

    @Override
    public void onSendBytes(int id, byte[] bytes) {
        Log.e("BLE", "Sending----->" + byte2Hex(bytes));
    }

    @Override
    public void onFinishFoundDevice() {
        Toast.makeText(this, "搜索已暂停", Toast.LENGTH_SHORT).show();
    }

    public void postShowToast(final String msg) {
        postShowToast(msg, null);
    }

    public void postShowToast(final String msg, final DoSthAfterPost doSthAfterPost) {
        runOnUiThread(() -> {
            Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
            if (doSthAfterPost != null) doSthAfterPost.doIt();
        });
    }


    private interface DoSthAfterPost {
        void doIt();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == Activity.RESULT_OK) {//是否选择，没选择就不会继续
                FileUtils fu = new FileUtils(this);
                Uri uri = data.getData();//得到uri，后面就是将uri转化成file的过程。
                mLogger.file = fu.getFilePathByUri(uri);

                mLogger.start(this);
                Toast.makeText(this, mLogger.file, Toast.LENGTH_SHORT).show();
                SharedPreferences.Editor info_edit = getSharedPreferences(MainActivity.PREFS_NAME, MODE_PRIVATE).edit();
                info_edit.putString("LogFile", mLogger.file);
                info_edit.apply();

                PlaceholderFragment.switch1.setChecked(true);
                String filename[] = mLogger.file.split("/");
                PlaceholderFragment.textView_file.setText(filename[filename.length - 1]);
            }
        }
    }

    private static Boolean isExit = false;

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        Timer tExit = null;
        if (isExit == false) {
            isExit = true; // 准备退出
            Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false;
                }
            }, 2000);
        } else {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
        }
    }

    @Override
    protected void onDestroy() {
        mBLESPPUtils.onDestroy();
        mLogger.stop();
        DeviceList.removeAll();
        super.onDestroy();
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        for (DeviceHandle dh : DeviceList.targetDevices)
            if (dh.dAdapter != null)
                if (dh.dAdapter.onScope) {
                    dh.dAdapter.setOnScope(false, false);
                    dh.dAdapter.pauseShow = true;
                }
    }

    @Override
    protected void onResume() {
        super.onResume();
        for (DeviceHandle dh : DeviceList.targetDevices)
            if (dh.dAdapter != null)
                if (dh.dAdapter.pauseShow) {
                    dh.dAdapter.setOnScope(true, false);
                    dh.dAdapter.pauseShow = false;
                }
    }
}