package com.cjy.flb.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.cjy.flb.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by Administrator on 2015/12/3 0003.
 */
public class MonthAdapter extends BaseAdapter {
    private final Context mContext;
    private HashMap<String, Boolean> hashMap;
    private ArrayList<Integer> list;
    private LayoutInflater inflater;

    public MonthAdapter(Context context, ArrayList<Integer> dateList, HashMap<String, Boolean> hashMap) {
        this.mContext=context;
        this.list = dateList;
        inflater = LayoutInflater.from(context);
        this.hashMap = hashMap;
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
            convertView = inflater.inflate(R.layout.item_tximage_monthadapter, null);
            convertView.setLayoutParams(new ListView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    parent.getMeasuredHeight() / 6));

            holder.mLinearLayout = (LinearLayout) convertView.findViewById(R.id.linearLayout);
            holder.mTextView = (TextView) holder.mLinearLayout.findViewById(R.id.textView);
            holder.mImageView = (ImageView) holder.mLinearLayout.findViewById(R.id.imageView);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

     /*   holder = new ViewHolder();
        convertView = inflater.inflate(R.layout.item_tximage_monthadapter, null);
        convertView.setLayoutParams(new ListView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                parent.getMeasuredHeight() / 6));

        holder.mLinearLayout = (LinearLayout) convertView.findViewById(R.id.linearLayout);
        holder.mTextView = (TextView) holder.mLinearLayout.findViewById(R.id.textView);
        holder.mImageView = (ImageView) holder.mLinearLayout.findViewById(R.id.imageView);*/

        int m = list.get(position);

        Calendar c = Calendar.getInstance();

        if (c.get(Calendar.DAY_OF_MONTH) == m) {
            holder.mLinearLayout.setBackgroundResource(R.drawable.monthf_cday);
        }
        SimpleDateFormat sdfmat = new SimpleDateFormat("yyyy-MM", Locale.SIMPLIFIED_CHINESE);
        String sDay = sdfmat.format(new Date());
        StringBuilder builder = new StringBuilder();

        if (m == 0) {
            holder.mTextView.setText("");
        } else {
            if (hashMap.size() > 0) {
              /*  for (String str : hashMap.keySet()) {
                    String day;
                    if (str.split("-")[2].startsWith("0")) {
                        day = str.split("-")[2].substring(1);//2015-04-06--6
                    } else {
                        day = str.split("-")[2];//2015-04-26--26
                    }
                    if (day.equals(String.valueOf(m))) {
                        holder.mImageView.setVisibility(View.VISIBLE);
                    }

                    if (day.equals(String.valueOf(c.get(Calendar.DAY_OF_MONTH)))&&day.equals(String.valueOf(m))) {
                        holder.mImageView.setVisibility(View.GONE);
                        holder.mLinearLayout.setBackgroundResource(R.drawable.monthf_cday_ex);
                    }
                }*/
                String day;
                if (m >= 10) {
                    day = builder.append(sDay).append("-").append(String.valueOf(m)).toString();
                }else{
                    day = builder.append(sDay).append("-").append(0).append(String.valueOf(m)).toString();
                }
                if (hashMap.containsKey(day)) {
                    holder.mImageView.setVisibility(View.VISIBLE);
                }

                if (String.valueOf(m).equals(String.valueOf(c.get(Calendar.DAY_OF_MONTH))) && hashMap.containsKey(day)) {
                    holder.mImageView.setVisibility(View.GONE);
                    holder.mLinearLayout.setBackgroundResource(R.drawable.monthf_cday_ex);
                }

            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd",Locale.SIMPLIFIED_CHINESE);
            String[] string = sdf.format(new Date()).split("-");
            SimpleDateFormat sdformat = new SimpleDateFormat("E");
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(string[0]).append("-").append(string[1]).append("-").append(changLongToString(m));
            Date day = null;
            try {
                day = sdf.parse(stringBuilder.toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            String week = sdformat.format(day);

            if (week.equals(mContext.getString(R.string.SAT_X)) || week.equals(mContext.getString(R.string.SUN_X))
                    ||week.equals(mContext.getString(R.string.SAT)) || week.equals(mContext.getString(R.string.SUN))) {
                holder.mTextView.setTextColor(Color.GRAY);
                holder.mTextView.setText(String.valueOf(m));
            } else {
                holder.mTextView.setTextColor(Color.BLACK);
                holder.mTextView.setText(String.valueOf(m));
            }
        }
        return convertView;
    }

    private String changLongToString(long hour) {
        String sHour;
        if (hour < 10) {
            sHour = "0" + String.valueOf(hour);
        } else {
            sHour = String.valueOf(hour);
        }
        return sHour;
    }

    class ViewHolder {
        LinearLayout mLinearLayout;
        TextView mTextView;
        ImageView mImageView;
    }
}
