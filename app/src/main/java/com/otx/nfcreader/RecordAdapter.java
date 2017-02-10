package com.otx.nfcreader;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.otx.nfcreader.card.Record;


import java.util.List;

/**
 * Created by wangjun on 2017/2/10.
 */
public class RecordAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Record> RecordList;
    private Context context;
    private LayoutInflater mLayoutInflater;
    private boolean isHK=false;

    public RecordAdapter(Context context, List<Record> list) {
        this.context = context;
        this.mLayoutInflater = LayoutInflater.from(context);
        RecordList = list;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RecordViewHolder(mLayoutInflater.inflate(R.layout.record_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Record Record=RecordList.get(position);

        ((RecordViewHolder) holder).date.setText(Record.getDate());
        ((RecordViewHolder)holder).time.setText(Record.getTime());
        ((RecordViewHolder)holder).otherinfo.setText(Record.getOther());
        ((RecordViewHolder)holder).charge.setText(Record.getMoney()+(isHK?"港币":"元"));
    }

    @Override
    public int getItemCount() {
        return RecordList.size();
    }

    public static class RecordViewHolder extends RecyclerView.ViewHolder {
        TextView date;
        TextView time;
        TextView otherinfo;
        TextView charge;
        public RecordViewHolder(View view) {
            super(view);
            date=(TextView)view.findViewById(R.id.date);
            time=(TextView)view.findViewById(R.id.time);
            otherinfo=(TextView)view.findViewById(R.id.otherinfo);
            charge=(TextView) view.findViewById(R.id.charge);
        }
    }

    public void setHK(boolean HK) {
        isHK = HK;
    }
}
