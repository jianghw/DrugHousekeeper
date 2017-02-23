package com.cjy.flb.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.cjy.flb.R;
import com.cjy.flb.activity.MyApplication;
import com.cjy.flb.utils.MHttpUtils;
import com.cjy.flb.utils.SharedPreUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseStream;
import com.lidroid.xutils.http.client.HttpRequest;
import com.socks.library.KLog;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2015/12/1 0001.
 */
public class NoteFragment extends BaseFragment {
    @Bind(R.id.radioButton)
    RadioButton mRadioButton;
    @Bind(R.id.radioButton2)
    RadioButton mRadioButton2;
    @Bind(R.id.rdioGroup_noteFrment)
    RadioGroup mRdioGroupNoteFrment;
    @Bind(R.id.fragment_note)
    FrameLayout mFragmentNote;
    @Bind(R.id.scrollView_note)
    SwipeRefreshLayout mScrollViewNote;

    protected static JsonArray jArrayCMonth = null;//本月服药信息
    protected static JsonArray jALastMonth = null;

    private WeekFragment weekFragment;
    private MonthFragment monthFragment;
    //默认显示位置
    private int currentPosition = 0;

    @Override
    protected LoadingPager.LoadResult load() {
        downCmonthData();
        downLmonthData();
        return LoadingPager.LoadResult.WRITE;
    }

    public void downCmonthData() {
        Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.DATE, 1);
        SimpleDateFormat simpleFormate = new SimpleDateFormat("yyyy-MM-dd", Locale.SIMPLIFIED_CHINESE);
        String firstDat = simpleFormate.format(calendar.getTime());
        // 本月的最后一天
        calendar.roll(Calendar.DATE, -1);
        String lastDat = simpleFormate.format(calendar.getTime());

