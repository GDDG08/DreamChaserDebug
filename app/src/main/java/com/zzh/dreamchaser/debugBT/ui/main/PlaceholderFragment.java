package com.zzh.dreamchaser.debugBT.ui.main;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.zzh.dreamchaser.debugBT.CustomActivity;
import com.zzh.dreamchaser.debugBT.MainActivity;
import com.zzh.dreamchaser.debugBT.connect.BLESPPUtils;
import com.zzh.dreamchaser.debugBT.connect.DeviceHandle;
import com.zzh.dreamchaser.debugBT.connect.DeviceList;
import com.zzh.dreamchaser.debugBT.data.ContentAdapter;
import com.zzh.dreamchaser.debugBT.databinding.Fragment1Binding;
import com.zzh.dreamchaser.debugBT.databinding.Fragment2Binding;
import com.zzh.dreamchaser.debugBT.databinding.FragmentMainBinding;
import com.zzh.dreamchaser.debugBT.view.MyListView;
import com.zzh.dreamchaser.debugBT.view.MyScrollView;

import java.util.Timer;
import java.util.TimerTask;

import static com.zzh.dreamchaser.debugBT.GDDGApplication.verCtrl;
import static com.zzh.dreamchaser.debugBT.MainActivity.mInnerSensor;
import static com.zzh.dreamchaser.debugBT.MainActivity.mLogger;
import static com.zzh.dreamchaser.debugBT.MainActivity.mMetaCTRLer;
import static com.zzh.dreamchaser.debugBT.tool.byteCov.*;
import static com.zzh.dreamchaser.debugBT.tool.myLog.logD;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private PageViewModel pageViewModel;

    private FragmentMainBinding binding;
    public static Fragment1Binding binding1;
    public static Fragment2Binding binding2;

    public static final int Page_Info = 1;
    public static final int Page_Tools = 2;

    public static int radiobutton_selected = 2;
    public static ContentAdapter dAdapter;
    public static MyListView lvd;
    public static TextView textView_fps;
    public static TextView textView_file;
    public static Switch switch1;
    public static Switch switch2;

    public static PlaceholderFragment newInstance(int index) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageViewModel = new ViewModelProvider(this).get(PageViewModel.class);
        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        pageViewModel.setIndex(index);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        int index = getArguments().getInt(ARG_SECTION_NUMBER);
        View root = null;
        switch (index) {
            case Page_Info:
                binding1 = Fragment1Binding.inflate(inflater, container, false);
                root = binding1.getRoot();

                MyScrollView mainScroll = binding1.mainScroll;
                mainScroll.setScrollListener(new MyScrollView.ScrollListener() {

                    Timer tStop = new Timer();

                    @Override
                    public void onScrollBegin(MyScrollView scrollView) {
                        for (DeviceHandle dh : DeviceList.targetDevices)
                            if (dh.dAdapter != null) {
                                dh.dAdapter.onHold = true;

                                tStop.cancel();
                                tStop = new Timer();
                                tStop.schedule(new TimerTask() {
                                    @Override
                                    public void run() {
                                        dh.dAdapter.onHold = false;
//                                        Log.d("Scroll", "timer");
                                    }
                                }, 50);
                            }
                    }

                    @Override
                    public void onScrollStop(MyScrollView scrollView) {
                        for (DeviceHandle dh : DeviceList.targetDevices)
                            if (dh.dAdapter != null)
                                dh.dAdapter.onHold = false;
                    }
                });

                switch1 = binding1.switch1;
                switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (mLogger.onLogging && isChecked)
                            return;
                        if (mLogger.file != null) {
                            if (isChecked)
                                mLogger.writeHeader();
                            mLogger.onLogging = isChecked;
                        } else {
                            buttonView.setChecked(false);
                            mLogger.init(getActivity());
                        }
                    }
                });
