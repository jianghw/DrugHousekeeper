package com.cjy.flb.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cjy.flb.R;
import com.cjy.flb.activity.MainActivity;
import com.cjy.flb.activity.MyApplication;
import com.cjy.flb.activity.SetDayOfWeek;
import com.cjy.flb.activity.SetRepeatDrug;
import com.cjy.flb.bean.PullSetDrugsBean;
import com.cjy.flb.customView.MedicamentInformation;
import com.cjy.flb.dao.DaoHolder;
import com.cjy.flb.dao.SetMedicTimeDao;
import com.cjy.flb.db.SetMedicTime;
import com.cjy.flb.event.MedicineInfoEvent;
import com.cjy.flb.event.PerodOfDayEvent;
import com.cjy.flb.utils.MHttpUtils;
import com.cjy.flb.utils.SharedPreUtil;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseStream;
import com.lidroid.xutils.http.client.HttpRequest;
import com.socks.library.KLog;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2015/12/1 0001.
 */
public class SetFragment4 extends BaseFragment implements View.OnClickListener, View.OnLongClickListener {

    @Bind(R.id.tv_week)
    TextView mTvWeek;
    @Bind(R.id.mi_morn)
    MedicamentInformation mMiMorn;
    @Bind(R.id.mi_noon)
    MedicamentInformation mMiNoon;
    @Bind(R.id.mi_afternoon)
    MedicamentInformation mMiAfternoon;
    @Bind(R.id.mi_night)
    MedicamentInformation mMiNight;
    @Bind(R.id.srl_refresh)
    SwipeRefreshLayout mSrlRefresh;
    @Bind(R.id.rl_set_week)
    RelativeLayout mSetWeek;

    private static final String AM = "MORN";
    private static final String NOON = "NOON";
    private static final String PM = "AFTERNOON";
    private static final String NIGHT = "NIGHT";

    private final FlbHandle mHandler = new FlbHandle(this);

    static class FlbHandle extends Handler {
        WeakReference<SetFragment4> weakReference;

        public FlbHandle(SetFragment4 fragment)
        {
            weakReference = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(Message msg)
        {
            SetFragment4 fragment = weakReference.get();
            if (fragment != null) {
                switch (msg.what) {
                    case 100:

                        break;
                    case 200:

                        break;
                    case 300:

                        break;
                    case 400:
                        MainActivity m = (MainActivity) fragment.getActivity();
                        m.showAlerDialog();
                        break;
                    default:
                        break;
                }
            }
            super.handleMessage(msg);
        }
    }

    @Override
    protected LoadingPager.LoadResult load()
    {
        RequestParams requestParams = new RequestParams();
        String accessToken = SharedPreUtil.getString("access_token");
        requestParams.addHeader("Authorization", "Bearer " + accessToken);
        HttpUtils httpUtils = new HttpUtils();
        httpUtils.configCurrentHttpCacheExpiry(0);
        try {
            ResponseStream responseStream = httpUtils.sendSync(
                    HttpRequest.HttpMethod.GET, MHttpUtils.PULL_DOWN_URL + MyApplication.flbId, requestParams);
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
                final JsonArray jsonArray = jsonElement.getAsJsonArray();
                MyApplication.getDaoSession().runInTx(new Runnable() {
                    @Override
                    public void run()
                    {
                        saveToDatabase(jsonArray);
                    }
                });
            }
        } catch (Exception e) {
            if (e.getMessage().equals("Unauthorized")) {
                Message message = mHandler.obtainMessage(400);
                mHandler.sendMessage(message);
            }
        }
        return LoadingPager.LoadResult.WRITE;
    }

