package com.zzh.dreamchaser.debugBT.hardware;

import static com.zzh.dreamchaser.debugBT.tool.byteCov.getFloat4All;
import static com.zzh.dreamchaser.debugBT.ui.main.PlaceholderFragment.onIMUUpdate;
import static com.zzh.dreamchaser.debugBT.ui.main.PlaceholderFragment.onSensorUpdate;
import static java.lang.Math.abs;

import android.content.Context;
import android.util.Log;

import com.zzh.dreamchaser.debugBT.data.Content;
import com.zzh.dreamchaser.debugBT.data.Var;

public class MetaCTRLer {
    private Context mContext;

    private final String TAG = "MetaController";

    public MetaCTRLer(Context mContext) {
        this.mContext = mContext;
    }


    float[] sensor_values = new float[3];
    float[] orientation_values = new float[3];
    float[] orientation_offset = new float[3];

    public void onIMUChanged(Content mContent) {
        float yaw  = getFloat4All(4,((Var) mContent.list.get(0)).data);
        float pitch  = getFloat4All(4,((Var) mContent.list.get(1)).data);
        float roll  = getFloat4All(4,((Var) mContent.list.get(2)).data);

        float[] values = new float[]{yaw, pitch, roll};
        calcAngle(values);

        Log.d(TAG, "orientation data[x:" + orientation_values[0] + ", y:" + orientation_values[1] + ", z:" + orientation_values[2] + "]");
        onIMUUpdate(orientation_values);

    }

    private void calcAngle(float[] values) {
        sensor_values = values;

        orientation_values[0] = values[0] - orientation_offset[0];
        orientation_values[1] = values[1] - orientation_offset[1];
        orientation_values[2] = values[2] - orientation_offset[2];
    }

    public void refreshOffset() {
        orientation_offset[0] = sensor_values[0];
        orientation_offset[1] = sensor_values[1];
        orientation_offset[2] = sensor_values[2];
    }
}
