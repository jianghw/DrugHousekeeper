package com.cjy.flb.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.cjy.flb.R;
import com.cjy.flb.activity.GSMActivityByMain;
import com.cjy.flb.activity.MainActivity;
import com.cjy.flb.activity.MyApplication;
import com.cjy.flb.activity.SetRepeatDrug;
import com.cjy.flb.activity.WifiChoiceActivity;
import com.cjy.flb.adapter.AddFlbMAdapter;
import com.cjy.flb.bean.DateQueryMedic;
import com.cjy.flb.bean.PullSetDrugsBean;
import com.cjy.flb.dao.BoxDao;
import com.cjy.flb.dao.DaoHolder;
import com.cjy.flb.dao.IsNotifDao;
import com.cjy.flb.dao.SetMedicTimeDao;
import com.cjy.flb.db.Box;
import com.cjy.flb.db.IsNotif;
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
import com.google.gson.JsonSyntaxException;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.ResponseStream;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.socks.library.KLog;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2015/12/1 0001.
 */
public class FLBFragment extends BaseFragment implements AdapterView.OnItemClickListener {

    @Bind(R.id.tv_date_flb)
    TextView mTvDateFlb;
    @Bind(R.id.tv_week_flb)
    TextView mTvWeekFlb;
    @Bind(R.id.tv_name_flb)
    TextView mTvNameFlb;

    @Bind(R.id.img_mon_flb)
    ImageView mImgMonFlb;
    @Bind(R.id.lv_mon_flb)
    ListView mLvMonFlb;
    @Bind(R.id.img_non_flb)
    ImageView mImgNonFlb;
    @Bind(R.id.lv_non_flb)
    ListView mLvNonFlb;
    @Bind(R.id.lv_aftern_flb)
    ListView mLvAfternFlb;
    @Bind(R.id.img_aftern_flb)
    ImageView mImgAfternFlb;
    @Bind(R.id.lv_even_flb)
    ListView mLvEvenFlb;
    @Bind(R.id.img_even_flb)
    ImageView mImgEvenFlb;
    @Bind(R.id.tv_time_flb)
    TextView mTvTimeFlb;
    @Bind(R.id.scrollView_flb)
    SwipeRefreshLayout mScrollViewFlb;
    @Bind(R.id.image_online)
    ImageView mImageOnline;
    @Bind(R.id.tv_online)
    TextView mTvOnline;
    @Bind(R.id.tv_am)
    TextView mTvAm;
    @Bind(R.id.tv_noon)
    TextView mTvNoon;
    @Bind(R.id.tv_pm)
    TextView mTvPm;
    @Bind(R.id.tv_bed)
    TextView mTvBed;
    @Bind(R.id.lr_un_line)
    LinearLayout mLrUnLine;

    /**
     * 各个时段存放的药物
     */
    private ArrayList<String> listMon = new ArrayList<>();
    private ArrayList<String> listNON = new ArrayList<>();
    private ArrayList<String> listAFTERNON = new ArrayList<>();
    private ArrayList<String> listEVEN = new ArrayList<>();
    /**
     * 吃药时间
     */
    private String eatTimeMon = null;
    private String eatTimeNon = null;
    private String eatTimeAfter = null;
    private String eatTimeEven = null;
    /**
     * 开始倒计时的终点
     */
    private long cMSeconds;
    /**
     * 计划时间
     */
    private long msMon = 0;
    private long msNon = 0;
    private long msAFTER = 0;
    private long msEVEN = 0;

    private String accessToken;

    //用于循环跑动的线程
    private Thread timeThread;
    private int timeCount = 0;