        RequestParams requestParams = new RequestParams();
        String accessToken = SharedPreUtil.getString("access_token");
        requestParams.addHeader("Authorization", "Bearer " + accessToken);
        requestParams.addBodyParameter("device_uid", MyApplication.flbId);
        requestParams.addBodyParameter("begin_day", firstDat);
        requestParams.addBodyParameter("end_day", lastDat);
        HttpUtils httpUtils = new HttpUtils();
        httpUtils.configCurrentHttpCacheExpiry(0);
        try {
            ResponseStream responseStream = httpUtils.sendSync(HttpRequest.HttpMethod.POST,
                    MHttpUtils.DATE_INTERVAL_MEDIC, requestParams);
            String entity = responseStream.readString();
            KLog.json(entity);
            JsonElement json = new JsonParser().parse(entity);
            JsonElement jsonElement = null;
            if (json.isJsonObject()) {
                JsonObject jsonObject = json.getAsJsonObject();
                jsonElement = jsonObject.get("response");
            }
            if (jsonElement != null && jsonElement.isJsonArray()) {
                //保存数据库
                jArrayCMonth = jsonElement.getAsJsonArray();
            }
        } catch (HttpException | IOException e) {
            e.printStackTrace();
        }
    }

    public void downLmonthData() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        SimpleDateFormat simpleFormate = new SimpleDateFormat("yyyy-MM-dd", Locale.SIMPLIFIED_CHINESE);
        String firstDat = simpleFormate.format(calendar.getTime());
        // 本月的最后一天
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.roll(Calendar.DAY_OF_MONTH, -1);
        String lastDat = simpleFormate.format(calendar.getTime());

        RequestParams requestParams = new RequestParams();
        String accessToken = SharedPreUtil.getString("access_token");
        requestParams.addHeader("Authorization", "Bearer " + accessToken);
        requestParams.addBodyParameter("device_uid", MyApplication.flbId);
        requestParams.addBodyParameter("begin_day", firstDat);
        requestParams.addBodyParameter("end_day", lastDat);
        HttpUtils httpUtils = new HttpUtils();
        httpUtils.configCurrentHttpCacheExpiry(0);
        try {
            ResponseStream responseStream = httpUtils.sendSync(
                    HttpRequest.HttpMethod.POST, MHttpUtils.DATE_INTERVAL_MEDIC, requestParams);
            String entity = responseStream.readString();
            KLog.json(entity);
            JsonElement json = new JsonParser().parse(entity);
            JsonElement jsonElement = null;
            if (json.isJsonObject()) {
                JsonObject jsonObject = json.getAsJsonObject();
                jsonElement = jsonObject.get("response");
            }
            if (jsonElement != null && jsonElement.isJsonArray()) {
                //保存数据
                jALastMonth = jsonElement.getAsJsonArray();
            }
        } catch (HttpException | IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected View setView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note, container, false);
        ButterKnife.bind(this, view);
        if (isAdded()) {
            initListener();
        }
        return view;
    }

    @Override
    protected LoadingPager.LoadResult readDatabase() {
        if(isAdded()){
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.fragment_note, getItem(currentPosition), String.valueOf(currentPosition)).commit();
        }
        return LoadingPager.LoadResult.READ;
    }

    public Fragment getItem(int position) {
        switch (position) {
            case 0://显示每月
                if (monthFragment == null) {
                    monthFragment = new MonthFragment();
                }
                return monthFragment;
            case 1://显示每周
                if (weekFragment == null) {
                    weekFragment = new WeekFragment();
                }
                return weekFragment;
            default:
                return null;
        }
    }

    public void initListener() {
        mRdioGroupNoteFrment.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radioButton:
                        setCurrentItem(0);
                        break;
                    case R.id.radioButton2:
                        setCurrentItem(1);
                        break;
                }
            }
        });

        mScrollViewNote.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mScrollViewNote != null) {
                    mScrollViewNote.setRefreshing(true);
                    (new Handler()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //3秒后停止刷新
                            //拉取2个月的网络数据
                            MHttpUtils.pullTwoMonthMedinInfo(getActivity(), MyApplication.flbId);
                            if (weekFragment != null && !getChildFragmentManager().beginTransaction().isEmpty()) {
                                getChildFragmentManager().beginTransaction()
                                        .remove(weekFragment)
                                        .commit();
                            }
                            weekFragment = null;
                            if (monthFragment != null && !getChildFragmentManager().beginTransaction().isEmpty()) {
                                getChildFragmentManager().beginTransaction()
                                        .remove(monthFragment)
                                        .commit();
                            }
                            monthFragment = null;
                            mScrollViewNote.setRefreshing(false);
                            show();
                        }
                    }, 3000);
                }
            }
        });
    }

    public void setCurrentItem(int i) {
        if (i != currentPosition) {
            FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
            hideAllFragments(fragmentTransaction);
            if (i == 0) {
                if (monthFragment == null) {
                    fragmentTransaction.add(R.id.fragment_note, getItem(i), "0");
                } else {
                    fragmentTransaction.show(monthFragment);
                }
            } else if (i == 1) {
                if (weekFragment == null) {
                    fragmentTransaction.add(R.id.fragment_note, getItem(i), "1");
                } else {
                    fragmentTransaction.show(weekFragment);
                }
            }
            fragmentTransaction.commit();
            currentPosition = i;
        }
    }

    private void hideAllFragments(FragmentTransaction fragmentTransaction) {
        if (monthFragment != null)
            fragmentTransaction.hide(monthFragment);
        if (weekFragment != null)
            fragmentTransaction.hide(weekFragment);
    }

    public void setMonthChangDate(int i) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM");
        if (i == 0) {//本月
            String day = sdf.format(new Date());
            mRadioButton2.setText(day);
        } else {//上月
            Calendar c = Calendar.getInstance();
            c.add(Calendar.MONTH, -1);
            String firstDat = sdf.format(c.getTime());
            mRadioButton2.setText(firstDat);
        }
    }

    public void setEnabled(boolean bag) {
        mScrollViewNote.setEnabled(bag);
    }

    @Override
    public void onDestroyView() {
        if (jALastMonth != null) jALastMonth = null;
        if (jArrayCMonth != null) jArrayCMonth = null;

        currentPosition = 0;
        ButterKnife.unbind(this);
        super.onDestroyView();
    }
}
