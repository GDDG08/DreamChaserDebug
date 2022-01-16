package com.zzh.dreamchaser.debugBT.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class MyScrollView extends ScrollView {
    public interface ScrollListener {
//        void onScrollChanged(MyScrollView scrollView, int x, int y, int oldx, int oldy);

        void onScrollBegin(MyScrollView scrollView);

        void onScrollStop(MyScrollView scrollView);
    }


    private ScrollListener scrollListener = null;

    public MyScrollView(Context context) {
        super(context);
    }

    public MyScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public MyScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setScrollListener(ScrollListener scrollListener) {
        this.scrollListener = scrollListener;
    }

    @Override
    protected void onScrollChanged(int x, int y, int oldx, int oldy) {
        super.onScrollChanged(x, y, oldx, oldy);
        Log.d("Scroll", "change");
        if (scrollListener != null)
            if (Math.abs(oldy - y) >= 1)
                scrollListener.onScrollBegin(this);
            else
                scrollListener.onScrollStop(this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (scrollListener != null)
            switch (ev.getAction()) {
                case MotionEvent.ACTION_SCROLL:
                case MotionEvent.ACTION_DOWN:
//                    Log.d("Scroll", "down");
                    scrollListener.onScrollBegin(this);
                    break;
                case MotionEvent.ACTION_UP:
//                    Log.d("Scroll", "up");
                    scrollListener.onScrollStop(this);
                    break;
            }
        return super.onTouchEvent(ev);
    }
}

