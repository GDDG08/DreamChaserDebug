package com.zzh.dreamchaser.debugBT.connect;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zzh.dreamchaser.debugBT.ScopeActivity;
import com.zzh.dreamchaser.debugBT.data.Content;
import com.zzh.dreamchaser.debugBT.data.ContentAdapter;
import com.zzh.dreamchaser.debugBT.view.MyListView;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Date;

import static com.zzh.dreamchaser.debugBT.MainActivity.mLogger;
import static com.zzh.dreamchaser.debugBT.connect.DeviceList.executorSvcConnect;
import static com.zzh.dreamchaser.debugBT.connect.DeviceList.executorSvcRecv;
import static com.zzh.dreamchaser.debugBT.tool.byteCov.byte2Hex;
import static com.zzh.dreamchaser.debugBT.tool.byteCov.i82Byte;
import static com.zzh.dreamchaser.debugBT.tool.myLog.logD;
import static com.zzh.dreamchaser.debugBT.ui.main.PlaceholderFragment.binding1;

public class DeviceHandle {

    public int id = -1;
    private boolean isConnected = false;
    private boolean hasUI = false;
    public String deviceMAC, deviceName;
    public ConnectTask connectTask;
    public RecvTask recvTask;
    public HandShake handShake = new HandShake(this);
    public Content mContent = new Content(true);
    private Activity activity;

    BluetoothSocket bluetoothSocket;
    BLESPPUtils.OnBluetoothAction onBluetoothAction;
    BLESPPUtils.OnDeviceConnectAction onDeviceConnectAction = new BLESPPUtils.OnDeviceConnectAction() {
        @Override
        public void onConnectSuccess(BluetoothDevice device, BluetoothSocket socket) {
            deviceName = device.getName();
            bluetoothSocket = socket;
            isConnected = true;
            recvTask = new RecvTask(socket, onDeviceRecvAction);
            executorSvcRecv.submit(recvTask);
            onBluetoothAction.onConnectSuccess(device, socket);
            handShake.start();
        }

        @Override
        public void onConnectFailed(String msg) {
            onBluetoothAction.onConnectFailed(deviceMAC, msg);
        }
    };
    BLESPPUtils.OnDeviceRecvAction onDeviceRecvAction = new BLESPPUtils.OnDeviceRecvAction() {

        @Override
        public void onReceiveBytes(byte[] bytes) {
            logD("BLE" + "Receiving----->" + byte2Hex(bytes) + "");
            boolean updateSucc = false;
            switch (bytes[0]) {
                case (byte) 0xff:
                    handShake.stop();
                    mContent = new Content(false);
                    mContent.CreatContent(bytes);
                    recvTask.setByteLen(mContent.byteLen);
                    if (!hasUI)
                        onCreateUI();
                    //Todo: start logging
//                onLogging = true;
                    mLogger.writeHeader();
                    sendData(i82Byte(0xf1));
                    break;
                case (byte) 0x01:
                case (byte) 0x02:
                case (byte) 0x03:
                    if (hasUI)
                        updateSucc = mContent.Update(bytes);
                default:
                    if (updateSucc)
                        onBluetoothAction.onReceiveBytes(id, bytes);
                    break;
            }

        }

        @Override
        public void onRecvFailed(String msg) {
//            onBluetoothAction.onConnectFailed(msg);
        }
    };

    public DeviceHandle(int id, String deviceMAC, BLESPPUtils.OnBluetoothAction onBluetoothAction, Activity activity) {
        this.id = id;
        this.deviceMAC = deviceMAC;
        this.onBluetoothAction = onBluetoothAction;
        this.activity = activity;

        if (deviceMAC == "DEMO")
            onCreateUI();
    }

    public void connect() {
        logD("connect:" + checkIsConnected());
        if (!checkIsConnected()) {
            connectTask = new ConnectTask(deviceMAC, onDeviceConnectAction);
//        new Thread(connectTask).run();
            executorSvcConnect.submit(connectTask);
        }
    }

