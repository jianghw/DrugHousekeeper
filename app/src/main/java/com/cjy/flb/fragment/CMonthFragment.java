package com.cjy.flb.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.cjy.flb.R;
import com.cjy.flb.adapter.MonthAdapter;
import com.cjy.flb.bean.DateQueryMedic;
import com.cjy.flb.event.WeekDateEven;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.socks.library.KLog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2015/12/3 0003.
 */
public class CMonthFragment extends BaseFragment {

    @Bind(R.id.gridView)
    GridView mGridView;

    @Bind(R.id.tv_cmonth)
    TextView mCmonth;

    private ArrayList<Integer> cDateList = new ArrayList<>();//存放每月的天数
    private HashMap<String, Boolean> cDhashMap = new HashMap<>();
    /**
     * 加载日期 1-31
     */
    private MonthAdapter monthAdapter;

    @Override
    protected LoadingPager.LoadResult load() {
        return LoadingPager.LoadResult.WRITE;
    }

    @Override
    public View setView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cmonth, container, false);
        ButterKnife.bind(this, view);
        if (isAdded()) {
            initListener();
        }
        return view;
    }

    public LoadingPager.LoadResult readDatabase() {
        try {
            if (NoteFragment.jArrayCMonth != null
                    && NoteFragment.jArrayCMonth.size() > 0 && cDhashMap.size() == 0) {
                HashMap<String, Boolean> hashMap = new HashMap<>();
                for (JsonElement element : NoteFragment.jArrayCMonth) {
                    DateQueryMedic dateQueryMedic = new Gson().fromJson(element, DateQueryMedic.class);
                    if (!dateQueryMedic.isTaken()) {//只有不服药
                        String time = dateQueryMedic.getEat_medicine_time().split("T")[0];
                        hashMap.put(time + "#" + dateQueryMedic.getNumber(), true);
                    }
                    if (dateQueryMedic.isTaken()) {
                        String time = dateQueryMedic.getEat_medicine_time().split("T")[0];
                        if (hashMap.containsKey(time + "#" + dateQueryMedic.getNumber())) {
                            hashMap.remove(time + "#" + dateQueryMedic.getNumber());
                        }
                    }
                }
                for (Map.Entry entry : hashMap.entrySet()) {
                    String key = entry.getKey().toString().split("#")[0];
                    cDhashMap.put(key,true);
                }
            }

            if (cDateList.size() == 0) {
                //当前日期
                Calendar c = Calendar.getInstance();
                //                c.add(Calendar.MONTH, 0);
                c.set(Calendar.DAY_OF_MONTH, 1);//设置为1号,当前日期既为本月第一天
                //本月的最大天数
                int fate = c.getActualMaximum(Calendar.DAY_OF_MONTH);
                int week = c.get(Calendar.DAY_OF_WEEK) - 1;
                KLog.a(week);
                //当本月第一天为星期日时
                if (week == 0) {
                    for (int i = 0; i < 6; i++) {
                        cDateList.add(0);
                    }
                } else {
                    //设置本月第一天的位置
                    for (int i = 0; i < week - 1; i++) {
                        cDateList.add(0);
                    }
                }
                for (int j = 1; j <= fate; j++) {
                    cDateList.add(j);
                }
            }

            monthAdapter = new MonthAdapter(getActivity(), cDateList, cDhashMap);
            mGridView.setAdapter(monthAdapter);
            MonthFragment f = (MonthFragment) getParentFragment();
            f.setTextViewDate(String.valueOf(cDhashMap.size()));

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM", Locale.SIMPLIFIED_CHINESE);
            String day = sdf.format(new Date());
            mCmonth.setText(day);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return LoadingPager.LoadResult.READ;
    }

    public int getAbnormalNumberOfDays() {
        int size = 0;
        if (cDhashMap != null) {
            size = cDhashMap.size();
        }
        return size;
    }

    public void initListener() {
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView tv = (TextView) view.findViewById(R.id.textView);
                if (!TextUtils.isEmpty(tv.getText())) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.SIMPLIFIED_CHINESE);
                    String[] string = sdf.format(new Date()).split("-");
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(string[0]).append("-").append(string[1]).append("-")
                            .append(changLongToString(tv.getText().toString().trim()));

                    MonthFragment monthFragment = (MonthFragment) getParentFragment();
                    NoteFragment note = (NoteFragment) monthFragment.getParentFragment();
                    note.mRdioGroupNoteFrment.check(R.id.radioButton2);

                    EventBus.getDefault().postSticky(new WeekDateEven(stringBuilder.toString()));
                }
            }
        });

    }

    private String changLongToString(String hour) {
        String sHour;
        if (Integer.parseInt(hour) < 10) {
            sHour = "0" + hour;
        } else {
            sHour = hour;
        }
        return sHour;
    }

    @Override
    public void onDestroyView() {
        if (!cDateList.isEmpty()) cDateList.clear();
        if (!cDhashMap.isEmpty()) cDhashMap.clear();
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

}
