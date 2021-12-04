package com.zzh.dreamchaser.debugBT.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.widget.Button;
import android.widget.SeekBar;

import androidx.annotation.NonNull;

import com.zzh.dreamchaser.debugBT.R;
import com.zzh.dreamchaser.debugBT.data.Content;
import com.zzh.dreamchaser.debugBT.data.Var;

import java.util.ArrayList;
import java.util.List;

import static com.zzh.dreamchaser.debugBT.MainActivity.mContent;
import static com.zzh.dreamchaser.debugBT.tool.byteCov.byte2Fl;
import static com.zzh.dreamchaser.debugBT.tool.byteCov.byte2Hex;
import static com.zzh.dreamchaser.debugBT.tool.byteCov.byte2i16;
import static com.zzh.dreamchaser.debugBT.tool.byteCov.byte2i32;
import static com.zzh.dreamchaser.debugBT.tool.byteCov.byte2i8;
import static com.zzh.dreamchaser.debugBT.tool.byteCov.getFloat4All;

public class SimpleScopeView extends TextureView implements TextureView.SurfaceTextureListener, Runnable {


    public static final int REX = 200;

    //    private SurfaceHolder surfaceHolder;
    private Paint paint;

    private volatile int width = 0;
    private volatile int height = 0;
    private volatile boolean isSurfaceReady = false;
    private volatile boolean isRunning = false;
    private volatile int dataPos = -1;

    public SimpleScopeView(Context context) {
        super(context);
        initView();
    }

    public SimpleScopeView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        initView();
    }

    public SimpleScopeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        setSurfaceTextureListener(this);
        //设置可获得焦点
        this.setFocusable(true);
        this.setFocusableInTouchMode(true);
        //设置常亮
        this.setKeepScreenOn(true);

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setColor(getResources().getColor(R.color.bluegray_100));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
//        isRunning = true;
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture arg0, int arg1, int arg2) {
        height = getHeight();
        width = getWidth();
        start();
//        isSurfaceReady = false;
//        new Thread(this).start();
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture arg0) {
        isSurfaceReady = false;
        return true;
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture arg0, int w, int h) {
        height = h;
        width = w;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture arg0) {
    }

    public void start() {
        if (!isSurfaceReady) {
            isSurfaceReady = true;
            new Thread(this).start();
        }
    }

    public void update(int pos) {
        start();
        dataPos = pos;
        isRunning = true;
    }

    public void stop() {
        isSurfaceReady = false;
    }

    @Override
    public void run() {
//        int cnt = 0;
        while (isSurfaceReady) {
            Canvas canvas = null;
            if (isRunning) {
                try {
                    canvas = this.lockCanvas(null);
                    if (canvas == null)
                        continue;
                    canvas.drawColor(getResources().getColor(R.color.bluegray_1000)/*, PorterDuff.Mode.CLEAR*/);
//                    canvas.drawColor(Color.TRANSPARENT);

//                    phaseValue = (phaseValue + 1) % 360;
//                    Path path = new Path();
//                    int offsetY = height / 2;
//                    path.moveTo(0, (float) (Math.sin(phaseValue * Math.PI / 180) * amplitudeValue) + offsetY);
//                    int repeatCount = width * 100 / waveLengthValue;
//                    for (int i = 1; i < repeatCount; i++) {
//                        path.lineTo(i * waveLengthValue / 100,
//                                (float) (Math.sin((i + phaseValue) * Math.PI / 180) * amplitudeValue) + offsetY);
//                    }
                    List<Float> his = getHisData();

                    float ratio = (height - 10) / range;
                    float offsetY = height / 2;

                    Path path = new Path();
                    path.moveTo(0, -(his.get(0) - offset) * ratio + offsetY);
                    for (int i = 1; i < REX; i++) {
                        path.lineTo(i * width / REX, -(his.get(i) - offset) * ratio + offsetY);
                    }
                    canvas.drawPath(path, paint);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (canvas != null) {
                        this.unlockCanvasAndPost(canvas);
//                        Log.d("Scope", "update ");
                    }
                }
//                cnt++;
//                if (cnt > 0) {
                isRunning = false;
//                    cnt = 0;
//                }
            }
        }
    }

    public float min = Float.POSITIVE_INFINITY, max = Float.NEGATIVE_INFINITY;
    public float range = 0, offset = 0;

    private List<Float> getHisData() {
        Var data = (Var) mContent.list.get(dataPos);
        List<byte[]> history = data.history;
        int type = data.type;

        List<Float> res = new ArrayList<>();
        min = Float.POSITIVE_INFINITY;
        max = Float.NEGATIVE_INFINITY;
        for (byte[] b : history) {
            float f = getFloat4All(type, b);
            min = Float.min(f, min);
            max = Float.max(f, max);
            res.add(f);
        }
        range = 1.0f;
        while ((range *= 2) <= max - min) ;

        int ave = (int) (max + min) / 2;
        offset = (2*ave / 1)/2;
//        while ((offset = (offset+1)*2) < ave) ;
        return res;
    }


//    @Override
//    public void run() {
//        while (isSurfaceReady) {
//            Canvas canvas = null;
//            if (isRunning) {
//                try {
//                    canvas = this.lockCanvas(null);
//                    canvas.drawColor(getResources().getColor(R.color.bluegray_1000)/*, PorterDuff.Mode.CLEAR*/);
////                    canvas.drawColor(Color.TRANSPARENT);
//                    phaseValue = (phaseValue + 1) % 360;
//                    Path path = new Path();
//                    int offsetY = height / 2;
//                    path.moveTo(0, (float) (Math.sin(phaseValue * Math.PI / 180) * amplitudeValue) + offsetY);
//                    int repeatCount = width * 100 / waveLengthValue;
//                    for (int i = 1; i < repeatCount; i++) {
//                        path.lineTo(i * waveLengthValue / 100,
//                                (float) (Math.sin((i + phaseValue) * Math.PI / 180) * amplitudeValue) + offsetY);
//                    }
//                    canvas.drawPath(path, paint);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                } finally {
//                    if (canvas != null) {
//                        this.unlockCanvasAndPost(canvas);
//
//                        Log.d("Scope", "update ");                    }
//                }
//            }
//
//            try {
//                Thread.sleep(5);
//            } catch (InterruptedException e) {
//                // Interrupted
//            }
//        }
//    }


/*    public void switcher(boolean b) {
        isRunning = b;
       *//* if (!isRunning) {
            Canvas canvas = surfaceHolder.lockCanvas();
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            surfaceHolder.unlockCanvasAndPost(canvas);
        }*//*
    }*/


}