    @Override
    protected View setView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_set_4, container, false);
        ButterKnife.bind(this, view);
        if (isAdded() && !EventBus.getDefault().isRegistered(this)) {
            initListener();
            EventBus.getDefault().registerSticky(this);
        }
        return view;
    }

    /**
     * 保存数据到数据表 SetMedicTime
     *
     * @param jsonArray 1-28个格子
     */
    private void saveToDatabase(JsonArray jsonArray)
    {
        if (jsonArray.size() > 0) {
            for (JsonElement jElement : jsonArray) {
                //第一个number-0 药物情况
                PullSetDrugsBean pullSetDrugsBean = new Gson().fromJson(jElement, PullSetDrugsBean.class);
                //药物数据 * medicine_name : 阿莫西林 quantity : 1
                List<PullSetDrugsBean.MedicineListEntity> list = pullSetDrugsBean.getMedicine_list();
                if (list.size() > 0) {//下拉有药物时
                    for (PullSetDrugsBean.MedicineListEntity mEntity : list) {
                        QueryBuilder<SetMedicTime> builder = DaoHolder.getSetMedicTimeDao(
                                SetMedicTimeDao.Properties.Device_uid.eq(MyApplication.flbId),
                                SetMedicTimeDao.Properties.Number.eq(pullSetDrugsBean.getNumber()),
                                SetMedicTimeDao.Properties.Medicine.eq(mEntity.getMedicine().getProduct_name_zh()));
                        long mCount = builder.buildCount().count();
                        if (mCount == 1) {//查到同药物，更新时间
                            SetMedicTime smt = builder.build().forCurrentThread().unique();
                            smt.setEat_time(pullSetDrugsBean.getEat_time());
                            smt.setQuantity(mEntity.getQuantity());
                            smt.setMedicine_id(mEntity.getMedicine().getId());
                            smt.setUnit(mEntity.getMedicine().getProduction_unit());
                            DaoHolder.updateInTxSetMedicTime(smt);
                        } else if (mCount == 0) {
                            //数据库中的药物查不到时，新插
                            int number = pullSetDrugsBean.getNumber();
                            SetMedicTime smt = new SetMedicTime(null, String.valueOf(number),
                                    MyApplication.flbId,
                                    mEntity.getMedicine().getProduct_name_zh(),
                                    mEntity.getQuantity(),
                                    mEntity.getMedicine().getId(),
                                    mEntity.getMedicine().getProduction_unit(),
                                    pullSetDrugsBean.getEat_time());
                            DaoHolder.insertInTxSetMedicTime(smt);
                        }
                    }
                }
            }
        }
    }

    @Override
    protected LoadingPager.LoadResult readDatabase()
    {
        //初始化显示
        accordingToNewFigures();
        return LoadingPager.LoadResult.READ;
    }

    /**
     * 按数据库中数据显示设置项目
     */
    private void accordingToNewFigures()
    {
        mMiMorn.setNeverAddMedicament(SetFragment4.AM);
        mMiNoon.setNeverAddMedicament(SetFragment4.NOON);
        mMiAfternoon.setNeverAddMedicament(SetFragment4.PM);
        mMiNight.setNeverAddMedicament(SetFragment4.NIGHT);

        SimpleDateFormat sdf = new SimpleDateFormat("E");
        int[] days;
        if (mTvWeek.getText().toString().equals(getString(R.string.today))) {
            days = weekToDatabaseId(sdf.format(new Date()));
        } else {
            days = weekToDatabaseId(mTvWeek.getText().toString());
        }

        for (int i = 0; i < days.length; i++) {
            QueryBuilder<SetMedicTime> builder = DaoHolder.getSetMedicTimeDao(
                    SetMedicTimeDao.Properties.Device_uid.eq(MyApplication.flbId),
                    SetMedicTimeDao.Properties.Number.eq(days[i]));

            if (builder.buildCount().count() > 0) {//数据库中有药物时
                List<SetMedicTime> list = builder.build().forCurrentThread().list();
                SetMedicTime smt = list.get(0);
                switch (i) {
                    case 0:
                        mMiMorn.setVisibility(View.VISIBLE);
                        if (TextUtils.isEmpty(smt.getEat_time())) {//设置时间为空时
                            mMiMorn.setSetMedicamentImageView(1, getString(R.string.set_time));
                        } else {
                            mMiMorn.setSetMedicamentImageView(2, smt.getEat_time());
                        }
                        break;
                    case 1:
                        mMiNoon.setVisibility(View.VISIBLE);
                        if (TextUtils.isEmpty(smt.getEat_time())) {//设置时间为空时
                            mMiNoon.setSetMedicamentImageView(3, getString(R.string.set_time));
                        } else {
                            mMiNoon.setSetMedicamentImageView(4, smt.getEat_time());
                        }
                        break;
                    case 2:
                        mMiAfternoon.setVisibility(View.VISIBLE);
                        if (TextUtils.isEmpty(smt.getEat_time())) {//设置时间为空时
                            mMiAfternoon.setSetMedicamentImageView(5, getString(R.string.set_time));
                        } else {
                            mMiAfternoon.setSetMedicamentImageView(6, smt.getEat_time());
                        }
                        break;
                    case 3:
                        mMiNight.setVisibility(View.VISIBLE);
                        if (TextUtils.isEmpty(smt.getEat_time())) {//设置时间为空时
                            mMiNight.setSetMedicamentImageView(7, getString(R.string.set_time));
                        } else {
                            mMiNight.setSetMedicamentImageView(8, smt.getEat_time());
                        }
                        break;
                }
            }
        }
    }

    public void initListener()
    {
        mSetWeek.setOnClickListener(this);

        mMiMorn.setOnClickListener(this);
        mMiNoon.setOnClickListener(this);
        mMiAfternoon.setOnClickListener(this);
        mMiNight.setOnClickListener(this);

        setUnaddDialog(mMiMorn);
        setUnaddDialog(mMiNoon);
        setUnaddDialog(mMiAfternoon);
        setUnaddDialog(mMiNight);

        mSrlRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh()
            {
                mSrlRefresh.setRefreshing(true);
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run()
                    {
                        //3秒后停止刷新
                        if (mSrlRefresh != null) {
                            show();
                            mSrlRefresh.setRefreshing(false);
                        }
                    }
                }, 3000);
            }
        });
    }

    private void setUnaddDialog(final MedicamentInformation view)
    {
        view.getMedicamentImageView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                view.onLongListenerShowPopu();
            }
        });
    }

    @Override
    public void onDestroyView()
    {
        ButterKnife.unbind(this);
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId()) {
            case R.id.rl_set_week:
                Intent intent = new Intent(getActivity(), SetDayOfWeek.class);
                Bundle bundle = new Bundle();
                bundle.putString("week", mTvWeek.getText().toString());
                intent.putExtra("bundle", bundle);
                startActivity(intent);
                break;
            case R.id.mi_morn:
                toSetRepeatDrug(0);
                break;
            case R.id.mi_noon:
                toSetRepeatDrug(1);
                break;
            case R.id.mi_afternoon:
                toSetRepeatDrug(2);
                break;
            case R.id.mi_night:
                toSetRepeatDrug(3);
                break;
        }
    }

    //跳转设置页面
    private void toSetRepeatDrug(int i)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("E");
        int[] days;
        if (mTvWeek.getText().toString().equals(getString(R.string.today))) {
            days = weekToDatabaseId(sdf.format(new Date()));
        } else {
            days = weekToDatabaseId(mTvWeek.getText().toString());
        }
        Intent intent = new Intent(getActivity(), SetRepeatDrug.class);
        Bundle bundle = new Bundle();
        bundle.putString("number", String.valueOf(days[i]));//当天对应的四个number中的一个
        bundle.putInt("inDay", i);//当天中某时段
        bundle.putString("week", mTvWeek.getText().toString());//星期~
        intent.putExtra("bundle", bundle);
        startActivity(intent);
    }

    @Override
    public boolean onLongClick(View v)
    {
        switch (v.getId()) {
            case R.id.mi_morn:
                mMiMorn.onLongListenerShowPopu();
                break;
            case R.id.mi_noon:
                mMiNoon.onLongListenerShowPopu();
                break;
            case R.id.mi_afternoon:
                mMiAfternoon.onLongListenerShowPopu();
                break;
            case R.id.mi_night:
                mMiNight.onLongListenerShowPopu();
                break;
        }
        return false;
    }

    /**
     * 设置好药物后返回
     *
     * @param event
     */
    public void onEventMainThread(final MedicineInfoEvent event)
    {
        if (event.isTrue()) {
            show();
        }
    }

    /**
     * 删除时间段数据后，显示
     *
     * @param event
     */
    public void onEventMainThread(PerodOfDayEvent event)
    {
        String week = event.getWeek();
        HashMap<String, Boolean> hasMap = event.getHasMap();
        SimpleDateFormat sdf = new SimpleDateFormat("E");

        if (week.equals(sdf.format(new Date()))) {
            mTvWeek.setText(getString(R.string.today));
        } else {
            mTvWeek.setText(week);
        }
        accordingToNewFigures();

        if (hasMap.containsKey(SetFragment4.AM)) {
            mMiMorn.setVisibility(View.VISIBLE);
        } else {
            mMiMorn.setVisibility(View.GONE);
        }
        if (hasMap.containsKey(SetFragment4.NOON)) {
            mMiNoon.setVisibility(View.VISIBLE);
        } else {
            mMiNoon.setVisibility(View.GONE);
        }
        if (hasMap.containsKey(SetFragment4.PM)) {
            mMiAfternoon.setVisibility(View.VISIBLE);
        } else {
            mMiAfternoon.setVisibility(View.GONE);
        }
        if (hasMap.containsKey(SetFragment4.NIGHT)) {
            mMiNight.setVisibility(View.VISIBLE);
        } else {
            mMiNight.setVisibility(View.GONE);
        }
    }

    /**
     * 每星期对应的number
     *
     * @param week
     * @return
     */
    private int[] weekToDatabaseId(String week)
    {
        int[] days;
        if (week.equals(getString(R.string.MON))
                || week.equals(getString(R.string.MON_X))) {
            days = new int[]{0, 1, 2, 3};
        } else if (week.equals(getString(R.string.TUE))
                || week.equals(getString(R.string.TUE_X))) {
            days = new int[]{4, 5, 6, 7};
        } else if (week.equals(getString(R.string.WED))
                || week.equals(getString(R.string.WED_X))) {
            days = new int[]{8, 9, 10, 11};
        } else if (week.equals(getString(R.string.THU))
                || week.equals(getString(R.string.THU_X))) {
            days = new int[]{12, 13, 14, 15};
        } else if (week.equals(getString(R.string.FRI))
                || week.equals(getString(R.string.FRI_X))) {
            days = new int[]{16, 17, 18, 19};
        } else if (week.equals(getString(R.string.SAT))
                || week.equals(getString(R.string.SAT_X))) {
            days = new int[]{20, 21, 22, 23};
        } else if (week.equals(getString(R.string.SUN))
                || week.equals(getString(R.string.SUN_X))) {
            days = new int[]{24, 25, 26, 27};
        } else {
            days = new int[4];
        }
        return days;
    }
}