    public void onDisConnect() {
        isConnected = false;
        onDestroyUI();
        try {
            recvTask.shutDown();
            connectTask.shutDown();
            handShake.stop();
            bluetoothSocket.close();
            onBluetoothAction.onConnectFailed(deviceMAC, deviceName + "(" + deviceMAC + ")\n已断开连接！");
        } catch (NullPointerException | IOException e) {
            e.printStackTrace();
        }
    }

    public boolean checkIsConnected() {
        return isConnected;
    }


    public void sendData(byte[] msg) {
        if (!checkIsConnected()) {
            logD("DeviceHandle--->" + "Sending failed as no connection found.");
            return;
        }
        try {
            bluetoothSocket.getOutputStream().write(msg);
            onBluetoothAction.onSendBytes(id, msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private long time_fps = new Date().getTime();
    private int count_fps = 0;

    public MyListView lvd;
    public TextView textView_name;
    public TextView textView_fps;
    public ContentAdapter dAdapter;

    public void onCreateUI() {
        hasUI = true;
        textView_name = new TextView(activity);
        textView_name.setText(deviceName);
        textView_name.setTextColor(Color.WHITE);
        LinearLayout.LayoutParams nameLl = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        nameLl.setMargins(0, 20, 0, 0);
        nameLl.gravity = Gravity.CENTER;
        textView_name.setLayoutParams(nameLl);

        textView_fps = new TextView(activity);
        textView_fps.setText("FPS");
        textView_fps.setTextColor(Color.WHITE);
        LinearLayout.LayoutParams fpsLl = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        fpsLl.setMargins(0, 10, 0, 0);
        fpsLl.gravity = Gravity.CENTER;
        textView_fps.setLayoutParams(fpsLl);

        lvd = new MyListView(activity);
        LinearLayoutManager layoutManager = new LinearLayoutManager(activity,
                RecyclerView.VERTICAL, false) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        lvd.setLayoutManager(layoutManager);
        dAdapter = new ContentAdapter(activity, this, lvd);
        dAdapter.setItemOnClickListener((v, pos) -> {
            if (dAdapter.onScope && pos % 2 == 1) {
                Intent intent = new Intent(activity, ScopeActivity.class);
                intent.putExtra("watch_list", new int[]{pos / 2});
                activity.startActivity(intent);
            }
        });
        lvd.setAdapter(dAdapter);
        RecyclerView.LayoutParams lvdRl = new RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.MATCH_PARENT
        );
//        lvdRl.setMarginStart(20);
//        lvdRl.setMarginEnd(20);
        lvd.setLayoutParams(lvdRl);
        DefaultItemAnimator itemAni = new DefaultItemAnimator() {

            @Override
            public void onAddStarting(RecyclerView.ViewHolder item) {
                super.onAddStarting(item);
                dAdapter.onHold = false;
//                logD("ANI--->onAddStarting");
            }

            @Override
            public void onAddFinished(RecyclerView.ViewHolder item) {
                super.onAddFinished(item);
                dAdapter.onHold = false;
//                logD("ANI--->onAddFinished");
            }

        };
        itemAni.setChangeDuration(0);
        itemAni.setSupportsChangeAnimations(false);
        lvd.setItemAnimator(itemAni);

        activity.runOnUiThread(() -> {
            LinearLayout root = binding1.page1Root;
            root.addView(textView_name);
            root.addView(textView_fps);
            root.addView(lvd);
        });
    }

    public void onDestroyUI() {
        if (hasUI)
            activity.runOnUiThread(() -> {
                LinearLayout root = binding1.page1Root;
                root.removeView(textView_name);
                root.removeView(textView_fps);
                root.removeView(lvd);
            });
        hasUI = false;
    }

    public void onUIUpdate() {
//        dAdapter.setContent(mContent);
        count_fps++;
        if (hasUI)
            activity.runOnUiThread(() -> {
                if (count_fps > 100) {
                    long time_now = new Date().getTime();
                    textView_fps.setText((float) 1000 * count_fps / (time_now - time_fps) + "");
                    count_fps = 0;
                    time_fps = time_now;
                }
                dAdapter.onUpDate();
            });
    }
}