    private final FlbHandle mHandler = new FlbHandle(this);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    static class FlbHandle extends Handler {
        WeakReference<FLBFragment> weakReference;

        public FlbHandle(FLBFragment fragment) {
            weakReference = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            FLBFragment fragment = weakReference.get();
            if (fragment != null) {
                switch (msg.what) {
                    case 100:
                        Bundle bundle = msg.getData();
                        if (fragment.mTvTimeFlb != null && bundle != null) {
                            String sHour = bundle.getString("sHour", "00");
                            String sMinutes = bundle.getString("sMinutes", "00");
                            String sSeconds = bundle.getString("sSeconds", "00");
                            fragment.mTvTimeFlb.setText(sHour + ":" + sMinutes + ":" + sSeconds);
                        }
                        break;
                    case 200:
                        if (fragment.mTvTimeFlb != null)
                            fragment.mTvTimeFlb.setText("--:--:--");
                        break;
                    case 300:
                        fragment.setIsOnline();
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

    /**
     * 登陆时加载页面
     * 保存数据库设置药物
     * 服药记录
     */
    @Override
    protected LoadingPager.LoadResult load() {
        loadMedicineRecord();
        loadMedicineInfo();
        return LoadingPager.LoadResult.WRITE;
    }

    private void loadMedicineInfo() {
        RequestParams requestParams = new RequestParams();
        requestParams.addHeader("Authorization", "Bearer " + accessToken);
        HttpUtils httpUtils = new HttpUtils();
        httpUtils.configCurrentHttpCacheExpiry(0);
        try {
            ResponseStream responseStream = httpUtils.sendSync(HttpRequest.HttpMethod.GET,
                    MHttpUtils.PULL_DOWN_URL + MyApplication.flbId, requestParams);
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
                    public void run() {
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
    }


    /**
     * 下拉服药记录
     */
    private void loadMedicineRecord() {
        RequestParams requestParams = new RequestParams();
        accessToken = SharedPreUtil.getString("access_token");
        requestParams.addHeader("Authorization", "Bearer " + accessToken);
        requestParams.addBodyParameter("device_uid", MyApplication.flbId);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.SIMPLIFIED_CHINESE);
        requestParams.addBodyParameter("day", sdf.format(new Date()));
        HttpUtils httpUtils = new HttpUtils();
        httpUtils.configCurrentHttpCacheExpiry(0);
        ResponseStream responseStream = null;
        try {
            responseStream = httpUtils.sendSync(HttpRequest.HttpMethod.POST,
                    MHttpUtils.DATE_QUERY_MEDIC, requestParams);
            String entity = responseStream.readString();
            JsonElement json = new JsonParser().parse(entity);
            JsonElement jsonElement = null;
            if (json.isJsonObject()) {
                JsonObject jsonObject = json.getAsJsonObject();
                jsonElement = jsonObject.get("response");
            }
            if (jsonElement != null && jsonElement.isJsonArray()) {
                //保存数据库
                BaseFragment.jsonArray = jsonElement.getAsJsonArray();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public View setView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_flb, container, false);
        ButterKnife.bind(this, view);
        if (isAdded() && !EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().registerSticky(this);
            initListener();
        }
        return view;
    }

    /**
     * 保存数据到数据表 SetMedicTime
     *
     * @param jsonArray 1-28个格子
     */
    private void saveToDatabase(JsonArray jsonArray) {
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
    protected LoadingPager.LoadResult readDatabase() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.SIMPLIFIED_CHINESE);
        mTvDateFlb.setText(sdf.format(new Date()));

        String week = weekTurnEnglish();
        mTvWeekFlb.setText(week);
        mTvNameFlb.setText(MyApplication.DEVICE_USER_NAME);

        //查询数据库
        QueryBuilder<Box> builder = DaoHolder.getBoxDao(BoxDao.Properties.Day_in_week.eq(getWeekStringToInt()));
        long count = builder.buildCount().count();
        if (count > 0) {
            final List<Box> boxList = builder.build().forCurrentThread().list();
            try {
                MyApplication.getDaoSession().runInTx(new Runnable() {
                    @Override
                    public void run() {
                        for (Box box : boxList) {
                            preAddToList(box);
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        AddFlbMAdapter adapter = new AddFlbMAdapter(getActivity(), listMon);
        mLvMonFlb.setAdapter(adapter);
        AddFlbMAdapter adapterNON = new AddFlbMAdapter(getActivity(), listNON);
        mLvNonFlb.setAdapter(adapterNON);
        AddFlbMAdapter adapterAFTER = new AddFlbMAdapter(getActivity(), listAFTERNON);
        mLvAfternFlb.setAdapter(adapterAFTER);
        AddFlbMAdapter adapterEVEN = new AddFlbMAdapter(getActivity(), listEVEN);
        mLvEvenFlb.setAdapter(adapterEVEN);

        //手动对应推送提示标记
        isPushPromptTag();
        //获取服务器数据后，标记是否服药
        isTakeMedicService();

        //开启倒计时
        timeThread = new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        if (timeCount % 300 == 0)
                            determineWhetherOnline();
                        setCountdown();
                        timeCount++;
                        sleep(1000);
                    }
                } catch (InterruptedException | ParseException e) {
                    e.printStackTrace();
                    clearData();
                }
            }
        };
        timeThread.start();
        return LoadingPager.LoadResult.READ;
    }

    private void clearData() {
        eatTimeMon = null;
        eatTimeNon = null;
        eatTimeAfter = null;
        eatTimeEven = null;

        timeCount=0;

        msMon = 0;
        msNon = 0;
        msAFTER = 0;
        msEVEN = 0;

        if (!listMon.isEmpty()) listMon.clear();
        if (!listNON.isEmpty()) listNON.clear();
        if (!listAFTERNON.isEmpty()) listAFTERNON.clear();
        if (!listEVEN.isEmpty()) listEVEN.clear();
    }

    private void preAddToList(Box box) {
        QueryBuilder<SetMedicTime> builder1 = DaoHolder.getSetMedicTimeDao(
                SetMedicTimeDao.Properties.Number.eq(box.getNumber()),
                SetMedicTimeDao.Properties.Device_uid.eq(MyApplication.flbId));
        if (builder1.buildCount().count() > 0) {
            int number = box.getNumber();
            if (number == 0 || number == 4 || number == 8 || number == 12 || number == 16 ||
                    number == 20 || number == 24) {
                addMedicineToList(builder1, listMon, 1);
            } else if (number == 1 || number == 5 || number == 9 || number == 13 || number ==
                    17 || number == 21 || number == 25) {
                addMedicineToList(builder1, listNON, 2);
            } else if (number == 2 || number == 6 || number == 10 || number == 14 || number
                    == 18 || number == 22 || number == 26) {
                addMedicineToList(builder1, listAFTERNON, 3);
            } else if (number == 3 || number == 7 || number == 11 || number == 15 || number
                    == 19 || number == 23 || number == 27) {
                addMedicineToList(builder1, listEVEN, 4);
            }
        }
    }

    /**
     * 向显示list中加药物
     *
     * @param builder
     * @param listMon
     * @param i
     */
    private void addMedicineToList(QueryBuilder<SetMedicTime> builder, ArrayList<String> listMon, int i) {
        List<SetMedicTime> list = builder.build().forCurrentThread().list();
        for (SetMedicTime smt : list) {
            listMon.add(smt.getMedicine());
        }
        if (list.get(0).getEat_time() != null) {
            switch (i) {
                case 1:
                    eatTimeMon = list.get(0).getEat_time();
                    break;
                case 2:
                    eatTimeNon = list.get(0).getEat_time();
                    break;
                case 3:
                    eatTimeAfter = list.get(0).getEat_time();
                    break;
                case 4:
                    eatTimeEven = list.get(0).getEat_time();
                    break;
                default:
                    break;
            }
        }
    }

    private void isPushPromptTag() {
        SimpleDateFormat sdfor = new SimpleDateFormat("yyyy-MM-dd", Locale.SIMPLIFIED_CHINESE);
        QueryBuilder<IsNotif> builder = DaoHolder.getIsNotifDao(
                IsNotifDao.Properties.Today.eq(sdfor.format(new Date())),
                IsNotifDao.Properties.Medic_id.eq(MyApplication.flbId));
        IsNotif isNotif = null;
        if (builder.buildCount().count() == 1) {
            isNotif = builder.build().forCurrentThread().unique();
        }
        if (isNotif != null) {
            switch (isNotif.getIsM()) {//0时为灰色
                case 1://为未服药
                    mImgMonFlb.setImageResource(R.drawable.flb_morning_red);
                    mTvAm.setTextColor(getResources().getColor(R.color.viewfinder_laser));
                    break;
                case 2://已经服药
                    mImgMonFlb.setImageResource(R.drawable.flb_morning_green);
                    mTvAm.setTextColor(getResources().getColor(R.color.green));
                    break;
                default:
                    break;
            }
            switch (isNotif.getIsN()) {
                case 1:
                    mImgNonFlb.setImageResource(R.drawable.flb_nooning_red);
                    mTvNoon.setTextColor(getResources().getColor(R.color.viewfinder_laser));
                    break;
                case 2:
                    mImgNonFlb.setImageResource(R.drawable.flb_nooning_green);
                    mTvNoon.setTextColor(getResources().getColor(R.color.green));
                    break;
                default:
                    break;
            }
            switch (isNotif.getIsA()) {
                case 1:
                    mImgAfternFlb.setImageResource(R.drawable.flb_after_red);
                    mTvPm.setTextColor(getResources().getColor(R.color.viewfinder_laser));
                    break;
                case 2:
                    mImgAfternFlb.setImageResource(R.drawable.flb_after_green);
                    mTvPm.setTextColor(getResources().getColor(R.color.green));
                    break;
                default:
                    break;
            }
            switch (isNotif.getIsE()) {
                case 1:
                    mImgEvenFlb.setImageResource(R.drawable.flb_evening_red);
                    mTvBed.setTextColor(getResources().getColor(R.color.viewfinder_laser));
                    break;
                case 2:
                    mImgEvenFlb.setImageResource(R.drawable.flb_evening_green);
                    mTvBed.setTextColor(getResources().getColor(R.color.green));
                    break;
                default:
                    break;
            }
        }
    }

    private void isTakeMedicService() {
        if (BaseFragment.jsonArray != null && BaseFragment.jsonArray.size() > 0) {
            try {
                for (JsonElement element : BaseFragment.jsonArray) {
                    DateQueryMedic dateQueryMedic = new Gson().fromJson(element, DateQueryMedic.class);
                    int number = dateQueryMedic.getNumber();
                    setTakenMedicine(dateQueryMedic, number);
                }
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
            }
        }
    }

    private void setTakenMedicine(DateQueryMedic dateQueryMedic, int number) {
        if (number == 0 || number == 4 || number == 8 || number == 12 || number == 16
                || number == 20 || number == 24) {
            if (dateQueryMedic.isTaken()) {
                mImgMonFlb.setImageResource(R.drawable.flb_morning_green);
                mTvAm.setTextColor(getResources().getColor(R.color.green));
            } else {
                mImgMonFlb.setImageResource(R.drawable.flb_morning_red);
                mTvAm.setTextColor(getResources().getColor(R.color.viewfinder_laser));
            }
        } else if (number == 1 || number == 5 || number == 9 || number == 13 ||
                number == 17 || number == 21 || number == 25) {

            if (dateQueryMedic.isTaken()) {
                mImgNonFlb.setImageResource(R.drawable.flb_nooning_green);
                mTvNoon.setTextColor(getResources().getColor(R.color.green));
            } else {
                mImgNonFlb.setImageResource(R.drawable.flb_nooning_red);
                mTvNoon.setTextColor(getResources().getColor(R.color.viewfinder_laser));
            }
        } else if (number == 2 || number == 6 || number == 10 || number == 14 ||
                number == 18 || number == 22 || number == 26) {
            if (dateQueryMedic.isTaken()) {
                mImgAfternFlb.setImageResource(R.drawable.flb_after_green);
                mTvPm.setTextColor(getResources().getColor(R.color.green));
            } else {
                mImgAfternFlb.setImageResource(R.drawable.flb_after_red);
                mTvPm.setTextColor(getResources().getColor(R.color.viewfinder_laser));
            }
        } else if (number == 3 || number == 7 || number == 11 || number == 15 ||
                number == 19 || number == 23 || number == 27) {
            if (dateQueryMedic.isTaken()) {
                mImgEvenFlb.setImageResource(R.drawable.flb_evening_green);
                mTvBed.setTextColor(getResources().getColor(R.color.green));
            } else {
                mImgEvenFlb.setImageResource(R.drawable.flb_evening_red);
                mTvBed.setTextColor(getResources().getColor(R.color.viewfinder_laser));
            }
        }
    }

    public void initListener() {
        mLvMonFlb.setOnItemClickListener(this);
        mLvNonFlb.setOnItemClickListener(this);
        mLvAfternFlb.setOnItemClickListener(this);
        mLvEvenFlb.setOnItemClickListener(this);

        mScrollViewFlb.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mScrollViewFlb != null) {
                    mScrollViewFlb.setRefreshing(true);
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (timeThread != null) timeThread.interrupt();
                            show();
                            mScrollViewFlb.setRefreshing(false);
                            //3秒后停止刷新
                        }
                    }, 3000);
                }
            }
        });

        mLrUnLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MyApplication.flbId.startsWith("1")) {
                    Intent intent = new Intent(getActivity(), GSMActivityByMain.class);
                    startActivity(intent);
                } else if (MyApplication.flbId.startsWith("2")) {
                    Intent intent = new Intent(getActivity(), WifiChoiceActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("from", "main");
                    intent.putExtras(bundle);
                    startActivity(intent);
                }

            }
        });
    }

    private String weekTurnEnglish() {
        String week = null;
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        int k = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        switch (k) {
            case 1:
                week = getString(R.string.MON);
                break;
            case 2:
                week = getString(R.string.TUE);
                break;
            case 3:
                week = getString(R.string.WED);
                break;
            case 4:
                week = getString(R.string.THU);
                break;
            case 5:
                week = getString(R.string.FRI);
                break;
            case 6:
                week = getString(R.string.SAT);
                break;
            case 0:
                week = getString(R.string.SUN);
                break;
        }
        return week;
    }

    private void determineWhetherOnline() {
        mHandler.sendEmptyMessage(300);
    }

    private String getWeekStringToInt() {
        String str = mTvWeekFlb.getText().toString().trim();
        String strWeek = null;
        if (getString(R.string.MON).equals(str)) {
            strWeek = "1";
        } else if (getString(R.string.TUE).equals(str)) {
            strWeek = "2";
        } else if (getString(R.string.WED).equals(str)) {
            strWeek = "3";
        } else if (getString(R.string.THU).equals(str)) {
            strWeek = "4";
        } else if (getString(R.string.FRI).equals(str)) {
            strWeek = "5";
        } else if (getString(R.string.SAT).equals(str)) {
            strWeek = "6";
        } else if (getString(R.string.SUN).equals(str)) {
            strWeek = "7";
        }
        return strWeek;
    }

    /**
     * 启动倒计时
     */
    private void setCountdown() throws ParseException {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HHmmss", Locale.SIMPLIFIED_CHINESE);
        String[] string = sdf.format(new Date()).split("-");

        SimpleDateFormat sdformat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.SIMPLIFIED_CHINESE);
        long time = sdformat.parse(sdformat.format(new Date())).getTime();//当前时间

        if (eatTimeMon != null) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(string[0]).append(eatTimeMon.split(":")[0]).append(eatTimeMon
                    .split(":")[1]).append(00);
            msMon = sdformat.parse(stringBuilder.toString()).getTime();//毫秒
        }
        if (eatTimeNon != null) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(string[0]).append(eatTimeNon.split(":")[0]).append(eatTimeNon
                    .split(":")[1]).append(00);
            msNon = sdformat.parse(stringBuilder.toString()).getTime();//毫秒
        }
        if (eatTimeAfter != null) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(string[0]).append(eatTimeAfter.split(":")[0]).append
                    (eatTimeAfter.split(":")[1]).append(00);
            msAFTER = sdformat.parse(stringBuilder.toString()).getTime();//毫秒
        }
        if (eatTimeEven != null) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(string[0]).append(eatTimeEven.split(":")[0]).append(eatTimeEven
                    .split(":")[1]).append(00);
            msEVEN = sdformat.parse(stringBuilder.toString()).getTime();//毫秒
        }
        //判断要显示的时间段
        if (time <= msMon) {
            showTimeToTV(msMon, time);
        } else if (time <= msNon) {
            showTimeToTV(msNon, time);
        } else if (time <= msAFTER) {
            showTimeToTV(msAFTER, time);
        } else if (time <= msEVEN) {
            showTimeToTV(msEVEN, time);
        } else {
            Message message = mHandler.obtainMessage(200);
            mHandler.sendMessage(message);
        }
    }

    /**
     * 计算倒计时
     *
     * @param time
     * @param mTime 当前时间
     */
    private void showTimeToTV(final long time, final long mTime) {
        cMSeconds = time - mTime;

        long day = ((cMSeconds / 1000) / (3600 * 24));
        long hour = ((cMSeconds / 1000) - day * 86400) / 3600;
        long minutes = ((cMSeconds / 1000) - day * 86400 - hour * 3600) / 60;
        long seconds = (cMSeconds / 1000) - day * 86400 - hour * 3600 - minutes * 60;
        String sHour = changLongToString(hour);
        String sMinutes = changLongToString(minutes);
        String sSeconds = changLongToString(seconds);

        Message message = mHandler.obtainMessage(100);
        Bundle bundle = new Bundle();
        bundle.putString("sHour", sHour);
        bundle.putString("sMinutes", sMinutes);
        bundle.putString("sSeconds", sSeconds);
        message.setData(bundle);
        mHandler.sendMessage(message);
    }

    private void setIsOnline() {
        if (mImageOnline != null && mTvOnline != null) {
           /* if (MainActivity.isOnline) {
                mImageOnline.setImageResource(R.drawable.flb_online);
                mTvOnline.setText(getString(R.string.box_online));
            } else {
                mImageOnline.setImageResource(R.drawable.flb_unonline);
                mTvOnline.setText(getString(R.string.box_offline));
            }*/
            String accessToken = SharedPreUtil.getString("access_token");
            RequestParams requestParams = new RequestParams();
            requestParams.addHeader("Authorization", "Bearer " + accessToken);
            HttpUtils httpUtils = new HttpUtils();
            httpUtils.configCurrentHttpCacheExpiry(0);
            httpUtils.send(HttpRequest.HttpMethod.GET, MHttpUtils.IS_ONLINE + MyApplication.flbId, requestParams,
                    new RequestCallBack<String>() {
                        @Override
                        public void onSuccess(ResponseInfo<String> responseInfo) {
                            KLog.json(responseInfo.result);
                            try {
                                JsonElement jsonElement = new JsonParser().parse(responseInfo.result);
                                JsonElement lElement = null;
                                if (jsonElement.isJsonObject()) {
                                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                                    JsonElement element = jsonObject.get("response");
                                    if (element.isJsonObject()) {
                                        JsonObject object = element.getAsJsonObject();
                                        lElement = object.get("last_visit");
                                    }
                                }
                                if (!(lElement != null && lElement.isJsonNull())) {
                                    String[] string = lElement != null ? lElement.getAsString().split("T") : new String[0];
                                    StringBuilder sb = new StringBuilder();
                                    sb.append(string[0]);
                                    String[] str = string[1].split(":");
                                    sb.append(str[0]).append(":").append(str[1]);
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-ddHH:mm", Locale.SIMPLIFIED_CHINESE);
                                    long nTime = sdf.parse(sdf.format(new Date())).getTime();
                                    long lastTime = sdf.parse(sb.toString()).getTime();

                                    if (nTime - lastTime <= 1000 * 60 * 10) {//在线
                                        mImageOnline.setImageResource(R.drawable.flb_online);
                                        mTvOnline.setText(getString(R.string.box_online));
                                    } else {
                                        mImageOnline.setImageResource(R.drawable.flb_unonline);
                                        mTvOnline.setText(getString(R.string.box_offline));
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(HttpException e, String s) {
                            KLog.d(s);
                        }
                    });
        }
    }

    /**
     * 8:2--08:02
     *
     * @param hour
     * @return
     */
    private String changLongToString(long hour) {
        String sHour;
        if (hour < 10) {
            sHour = "0" + String.valueOf(hour);
        } else {
            sHour = String.valueOf(hour);
        }
        return sHour;
    }

    @Override
    public void onDestroyView() {
        if (timeThread != null) timeThread.interrupt();

        ButterKnife.unbind(this);
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }

    private int[] weekToDatabaseId(String week) {
        int[] days = new int[4];
        if (week.equals(getString(R.string.MON))) {
            days = new int[]{0, 1, 2, 3};
        } else if (week.equals(getString(R.string.TUE))) {
            days = new int[]{4, 5, 6, 7};
        } else if (week.equals(getString(R.string.WED))) {
            days = new int[]{8, 9, 10, 11};
        } else if (week.equals(getString(R.string.THU))) {
            days = new int[]{12, 13, 14, 15};
        } else if (week.equals(getString(R.string.FRI))) {
            days = new int[]{16, 17, 18, 19};
        } else if (week.equals(getString(R.string.SAT))) {
            days = new int[]{20, 21, 22, 23};
        } else if (week.equals(getString(R.string.SUN))) {
            days = new int[]{24, 25, 26, 27};
        }
        return days;
    }

    public void onEventMainThread(final MedicineInfoEvent event) {
        if (event.isTrue()) {
            if (timeThread != null) timeThread.interrupt();
            show();
        }
    }

    public void onEventMainThread(PerodOfDayEvent event) {
        if (timeThread != null) timeThread.interrupt();
        show();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.lv_mon_flb:
                if (listMon.size() > 0) {
                    setEatDrugTime(0);
                }
                break;
            case R.id.lv_non_flb:
                if (listNON.size() > 0) {
                    setEatDrugTime(1);
                }
                break;
            case R.id.lv_aftern_flb:
                if (listAFTERNON.size() > 0) {
                    setEatDrugTime(2);
                }
                break;
            case R.id.lv_even_flb:
                if (listEVEN.size() > 0) {
                    setEatDrugTime(3);
                }
                break;
            default:
                break;
        }
    }

    private void setEatDrugTime(int i) {
        int[] days = weekToDatabaseId(mTvWeekFlb.getText().toString());
        Intent intent = new Intent(getActivity(), SetRepeatDrug.class);
        Bundle bundle = new Bundle();
        bundle.putString("number", String.valueOf(days[i]));//当天对应的四个number中的一个
        bundle.putInt("inDay", i);//当天中某时段
        bundle.putString("week", mTvWeekFlb.getText().toString());//星期~
        intent.putExtra("bundle", bundle);
        startActivity(intent);
    }
}