//                textView_fps = binding1.textViewFps;
                textView_file = binding1.textViewFile;
                textView_file.setOnClickListener((v) -> {
                    Toast.makeText(getContext(), mLogger.file + "\n饼：这里应该是点按打开，长按分享", Toast.LENGTH_SHORT).show();
                });

                switch2 = binding1.switch2;
                switch2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        for (DeviceHandle device : DeviceList.targetDevices)
                            if (device.dAdapter != null)
                                device.dAdapter.setOnScope(isChecked, true);
//                        dAdapter.notifyDataSetChanged();
//                        lvd.postInvalidate();
                    }
                });
                binding1.refresh.setOnClickListener((v) -> {
                    for (DeviceHandle deviceHandle : DeviceList.targetDevices)
                        deviceHandle.sendData(i82Byte(0xf1));
                });
                DeviceList.demo(getActivity(), "DEMO");

                final SeekBar seekBar11 = binding1.seekBar11;
                final TextView seekbarText11 = binding1.seekbarText11;
                final SeekBar seekBar12 = binding1.seekBar12;
                final TextView seekbarText12 = binding1.seekbarText12;
                SeekBar.OnSeekBarChangeListener listener3 = new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        /*if (seekBar == seekBar11) {
                            seekbarText11.setText((1 + 2 * (float) (seekBar11.getProgress() - 500) / 500) + "");
                        } else if (seekBar == seekBar12) {
                            seekbarText12.setText((1 + 2 * (float) (seekBar12.getProgress() - 500) / 500) + "");
                        }
                        byte[] temp = new byte[9];
                        temp[0] = (byte) 0xa8;
                        System.arraycopy(fl2Byte(1 + 2 * (float) (seekBar11.getProgress() - 500) / 500), 0, temp, 1, 4);
                        System.arraycopy(fl2Byte(1 + 2 * (float) (seekBar12.getProgress() - 500) / 500), 0, temp, 5, 4);

                        Log.d("CMD_SET_MOTOR_BAIS:-->", byte2Hex(temp));
                        DeviceList.targetDevices.get(0).sendData(temp);*/


                        seekbarText11.setText((0.00020635f + 0.0001 * (float) seekBar11.getProgress() / 1000) + "");
                        seekbarText12.setText((0.05f + 0.1 * (float) seekBar12.getProgress() / 1000) + "");

                        byte[] temp = new byte[9];
                        temp[0] = (byte) 0xa8;
                        System.arraycopy(fl2Byte(0.00020635f + 0.0001f * (float) seekBar11.getProgress() / 1000), 0, temp, 1, 4);
                        System.arraycopy(fl2Byte(0.05f + 0.1f * (float) seekBar12.getProgress() / 1000), 0, temp, 5, 4);

                        logD("CMD_SET_MOTOR_BAIS:-->" + byte2Hex(temp));
                        DeviceList.targetDevices.get(0).sendData(temp);

