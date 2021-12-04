package com.zzh.dreamchaser.debugBT.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

import androidx.recyclerview.widget.RecyclerView;

public class MyListView extends RecyclerView {

    public MyListView(Context paramContext) {
        super(paramContext);
    }

    public MyListView(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
    }

    public MyListView(Context paramContext, AttributeSet paramAttributeSet,
                      int paramInt) {
        super(paramContext, paramAttributeSet, paramInt);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }



}
