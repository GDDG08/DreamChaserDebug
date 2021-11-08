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
import com.zzh.dreamchaser.debugBT.databinding.Fragment1Binding;
import com.zzh.dreamchaser.debugBT.databinding.Fragment2Binding;
import com.zzh.dreamchaser.debugBT.databinding.FragmentMainBinding;

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
                final Button button = binding1.button;
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharedPreferences colorInfo = getActivity().getSharedPreferences(MainActivity.PREFS_NAME, MODE_PRIVATE);
                        int color2;
                        for (int i = 1; i <= 3; i++) {
                            color2 = colorInfo.getInt("light" + i, Color.parseColor("#ffffffff"));
                            sendColor(i, color2);
                        }
                        BLsend("!");
                        Toast.makeText(getContext(),"Done!",Toast.LENGTH_LONG).show();
                    }
                });

                final Switch switch1 = binding1.switch1;
                final Switch switch2 = binding1.switch2;
                final Switch switch3 = binding1.switch3;

                switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                        BLsend(isChecked? "{":"}");
                        BLsend(i82Byte(0xff));

                    }
                });
                switch2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        BLsend(isChecked? "[":"]");
                    }
                });
                switch3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        BLsend(isChecked? "<":">");
                    }
                });
                break;
            case Page_Mode_Setting:
                binding2 = Fragment2Binding.inflate(inflater, container, false);
                root = binding2.getRoot();

                final RadioGroup rg = binding2.RadioGroup;
                final ColorPicker picker = binding2.picker;
                final SVBar svBar = binding2.svbar;
                picker.addSVBar(svBar);

//                binding2.radioButton2.setChecked(true);
                rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        SharedPreferences colorInfo = getActivity().getSharedPreferences(MainActivity.PREFS_NAME, MODE_PRIVATE);
                        switch (checkedId){
                            case R.id.radioButton1:
                                radiobutton_selected = 1;
                                break;
                            case R.id.radioButton2:
                                radiobutton_selected = 2;
                                break;
                            case R.id.radioButton3:
                                radiobutton_selected = 3;
                                break;
                        }
                        int color = colorInfo.getInt("light"+radiobutton_selected,0);
                        picker.setOldCenterColor(color);
                        picker.setColor(color);
                        sendColor();
                        RefreshColor(color);
                    }
                });

                picker.setOnColorChangedListener(new ColorPicker.OnColorChangedListener() {
                    @Override
                    public void onColorChanged(int color0) {
                        color = svBar.getColor();
//                        color_change_cnt ++;
//                        if (color_change_cnt == 1)
                            sendColor(radiobutton_selected, color);
//                        else if (color_change_cnt == 2)
//                            color_change_cnt = 0;

                        RefreshColor(color);
                    }
                });
                picker.setOnColorSelectedListener(new ColorPicker.OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int color0) {
                        //保存
                        SharedPreferences colorInfo = getActivity().getSharedPreferences(MainActivity.PREFS_NAME, MODE_PRIVATE);
                        SharedPreferences.Editor editor = colorInfo.edit();//获取Editor
                        editor.putInt("light"+radiobutton_selected, color);
                        editor.commit();
                    }
                });
                final Button button2 = binding2.button2;
                button2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MainActivity.initColors(getActivity());
                        Toast.makeText(getContext(),"Done!",Toast.LENGTH_LONG).show();
                    }
                });
                break;
            default:
                break;
        }
        return root;
    }

    private void RefreshColor(int color) {
        int r,g,b;
        r = (color & 0x00ff0000) >> 16;
        g = (color & 0x0000ff00) >> 8;
        b = (color & 0x000000ff) >> 0;

        binding2.textView1.setText(r+"");
        binding2.textView2.setText(g+"");
        binding2.textView3.setText(b+"");
    }

    public static void sendColor() {
        sendColor(radiobutton_selected, color);
    }

    public static void sendColor(int index, int color){
//        Log.d("Dedddddd", Integer.toHexString(color));
        String cmd = "#" + index + "&" + Integer.toHexString(color).substring(2) + "@";
        BLsend(cmd);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}