//                        if (seekBar == seekBar11) {
//                            seekbarText11.setText((0.5 + 0.2 * (float) seekBar11.getProgress() / 1000) + "");
//                        } else if (seekBar == seekBar12) {
//                            seekbarText12.setText((0.5 + 0.2 * (float) seekBar12.getProgress() / 1000) + "");
//                        }
//                        byte[] temp = new byte[9];
//                        temp[0] = (byte) 0xa8;
//                        System.arraycopy(fl2Byte(0.5f + 0.2f * (float) seekBar11.getProgress() / 1000), 0, temp, 1, 4);
//                        System.arraycopy(fl2Byte(0.5f + 0.2f * (float) seekBar12.getProgress() / 1000), 0, temp, 5, 4);
//
//                        Log.d("CMD_SET_MOTOR_BAIS:-->", byte2Hex(temp));
//                        DeviceList.targetDevices.get(0).sendData(temp);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                };
                seekBar11.setOnSeekBarChangeListener(listener3);
                seekBar12.setOnSeekBarChangeListener(listener3);

                break;
            case Page_Tools:
                binding2 = Fragment2Binding.inflate(inflater, container, false);
                root = binding2.getRoot();

                switch_sensor = binding2.switchSensor;
                switch_imu = binding2.switchImu;

                binding2.button2.setOnClickListener((v) -> {
//                    Intent i = new Intent(getActivity(), CustomActivity.class);
//                    startActivity(i);
//                    verCtrl.getSeverVer();
                    mInnerSensor.refreshOffset();
                    mMetaCTRLer.refreshOffset();
                });
                final SeekBar seekBar1 = binding2.seekBar1;
                final SeekBar seekBar2 = binding2.seekBar2;
                final SeekBar seekBar3 = binding2.seekBar3;
                final SeekBar seekBar4 = binding2.seekBar4;

                final TextView seekbarText1 = binding2.seekbarText1;
                final TextView seekbarText2 = binding2.seekbarText2;
                final TextView seekbarText3 = binding2.seekbarText3;
                final TextView seekbarText4 = binding2.seekbarText4;

                SeekBar.OnSeekBarChangeListener listener = new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if (seekBar == seekBar1) {
                            seekbarText1.setText((float) (progress - 500) / 2500 + "");
                        } else if (seekBar == seekBar2) {
                            seekbarText2.setText((float) (progress - 500) / 2500 + "");
                        } else if (seekBar == seekBar3) {
                            seekbarText3.setText((float) (progress - 500) / 2500 + "");
                        } else if (seekBar == seekBar4) {
                            seekbarText4.setText((float) (progress - 500) / 2500 + "");
                        }
                        byte[] temp = new byte[17];
                        temp[0] = (byte) 0xa0;
                        System.arraycopy(fl2Byte(1 + (float) (seekBar1.getProgress() - 500) / 2500), 0, temp, 1, 4);
                        System.arraycopy(fl2Byte(1 + (float) (seekBar2.getProgress() - 500) / 2500), 0, temp, 5, 4);
                        System.arraycopy(fl2Byte(1 + (float) (seekBar3.getProgress() - 500) / 2500), 0, temp, 9, 4);
                        System.arraycopy(fl2Byte(1 + (float) (seekBar4.getProgress() - 500) / 2500), 0, temp, 13, 4);
