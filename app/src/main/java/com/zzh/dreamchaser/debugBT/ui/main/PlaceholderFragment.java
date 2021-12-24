package com.zzh.dreamchaser.debugBT.ui.main;

import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
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
import static com.zzh.dreamchaser.debugBT.MainActivity.mLogger;
import static com.zzh.dreamchaser.debugBT.tool.byteCov.*;

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
                        deviceHandle.onUIUpdate();
                });
                DeviceList.demo(getActivity(),"DEMO");
//                lvd = binding1.ListViewData;
////                LinearLayoutManager layoutManager = new LinearLayoutManager(getContext() );
////                //设置布局管理器
////
////                //设置为垂直布局，这也是默认的
////                layoutManager.setOrientation(RecyclerView.VERTICAL);
//                LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(),
//                        RecyclerView.VERTICAL, false) {
//                    @Override
//                    public boolean canScrollVertically() {
//                        return false;
//                    }
//                };
//                lvd.setLayoutManager(layoutManager);
//                dAdapter = new ContentAdapter(getContext(),((MainActivity)getActivity()).mContent, lvd);
////                lvd.setDivider(null);
//                dAdapter.setItemOnClickListener((v, pos) -> {
//                    if (dAdapter.onScope && pos % 2 == 1) {
//                        Intent intent = new Intent(getActivity(), ScopeActivity.class);
//                        intent.putExtra("watch_list", new int[]{pos / 2});
//                        startActivity(intent);
//                    }
//                });
//                lvd.setAdapter(dAdapter);
//
//
//                DefaultItemAnimator itemAni = new DefaultItemAnimator() {
//                    @Override
//                    public void onAddStarting(RecyclerView.ViewHolder item) {
//                        super.onAddStarting(item);
//                        dAdapter.onHold = true;
//                    }
//
//                    @Override
//                    public void onAddFinished(RecyclerView.ViewHolder item) {
//                        super.onAddFinished(item);
//                        dAdapter.onHold = false;
//                    }
//
//                    @Override
//                    public void onAnimationFinished(@NonNull @NotNull RecyclerView.ViewHolder viewHolder) {
//                        super.onAnimationFinished(viewHolder);
////                        dAdapter.onHold = false;
//                    }
//                };
//                itemAni.setChangeDuration(0);
////                itemAni.setMoveDuration(0);
//                itemAni.setSupportsChangeAnimations(false);
//                lvd.setItemAnimator(itemAni);


                break;
            case Page_Tools:
                binding2 = Fragment2Binding.inflate(inflater, container, false);
                root = binding2.getRoot();

                binding2.button2.setOnClickListener((v) -> {
                    Intent i = new Intent(getActivity(), CustomActivity.class);
                    startActivity(i);
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
                        Log.d("COMPENSATE:-->", byte2Hex(temp));
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

                BLESPPUtils.OnBluetoothAction oba = new BLESPPUtils.OnBluetoothAction() {
                    @Override
                    public void onFoundDevice(BluetoothDevice device) {

                    }

                    @Override
                    public void onConnectSuccess(BluetoothDevice device, BluetoothSocket socket) {
//                        BLESPPUtils.RecvTask recvTask = new BLESPPUtils.RecvTask(mBLESPPUtils1.on)socket)
                        Log.d("DOUBLE", "连接成功" + device.getName() + device.getAddress());
                        ((MainActivity) getActivity()).postShowToast(device.getName() + "(" + device.getAddress() + ")\n连接成功!");
                    }

                    @Override
                    public void onConnectFailed(String deviceMac, String msg) {
                        Log.d("DOUBLE", "连接失败！" + msg);
                        ((MainActivity) getActivity()).postShowToast(msg);
                        mLogger.writeHeader();
                        new AlertDialog.Builder(getContext())
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
                    }

                    @Override
                    public void onReceiveBytes(int id, byte[] bytes) {
                        Log.d("Receiving1----->", "设备" + id + ":" + new String(bytes));
                        ((MainActivity) getActivity()).runOnUiThread(() -> {
                            if (id == 0)
                                binding2.textView.setText(new String(bytes));
                            else if (id == 1)
                                binding2.textView3.setText(new String(bytes));
                        });
                        DeviceList.targetDevices.get(id).onUIUpdate();
                        mLogger.runOnCall();
                    }

                    @Override
                    public void onSendBytes(int id, byte[] bytes) {

                    }

                    @Override
                    public void onFinishFoundDevice() {

                    }
                };
                DeviceList.setOnBluetoothAction(oba);
//                BLESPPUtils mBLESPPUtils = new BLESPPUtils(getActivity(), oba);
//
//                if (!mBLESPPUtils.isBluetoothEnable())
//                    mBLESPPUtils.enableBluetooth();
//                mBLESPPUtils.onCreate();

                binding2.button3.setOnClickListener((v) -> {

                    DeviceList.connect(getActivity(), "98:D3:61:FD:33:2D");
                });
                binding2.button4.setOnClickListener((v) -> {
                    DeviceList.connect(getActivity(), "00:21:13:00:71:C3");
                });
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
}