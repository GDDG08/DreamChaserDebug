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

public class SimpleScopeView extends TextureView implements  TextureView.SurfaceTextureListener,Runnable {

    private static final int DEFAULT_WAVE_LENGTH = 200;
    private static final int DEFAULT_AMPLITUDE = 80;


//    private SurfaceHolder surfaceHolder;
    private Paint paint;

    private volatile int width = 0;
    private volatile int height = 0;
    private volatile boolean isSurfaceReady = false;
    private volatile boolean isRunning = false;
    private volatile int waveLengthValue = DEFAULT_WAVE_LENGTH;
    private volatile int amplitudeValue = DEFAULT_AMPLITUDE;
    private int phaseValue = 0;

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
//        surfaceHolder = getHolder();
//        surfaceHolder.addCallback(this);
//        surfaceHolder.setFormat(PixelFormat.TRANSPARENT);
//        setZOrderMediaOverlay(true);
        setSurfaceTextureListener(this);
        //设置可获得焦点
//        this.setFocusable(true);
//        this.setFocusableInTouchMode(true);
        //设置常亮
        this.setKeepScreenOn(true);

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setColor(getResources().getColor(R.color.bluegray_100));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(8);
        isRunning = true;
    }

//    @Override
//    public void surfaceCreated(@NonNull SurfaceHolder holder) {
//        isSurfaceReady = true;
//        new Thread(this).start();
//    }
//
//    @Override
//    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int w, int h) {
//        height = h;
//        width = w;
//    }
//
//    @Override
//    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
//        isSurfaceReady = false;
//    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture arg0, int arg1, int arg2) {
        height = getHeight();
        width = getWidth();
        isSurfaceReady = true;
        new Thread(this).start();
    }
    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture arg0) {
        isSurfaceReady = false;
        return true;
    }
    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture arg0, int w,int h) {
        height = h;
        width = w;
    }
    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture arg0) {
    }

    @Override
    public void run() {
//        float x = 0.0f;
//        float y = 0.0f;
//        float speedX = 5.0f;
//        float speedY = 3.0f;
        while (isSurfaceReady) {
            Canvas canvas = null;
            if (isRunning) {
                try {
                    canvas = this.lockCanvas(null);
                    canvas.drawColor(getResources().getColor(R.color.bluegray_1000)/*, PorterDuff.Mode.CLEAR*/);
//                    canvas.drawColor(Color.TRANSPARENT);
                    phaseValue = (phaseValue + 1) % 360;
                    Path path = new Path();
                    int offsetY = height / 2;
                    path.moveTo(0, (float) (Math.sin(phaseValue * Math.PI / 180) * amplitudeValue) + offsetY);
                    int repeatCount = width * 100 / waveLengthValue;
                    for (int i = 1; i < repeatCount; i++) {
                        path.lineTo(i * waveLengthValue / 100,
                                (float) (Math.sin((i + phaseValue) * Math.PI / 180) * amplitudeValue) + offsetY);
                    }
                    canvas.drawPath(path, paint);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (canvas != null) {
                        this.unlockCanvasAndPost(canvas);

                        Log.d("Scope", "update ");                    }
                }
            }
//            final Canvas canvas = this.lockCanvas(null);
//            try {
//                canvas.drawColor(Color.WHITE, PorterDuff.Mode.CLEAR);
//                //canvas.drawColor(Color.BLACK, PorterDuff.Mode.CLEAR);
//                canvas.drawRect(x, y, x + 20.0f, y + 20.0f, paint);
//            } finally {
//                this.unlockCanvasAndPost(canvas);
//            }
//
//            if (x + 20.0f + speedX >= width || x + speedX <= 0.0f) {
//                speedX = -speedX;
//            }
//            if (y + 20.0f + speedY >= height || y + speedY <= 0.0f) {
//                speedY = -speedY;
//            }
//
//            x += speedX;
//            y += speedY;

            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                // Interrupted
            }
        }
    }

    public void switcher(boolean b) {
        isRunning = b;
       /* if (!isRunning) {
            Canvas canvas = surfaceHolder.lockCanvas();
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            surfaceHolder.unlockCanvasAndPost(canvas);
        }*/
    }


}