//                        BLsend(temp);
                        logD("COMPENSATE:-->" + byte2Hex(temp));
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                };
                seekBar1.setOnSeekBarChangeListener(listener);
                seekBar2.setOnSeekBarChangeListener(listener);
                seekBar3.setOnSeekBarChangeListener(listener);
                seekBar4.setOnSeekBarChangeListener(listener);

                final SeekBar seekBar5 = binding2.seekBar5;
                final TextView seekbarText5 = binding2.seekbarText5;
                final SeekBar seekBar6 = binding2.seekBar6;
                final TextView seekbarText6 = binding2.seekbarText6;

                SeekBar.OnSeekBarChangeListener listener2 = new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        seekbarText5.setText((0 + 12 * ((float) seekBar5.getProgress() - 500) / 1000) + "");
                        seekbarText6.setText((0 + 2 * ((float) seekBar6.getProgress() - 500) / 1000) + "");

                        byte[] temp = new byte[9];
                        temp[0] = (byte) 0xb1;
                        System.arraycopy(fl2Byte(0 + 12 * ((float) seekBar5.getProgress() - 500) / 1000), 0, temp, 1, 4);
                        System.arraycopy(fl2Byte(0 + 2 * ((float) seekBar6.getProgress() - 500) / 1000), 0, temp, 5, 4);
                        logD("Offset:-->" + byte2Hex(temp));
                        DeviceList.targetDevices.get(0).sendData(temp);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                };
                seekBar5.setOnSeekBarChangeListener(listener2);
                seekBar6.setOnSeekBarChangeListener(listener2);

                final SeekBar seekBar13 = binding2.seekBar13;
                final TextView seekbarText13 = binding2.seekbarText13;
                final SeekBar seekBar14 = binding2.seekBar14;
                final TextView seekbarText14 = binding2.seekbarText14;
                final SeekBar seekBar15 = binding2.seekBar15;
                final TextView seekbarText15 = binding2.seekbarText15;
                final SeekBar seekBar16 = binding2.seekBar16;
                final TextView seekbarText16 = binding2.seekbarText16;
                final SeekBar seekBar17 = binding2.seekBar17;
                final TextView seekbarText17 = binding2.seekbarText17;
                final SeekBar seekBar18 = binding2.seekBar18;
                final TextView seekbarText18 = binding2.seekbarText18;
                final SeekBar seekBar19 = binding2.seekBar19;
                final TextView seekbarText19 = binding2.seekbarText19;
                final SeekBar seekBar20 = binding2.seekBar20;
                final TextView seekbarText20 = binding2.seekbarText20;
                final SeekBar seekBar21 = binding2.seekBar21;
                final TextView seekbarText21 = binding2.seekbarText21;
                SeekBar.OnSeekBarChangeListener listener4 = new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        seekbarText13.setText((0 + 3 * (float) seekBar13.getProgress() / 1000) + "");
                        seekbarText14.setText((0 + 0.1f * (float) seekBar14.getProgress() / 1000) + "");
                        seekbarText15.setText((0 + 5 * (float) seekBar15.getProgress() / 1000) + "");
                        seekbarText16.setText((0 + 3 * (float) seekBar16.getProgress() / 1000) + "");
                        seekbarText17.setText((0 + 0.1f * (float) seekBar17.getProgress() / 1000) + "");
                        seekbarText18.setText((0 + 5 * (float) seekBar18.getProgress() / 1000) + "");
                        seekbarText19.setText((0 + 3 * (float) seekBar19.getProgress() / 1000) + "");
                        seekbarText20.setText((0 + 0.1f * (float) seekBar20.getProgress() / 1000) + "");
                        seekbarText21.setText((0 + 5 * (float) seekBar21.getProgress() / 1000) + "");


                        byte[] temp = new byte[9];
                        temp[0] = (byte) 0xa8;
                        System.arraycopy(fl2Byte(0 + 3 * (float) seekBar13.getProgress() / 1000), 0, temp, 1, 4);
                        System.arraycopy(fl2Byte(0 + 0.1f * (float) seekBar14.getProgress() / 1000), 0, temp, 5, 4);
                        System.arraycopy(fl2Byte(0 + 5 * (float) seekBar15.getProgress() / 1000), 0, temp, 9, 4);
                        System.arraycopy(fl2Byte(0), 0, temp, 13, 4);
                        System.arraycopy(fl2Byte(0 + 3 * (float) seekBar16.getProgress() / 1000), 0, temp, 17, 4);
                        System.arraycopy(fl2Byte(0 + 0.1f * (float) seekBar17.getProgress() / 1000), 0, temp, 21, 4);
                        System.arraycopy(fl2Byte(0 + 5 * (float) seekBar18.getProgress() / 1000), 0, temp, 25, 4);
                        System.arraycopy(fl2Byte(0), 0, temp, 29, 4);
                        System.arraycopy(fl2Byte(0 + 3 * (float) seekBar19.getProgress() / 1000), 0, temp, 33, 4);
                        System.arraycopy(fl2Byte(0 + 0.1f * (float) seekBar20.getProgress() / 1000), 0, temp, 37, 4);
                        System.arraycopy(fl2Byte(0 + 5 * (float) seekBar21.getProgress() / 1000), 0, temp, 41, 4);
                        System.arraycopy(fl2Byte(0), 0, temp, 44, 4);

                        logD("CMD_SET_MOTOR_BAIS:-->" + byte2Hex(temp));
                        DeviceList.targetDevices.get(0).sendData(temp);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                };
                seekBar13.setOnSeekBarChangeListener(listener4);
                seekBar14.setOnSeekBarChangeListener(listener4);
                seekBar15.setOnSeekBarChangeListener(listener4);
                seekBar16.setOnSeekBarChangeListener(listener4);
                seekBar17.setOnSeekBarChangeListener(listener4);
                seekBar18.setOnSeekBarChangeListener(listener4);
                seekBar19.setOnSeekBarChangeListener(listener4);
                seekBar20.setOnSeekBarChangeListener(listener4);
                seekBar21.setOnSeekBarChangeListener(listener4);

