package com.cjy.flb.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cjy.flb.R;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/1/4 0004.
 */
public class MProblemAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private ArrayList<String> list;

    public MProblemAdapter(Context context, ArrayList<String> medicList) {
        this.list = medicList;
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
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_mproblem_adapter, null);

            holder.mTextName = (TextView) convertView.findViewById(R.id.textView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String str = list.get(position).split("/")[0];
        holder.mTextName.setText(str);

        return convertView;
    }

    class ViewHolder {
        TextView mTextName;

    }
}
