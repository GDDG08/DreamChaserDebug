package com.zzh.dreamchaser.debugBT.ui.main;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.SVBar;
import com.zzh.dreamchaser.debugBT.MainActivity;
import com.zzh.dreamchaser.debugBT.R;
import com.zzh.dreamchaser.debugBT.data.ContentAdapter;
import com.zzh.dreamchaser.debugBT.data.ContentUpdate;
import com.zzh.dreamchaser.debugBT.databinding.Fragment1Binding;
import com.zzh.dreamchaser.debugBT.databinding.Fragment2Binding;
import com.zzh.dreamchaser.debugBT.databinding.FragmentMainBinding;
import com.zzh.dreamchaser.debugBT.view.MyListView;

import static android.content.Context.MODE_PRIVATE;
import static com.zzh.dreamchaser.debugBT.MainActivity.BLsend;
import static com.zzh.dreamchaser.debugBT.tool.byteCov.*;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private PageViewModel pageViewModel;

    private FragmentMainBinding binding;
    private Fragment1Binding binding1;
    public static Fragment2Binding binding2;

    public static final int Page_Mode_Automatic = 1;
    public static final int Page_Mode_Setting = 2;

    public static int color = Color.parseColor("#ffffffff");
    public static int radiobutton_selected = 2;
    public static ContentAdapter dAdapter;
    public static MyListView lvd;


//    int color_change_cnt = 0;

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
        switch(index){
            case Page_Mode_Automatic:
                binding1 = Fragment1Binding.inflate(inflater, container, false);
                root = binding1.getRoot();

                final Switch switch1 = binding1.switch1;

                switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                        BLsend(isChecked? "{":"}");
                        BLsend(i82Byte(0xff));
                        getActivity().runOnUiThread(new ContentUpdate());
                    }
                });

                lvd = binding1.ListViewData;
                dAdapter = new ContentAdapter(getContext());
                lvd.setDivider(null);
                lvd.setAdapter(dAdapter);

//                new ContentUpdate().start();

                break;
            case Page_Mode_Setting:
                binding2 = Fragment2Binding.inflate(inflater, container, false);
                root = binding2.getRoot();

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
                        if (seekBar == seekBar1){
                            seekbarText1.setText((float)(progress-500)/2500+"");
                        }else if (seekBar == seekBar2){
                            seekbarText2.setText((float)(progress-500)/2500+"");
                        }else if (seekBar == seekBar3){
                            seekbarText3.setText((float)(progress-500)/2500+"");
                        }else if (seekBar == seekBar4){
                            seekbarText4.setText((float)(progress-500)/2500+"");
                        }
                        byte[] temp = new byte[17];
                        temp[0] = (byte) 0xa0;
                        System.arraycopy(fl2Byte(1+(float)(seekBar1.getProgress()-500)/2500), 0, temp, 1, 4);
                        System.arraycopy(fl2Byte(1+(float)(seekBar2.getProgress()-500)/2500), 0, temp, 5, 4);
                        System.arraycopy(fl2Byte(1+(float)(seekBar3.getProgress()-500)/2500), 0, temp, 9, 4);
                        System.arraycopy(fl2Byte(1+(float)(seekBar4.getProgress()-500)/2500), 0, temp, 13, 4);
                        BLsend(temp);
                        Log.d("COMPENSATE:-->",byte2Hex(temp));
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