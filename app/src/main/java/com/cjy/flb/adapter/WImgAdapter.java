package com.cjy.flb.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.cjy.flb.R;
import com.cjy.flb.enums.Medicine;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015/12/3 0003.
 */
public class WImgAdapter extends BaseAdapter {
    private ArrayList<Medicine> list;
    private LayoutInflater inflater;

    public WImgAdapter(Context context, ArrayList<Medicine> imgList) {
        this.list = imgList;
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
        convertView = inflater.inflate(R.layout.item_img_wimgadapter, null);
        convertView.setLayoutParams(new ListView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                parent.getMeasuredHeight() / 7));
        holder.mImageView = (ImageView) convertView.findViewById(R.id.imageView);

        Medicine m = list.get(position);
        switch (m) {
            case NORMAL:
                holder.mImageView.setImageResource(R.drawable.weekf_normal);
                break;
            case UNORMAL:
                holder.mImageView.setImageResource(R.drawable.weekf_abnormal);
                break;
            case USET://没设置
                holder.mImageView.setImageBitmap(null);
                break;
            case KONG: //没到服药时间，即没数据
                holder.mImageView.setImageResource(R.drawable.weekf_unset);
                break;
            default:
                holder.mImageView.setImageBitmap(null);
                break;
        }
        return convertView;
    }

    class ViewHolder {
        ImageView mImageView;
    }
}
