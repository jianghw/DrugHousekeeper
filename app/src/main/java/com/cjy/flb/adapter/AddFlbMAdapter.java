package com.cjy.flb.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cjy.flb.R;
import com.socks.library.KLog;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015/12/7 0007.
 */
public class AddFlbMAdapter extends BaseAdapter {
    private final ArrayList<String> list;
    private final LayoutInflater inflater;
    private final Context context;

    public AddFlbMAdapter(Context context, ArrayList<String> list)
    {
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount()
    {
        return list.size();
    }

    @Override
    public Object getItem(int position)
    {
        return list.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_add_flbm_adapter, null);
            holder.mTextName = (TextView) convertView.findViewById(R.id.tv_body);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.mTextName.setText(list.get(position));
        return convertView;
    }

    class ViewHolder {
        TextView mTextName;
    }
}
