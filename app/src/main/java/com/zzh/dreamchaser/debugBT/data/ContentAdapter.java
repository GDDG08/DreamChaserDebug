package com.zzh.dreamchaser.debugBT.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zzh.dreamchaser.debugBT.R;

public class ContentAdapter extends BaseAdapter {
    Context context;
    TextView txt1, txt2;

    public int getCount() {
        return Content.dataLen;
//        return 3;
    }

    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

//    int temp = 0;
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        final LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View v = inflater.inflate(R.layout.list_data, null);

        txt1 = (TextView) v.findViewById(R.id.txt1);
        txt2 = (TextView) v.findViewById(R.id.txt2);

        if (position % 2 == 0){
            v.setBackgroundColor(0x1FFFFFFF);
        }
//        if (position == 0) {
//            txt1.setText("1");
//            txt2.setText("value"+temp);
//        }
//        else if (position == 1) {
//            txt1.setText("2");
//            txt2.setText("value"+temp);
//        }
//        else {
//            txt1.setText("3");
//            txt2.setText("value"+temp);
//        }
        Var data = (Var)Content.list.get(position);
        txt1.setText(data.getTag());
        txt2.setText(data.getStr());
//        temp++;
        return v;
    }

    public ContentAdapter(Context ct) {
        context = ct;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }
}

