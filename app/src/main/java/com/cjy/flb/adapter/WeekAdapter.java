package com.cjy.flb.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.cjy.flb.R;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015/12/3 0003.
 * weekFragment中涉及到的一周日期
 */
public class WeekAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<String> list;
    private LayoutInflater inflater;

    public WeekAdapter(Context context, ArrayList<String> dateList) {
        this.context = context;
        this.list = dateList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        holder = new ViewHolder();
        convertView = inflater.inflate(R.layout.item_textview_weekadapter, null);
        //设置每个子item 等高
        convertView.setLayoutParams(new ListView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                parent.getMeasuredHeight() / 7));
        holder.mTextView = (TextView) convertView.findViewById(R.id.textView);
        if (position == 5 || position == 6) {
            holder.mTextView.setTextColor(Color.GRAY);
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(list.get(position).split("-")[1]).append("-").append(list.get(position).split("-")[2]);
        holder.mTextView.setText(stringBuilder.toString());
        return convertView;
    }

    class ViewHolder {
        TextView mTextView;
    }
}
