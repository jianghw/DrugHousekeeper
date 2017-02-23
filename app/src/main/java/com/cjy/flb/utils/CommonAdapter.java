package com.cjy.flb.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by lvzho on 2015/4/29 0029.
 */
public abstract class CommonAdapter<T> extends BaseAdapter {
    protected List<T> mDatas;
    private LayoutInflater inflater;
    private int layoutId;

    public CommonAdapter(List<T> mDatas, Context context,int layoutId) {
        this.mDatas = mDatas;
        this.inflater = LayoutInflater.from(context);
        this.layoutId = layoutId;
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = ViewHolder.getViewHolder(parent,convertView,inflater,layoutId,position);
        convert(viewHolder, mDatas.get(position),position);
        return viewHolder.getConvertView();
    }
    public abstract void convert(ViewHolder viewHolder,T t,int position);
}
