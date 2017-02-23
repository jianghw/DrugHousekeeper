package com.cjy.flb.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cjy.flb.R;
import com.cjy.flb.bean.LinkmanID;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015/12/11 0011.
 */
public class DistressCallAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<LinkmanID> list;
    private LayoutInflater inflater;

    public DistressCallAdapter(Context context, ArrayList<LinkmanID> list) {
        this.list = list;
        this.context = context;
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
            convertView = inflater.inflate(R.layout.item_distress_call_adapter, null);
            holder.mImageHead = (ImageView) convertView.findViewById(R.id.img_head);
            holder.mTextName = (TextView) convertView.findViewById(R.id.tv_name);
            holder.mTextPhoneName = (TextView) convertView.findViewById(R.id.tv_phoneName);
            holder.mTextPhone = (TextView) convertView.findViewById(R.id.tv_phone);
            holder.mLinear = (LinearLayout) convertView.findViewById(R.id.lrlayout_imag);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        LinkmanID bID = list.get(position);
        switch (bID.getId()) {
            case 0:
                holder.mTextPhoneName.setText(context.getString(R.string.guardian_1));
                break;
            case 1:
                holder.mTextPhoneName.setText(context.getString(R.string.guardian_2));
                break;
            case 2:
                holder.mTextPhoneName.setText(context.getString(R.string.doctor));
                break;
        }
        if(position==2){
            holder.mLinear.setVisibility(View.GONE);
        }else{
            holder.mLinear.setVisibility(View.VISIBLE);
        }

        holder.mTextName.setText(bID.getName());
        holder.mTextPhone.setText(bID.getPhone());
        return convertView;
    }

    class ViewHolder {
        ImageView mImageHead;
        TextView mTextName;
        TextView mTextPhoneName;
        TextView mTextPhone;
        LinearLayout mLinear;
    }
}
