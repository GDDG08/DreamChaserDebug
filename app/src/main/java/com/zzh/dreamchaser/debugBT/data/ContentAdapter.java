package com.zzh.dreamchaser.debugBT.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.zzh.dreamchaser.debugBT.MainActivity;
import com.zzh.dreamchaser.debugBT.R;
import com.zzh.dreamchaser.debugBT.view.MyListView;
import com.zzh.dreamchaser.debugBT.view.SimpleScopeView;

import static com.zzh.dreamchaser.debugBT.MainActivity.mContent;

public class ContentAdapter extends RecyclerView.Adapter<ContentAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final LinearLayout txt;
        public final ConstraintLayout scope_area;
        public final TextView txt1, txt2, scope_txt1, scope_txt2, scope_txt3;
        public final SimpleScopeView ssv;

        MyItemOnClickListener mListener;

        public ViewHolder(View v, MyItemOnClickListener myItemOnClickListener) {
            super(v);
            txt = (LinearLayout) v.findViewById(R.id.data_item_txt);
            txt1 = (TextView) v.findViewById(R.id.data_item_txt1);
            txt2 = (TextView) v.findViewById(R.id.data_item_txt2);
            scope_area = (ConstraintLayout) v.findViewById(R.id.list_scope_area);
            scope_txt1 = (TextView) v.findViewById(R.id.data_scope_txt1);
            scope_txt2 = (TextView) v.findViewById(R.id.data_scope_txt2);
            scope_txt3 = (TextView) v.findViewById(R.id.data_scope_txt3);
            ssv = (SimpleScopeView) v.findViewById(R.id.list_scope);

            this.mListener = myItemOnClickListener;
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                mListener.onItemOnClick(v, getAdapterPosition());
            }
        }
    }

    private Context mContext;
    private MyListView mMyListView;
    public boolean onScope = false, onHold = false;

    private MyItemOnClickListener mMyItemOnClickListener;


    public ContentAdapter(Context content, MyListView mlv) {
        this.mContext = content;
        this.mMyListView = mlv;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

//        int temp = 0;
        if (position % 2 == 0) {
            holder.itemView.setBackgroundColor(0x1FFFFFFF);
        } else {
            holder.itemView.setBackgroundColor(0x00000000);
        }

//        if (position == 0) {
//            holder.txt1.setText("1");
//            holder.txt2.setText("value" + temp);
//        } else if (position == 1) {
//            holder.txt1.setText("2");
//            holder.txt2.setText("value" + temp);
//        } else {
//            holder.txt1.setText("3");
//            holder.txt2.setText("value" + temp);
//        }

        if (onScope) {
            if (position % 2 == 0) {
                holder.txt.setVisibility(View.VISIBLE);
                holder.scope_area.setVisibility(View.GONE);
                holder.ssv.stop();
                Var data = (Var) mContent.list.get(position / 2);
                holder.txt1.setText(data.getTag());
                holder.txt2.setText(data.getStr());
            } else {
                holder.txt.setVisibility(View.GONE);
                holder.scope_area.setVisibility(View.VISIBLE);
                holder.ssv.update(position / 2);

            }
        } else {
            holder.txt.setVisibility(View.VISIBLE);
            holder.scope_area.setVisibility(View.GONE);
            holder.ssv.stop();
            Var data = (Var) mContent.list.get(position);
            holder.txt1.setText(data.getTag());
            holder.txt2.setText(data.getStr());
        }
//        temp++;


//        holder.ssv.setVisibility(onScope ? View.VISIBLE : View.GONE);
//        if (onScope)
//            holder.ssv.update(position);
//        else
//            holder.ssv.stop();

    }

    //    private int Len4SSV = 0;
    @Override
    public int getItemCount() {
        if (onScope)
            return 2 * mContent.dataLen;
        else
            return mContent.dataLen;
//        return Content.dataLen + Len4SSV;
//        return 3;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //LayoutInflater.from指定写法
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_data, parent, false);
        return new ViewHolder(v, mMyItemOnClickListener);
    }

    public void setOnScope(boolean set, boolean hold) {
        if (set != onScope) {
            onHold = hold;
            onScope = set;
            for (int i = 0; i < mContent.dataLen; i++) {
                if (set) {
//                    Len4SSV++;
                    notifyItemInserted(2 * i + 1);
                } else {
//                    Len4SSV--;
//                    dAdapter.notifyItemRemoved(2 * i + 1);
                    notifyDataSetChanged();
//                    Len4SSV--;
                }
//                dAdapter.ssv.update();
            }
        }
    }

    public void onUpDate() {
        if (!onHold) {
            if (onScope) {
                for (int i = 0; i < mContent.dataLen; i++) {
                    notifyItemChanged(i * 2);
//                getItem(2*i+1);
//                dAdapter.ssv.update();
                    ContentAdapter.ViewHolder vh = (ContentAdapter.ViewHolder) mMyListView.findViewHolderForAdapterPosition(2 * i + 1);
                    if (vh != null) {
                        vh.ssv.update(i);
                        vh.scope_txt1.setText(vh.ssv.min + "");
                        vh.scope_txt2.setText(vh.ssv.range + "");
                        vh.scope_txt3.setText(vh.ssv.max + "");
                    }
                }
            } else {
                notifyDataSetChanged();
            }
        }
    }

    public interface MyItemOnClickListener {
        public void onItemOnClick(View view, int postion);
    }

    public void setItemOnClickListener(MyItemOnClickListener listener) {
        mMyItemOnClickListener = listener;
    }
//public class ContentAdapter extends BaseAdapter {
//    Context context;
//    TextView txt1, txt2;
//    private boolean onScope = false;
//    private boolean onScope_lst = true;
//
//    public int getCount() {
////        return Content.dataLen;
//        return 3;
//    }
//
//    public Object getItem(int position) {
//        // TODO Auto-generated method stub
//        return position;
//    }
//
//    public long getItemId(int position) {
//        // TODO Auto-generated method stub
//        return position;
//    }
//
//    int temp = 0;
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        // TODO Auto-generated method stub
//        final LayoutInflater inflater = (LayoutInflater) context
//                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        final View v = inflater.inflate(R.layout.list_data, null);
//
//        txt1 = (TextView) v.findViewById(R.id.txt1);
//        txt2 = (TextView) v.findViewById(R.id.txt2);
//
//        if (position % 2 == 0) {
//            v.setBackgroundColor(0x1FFFFFFF);
//        }
//        if (position == 0) {
//            txt1.setText("1");
//            txt2.setText("value" + temp);
//        } else if (position == 1) {
//            txt1.setText("2");
//            txt2.setText("value" + temp);
//        } else {
//            txt1.setText("3");
//            txt2.setText("value" + temp);
//        }
////        Var data = (Var)Content.list.get(position);
////        txt1.setText(data.getTag());
////        txt2.setText(data.getStr());
//        temp++;
//
//        SimpleScopeView ssv = (SimpleScopeView) v.findViewById(R.id.list_scope);
//
//        ssv.setVisibility(onScope? View.VISIBLE:View.GONE);
//        if (onScope)
//            ssv.update(position);
//        else
//            ssv.stop();
//
//        return v;
//}
//
//    public ContentAdapter(Context ct) {
//        context = ct;
//    }
//
//    @Override
//    public void notifyDataSetChanged() {
//        super.notifyDataSetChanged();
//    }

//    public void setOnScope(boolean set) {
//        onScope = set;
//    }
}