//                BLESPPUtils.OnBluetoothAction oba = new BLESPPUtils.OnBluetoothAction() {
//                    @Override
//                    public void onFoundDevice(BluetoothDevice device) {
//
//                    }
//
//                    @Override
//                    public void onConnectSuccess(BluetoothDevice device, BluetoothSocket socket) {
////                        BLESPPUtils.RecvTask recvTask = new BLESPPUtils.RecvTask(mBLESPPUtils1.on)socket)
//                        Log.d("DOUBLE", "连接成功" + device.getName() + device.getAddress());
//                        ((MainActivity) getActivity()).postShowToast(device.getName() + "(" + device.getAddress() + ")\n连接成功!");
//                    }
//
//                    @Override
//                    public void onConnectFailed(String deviceMac, String msg) {
//                        Log.d("DOUBLE", "连接失败！" + msg);
//                        ((MainActivity) getActivity()).postShowToast(msg);
////                        mLogger.writeHeader();
//                        new AlertDialog.Builder(getContext())
//                                .setTitle("是否重连")
//                                .setMessage(msg)
//                                .setNegativeButton("取消", (view, which) -> {
//                                    DeviceList.removeDevice(deviceMac);
////                                    DeviceList.targetDevices.remove(DeviceList.getDeviceHandle(deviceMac));
//                                })
//                                .setPositiveButton("重试", (view, which) -> {
//                                    DeviceList.getDeviceHandle(deviceMac).connect();
//                                })
//                                .show();
//                    }
//
//                    @Override
//                    public void onReceiveBytes(int id, byte[] bytes) {
//                        Log.d("Receiving1----->", "设备" + id + ":" + new String(bytes));
//                        DeviceList.targetDevices.get(id).onUIUpdate();
//                        mLogger.runOnCall();
//                    }
//
//                    @Override
//                    public void onSendBytes(int id, byte[] bytes) {
//                        Log.d("Senidng--->", byte2Hex(bytes));
//                    }
//
//                    @Override
//                    public void onFinishFoundDevice() {
//
//                    }
//                };
//                DeviceList.setOnBluetoothAction(oba);

                break;
            default:
                break;
        }
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    static Switch switch_sensor;
    static boolean sensor_first = true;


    public static void onSensorUpdate(float[] orientaion) {
        if (switch_sensor.isChecked()) {
            if (sensor_first == true) {
                mInnerSensor.refreshOffset();
                sensor_first = false;
            }
            GimbalCTRL(orientaion);
            return;
        } else {
            sensor_first = true;
        }
    }

    static Switch switch_imu;
    static boolean imu_first = true;

    public static void onIMUUpdate(float[] orientaion) {
        binding2.seekbarText1.setText("x:" + orientaion[0] + ", y:" + orientaion[1] + ", z:" + orientaion[2]);
        if (switch_imu.isChecked()) {
            if (imu_first == true) {
                mMetaCTRLer.refreshOffset();
                imu_first = false;
            }
            GimbalCTRL(orientaion);
        } else {
            imu_first = true;
        }
    }

    private static void GimbalCTRL(float[] orientaion) {
        byte[] temp = new byte[9];
        temp[0] = (byte) 0xb6;
        System.arraycopy(fl2Byte(orientaion[0]), 0, temp, 1, 4);
        System.arraycopy(fl2Byte(orientaion[1]), 0, temp, 5, 4);
        logD("CMD_SET_GIMBAL_ANGLE:-->" + byte2Hex(temp));
        try {
            DeviceList.getDeviceHandle(0).sendData(temp);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}