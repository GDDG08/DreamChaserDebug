package com.zzh.dreamchaser.debugBT.hardware;


import static android.content.Context.SENSOR_SERVICE;

import static com.zzh.dreamchaser.debugBT.ui.main.PlaceholderFragment.onSensorUpdate;

import static java.lang.Math.abs;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class InnerSensor {
    private Context mContext;

    private final String TAG = "InnerSensor";

    private SensorManager mSensorManager;
    private MySensorEventListener mMySensorEventListener;
//    private float[] mAccelerometerReading = new float[3];
//    private float[] mMagneticFieldReading = new float[3];

    public InnerSensor(Context mContext) {
        this.mContext = mContext;
        this.mSensorManager = (SensorManager) mContext.getSystemService(SENSOR_SERVICE);
        this.mMySensorEventListener = new MySensorEventListener();
    }

    public void Resume() {

        if (mSensorManager == null) {
            return;
        }
        refreshOffset();
        round_count = 0;
//        Sensor accelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
//        if (accelerometerSensor != null) {
//            //register accelerometer sensor listener
//            mSensorManager.registerListener((SensorEventListener) mMySensorEventListener, accelerometerSensor, SensorManager.SENSOR_DELAY_UI);
//        } else {
//            Log.d(TAG, "Accelerometer sensors are not supported on current devices.");
//        }
//
//        Sensor magneticSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
//        if (magneticSensor != null) {
//            //register magnetic sensor listener
//            mSensorManager.registerListener(mMySensorEventListener, magneticSensor, SensorManager.SENSOR_DELAY_UI);
//        } else {
//            Log.d(TAG, "Magnetic sensors are not supported on current devices.");
//        }

        Sensor orientationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        if (orientationSensor != null) {
            //register gyroscope sensor listener
            mSensorManager.registerListener(mMySensorEventListener, orientationSensor, SensorManager.SENSOR_DELAY_FASTEST   );
        } else {
            Log.d(TAG, "Orientation sensors are not supported on current devices.");
        }
    }

    public void Pause() {
        if (mSensorManager == null) {
            return;
        }
        //unregister all listener
        mSensorManager.unregisterListener(mMySensorEventListener);
    }


//    private void calculateOrientation() {
//        final float[] rotationMatrix = new float[9];
//        SensorManager.getRotationMatrix(rotationMatrix, null, mAccelerometerReading, mMagneticFieldReading);
//
//        final float[] orientationAngles = new float[3];
//        SensorManager.getOrientation(rotationMatrix, orientationAngles);
//
//        // 要经过一次数据格式的转换，转换为度
//        orientationAngles[0] = (float) Math.toDegrees(orientationAngles[0]);
//        orientationAngles[1] = (float) Math.toDegrees(orientationAngles[1]);
//        orientationAngles[2] = (float) Math.toDegrees(orientationAngles[2]);
//
//        Log.d(TAG, "orientation2 data[x:" + orientationAngles[0] + ", y:" + orientationAngles[1] + ", z:" + orientationAngles[2] + "]");
//        }

    float[] sensor_values = new float[3];
    float[] orientation_values = new float[3];
    float[] orientation_offset = new float[3];

    private class MySensorEventListener implements SensorEventListener {
        @Override
        public void onSensorChanged(SensorEvent event) {
//            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
//                mAccelerometerReading = event.values;
//                Log.d(TAG, "accelerometer data[x:" + event.values[0] + ", y:" + event.values[1] + ", z:" + event.values[2] + "]");
//            } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
//                mMagneticFieldReading = event.values;
//                Log.d(TAG, "magnetic data[x:" + event.values[0] + ", y:" + event.values[1] + ", z:" + event.values[2] + "]");
//            } else
            if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
                calcAngle(event.values);

//                Log.d(TAG, "orientation data[x:" + orientation_values[0] + ", y:" + orientation_values[1] + ", z:" + orientation_values[2] + "]");
                onSensorUpdate(orientation_values);
            }
//            calculateOrientation();

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            Log.d(TAG, "onAccuracyChanged:" + sensor.getType() + "->" + accuracy);
        }

    }

    float yaw_angle_last = 0;
    int round_count = 0;

    private void calcAngle(float[] values) {
        values[0]*=-1;
        sensor_values = values;
        float yaw_angle = values[0];
        float diff = yaw_angle - yaw_angle_last;
        if (diff < -300)
            round_count++;
        else if (diff > 300)
            round_count--;

        float consequent_angle = round_count * 360 + yaw_angle;

        if (abs(round_count) > 100) {
            consequent_angle -= 360 * round_count;
            round_count = 0;
        }

        yaw_angle_last = yaw_angle;

        orientation_values[0] = consequent_angle - orientation_offset[0];
        orientation_values[1] = values[1] - orientation_offset[1];
        orientation_values[2] = values[2] - orientation_offset[2];
    }

    public void refreshOffset() {
        orientation_offset[0] = sensor_values[0];
        orientation_offset[1] = sensor_values[1];
        orientation_offset[2] = sensor_values[2];
//        yaw_angle_last = 0;
        round_count = 0;
    }
}
