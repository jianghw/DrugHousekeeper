package com.cjy.flb.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.brithpicker.TimePopupWindow;
import com.cjy.flb.R;
import com.cjy.flb.adapter.SetRepeatDrugAdapter;
import com.cjy.flb.bean.PullDownMedicine;
import com.cjy.flb.customView.SetMedicPopWindow;
import com.cjy.flb.dao.BoxDao;
import com.cjy.flb.dao.DaoHolder;
import com.cjy.flb.dao.RepeatWeekDao;
import com.cjy.flb.dao.SetMedicTimeDao;
import com.cjy.flb.db.Box;
import com.cjy.flb.db.RepeatWeek;
import com.cjy.flb.db.SetMedicTime;
import com.cjy.flb.event.MedicineInfoEvent;
import com.cjy.flb.manager.AppManager;
import com.cjy.flb.utils.MHttpUtils;
import com.cjy.flb.utils.SharedPreUtil;
import com.cjy.flb.utils.ToastUtil;
import com.daimajia.swipe.SwipeLayout;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.socks.library.KLog;

import org.apache.http.entity.StringEntity;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2016/3/2 0002.
 * 设置药物，重复，时间
 */
public class SetRepeatDrug extends BaseActivity implements
        View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    @Bind(R.id.ll_top_back)
    LinearLayout mLlTopBack;
    @Bind(R.id.tv_top_more)
    TextView mTvTopMore;

    @Bind(R.id.image_time)
    ImageView mImageTime;
    @Bind(R.id.tv_time)
    TextView mTvTime;

    @Bind(R.id.rl_set_week)
    RelativeLayout mRlSetWeek;
    @Bind(R.id.tv_repeat)
    TextView mTvRepeat;
    @Bind(R.id.tv_week)
    TextView mTvWeek;
    @Bind(R.id.rl_repeat_week)
    RelativeLayout mRlRepeatWeek;
    @Bind(R.id.listView_medic)
    ListView mListViewMedic;

    //药物集合
    public static ArrayList<String> mList = new ArrayList<>();
    private SetRepeatDrugAdapter adapter;
    //时间段选择记录
    public static HashMap<String, Boolean> checkMap = new HashMap<>();

    //时间选择器相关
    private TimePopupWindow pwTime;
    private ArrayList<String> time1Items = new ArrayList<>();
    private ArrayList<ArrayList<String>> time2Items = new ArrayList<>();
    //当前星期标记
    private String cWeek;
    private String pointInDay;
    //标记每天-星期的重复
    private boolean[] cWeeks;
    //当前number
    public static String transferNumber;
    //标记是否弹提示
    private boolean isShow;

    @Override
    public void initView() {
        setContentView(R.layout.activity_set_medicine);
        ButterKnife.bind(this);

        initBar(getString(R.string.medicine), false, true);
        tvMore.setVisibility(View.VISIBLE);
        tvMore.setText(getString(R.string.add));
    }

    @Override
    public void initData() {
        Bundle bundle = getIntent().getBundleExtra("bundle");
        if (bundle != null) {
            transferNumber = bundle.getString("number", "");
            int inDay = bundle.getInt("inDay");
            switch (inDay) {
                case 0:
                    pointInDay = "MORN";
                    break;
                case 1:
                    pointInDay = "NOON";
                    break;
                case 2:
                    pointInDay = "AFTERNOON";
                    break;
                case 3:
                    pointInDay = "NIGHT";
                    break;
                default:
                    break;
            }
            String week = bundle.getString("week");
            if (week != null && week.equals(getString(R.string.today))) {
                SimpleDateFormat sdf = new SimpleDateFormat("E");
                cWeek = sdf.format(new Date());
            } else {
                cWeek = week;
            }
            initTimeOfData(inDay);
        }
        //查询数据库对应的药物
        QueryBuilder<SetMedicTime> builder = DaoHolder.getSetMedicTimeDao(
                SetMedicTimeDao.Properties.Number.eq(transferNumber),
                SetMedicTimeDao.Properties.Device_uid.eq(MyApplication.flbId));
        if (builder.buildCount().count() > 0) {
            List<SetMedicTime> list = builder.build().forCurrentThread().list();
            String time = list.get(0).getEat_time();
            if (!TextUtils.isEmpty(time)) {//设置时间
                mTvTime.setText(time);
                mTvTime.setTextColor(getResources().getColor(R.color.btn_textColor));
                mImageTime.setImageResource(R.drawable.set_time_yes);
            } else {
                mTvTime.setText(getString(R.string.reminder_time));
                mTvTime.setTextColor(getResources().getColor(R.color.viewfinder_laser));
                mImageTime.setImageResource(R.drawable.set_time_no);
            }
            for (SetMedicTime smt : list) {
                String medic = smt.getMedicine();
                Integer quantity = smt.getQuantity();
                Integer medic_id = smt.getMedicine_id();
                SetRepeatDrug.mList.add(medic + "/" + quantity + "/" + medic_id);
            }
        }
        //listview中显示数据药物
        adapter = new SetRepeatDrugAdapter(this, handler);
        mListViewMedic.setAdapter(adapter);
        //时间选择器
        pwTime = new TimePopupWindow(context);
        //初始化显示有选择过的星期
        chooseWeeksDisplay(transferNumber);
        isShow = true;
    }

    private void chooseWeeksDisplay(String number) {
        cWeeks = new boolean[8];
        for (int i = 0; i < cWeeks.length; i++) {
            cWeeks[i] = false;
        }
        StringBuilder sb = new StringBuilder();
        QueryBuilder<RepeatWeek> builder = MyApplication.getDaoSession().getRepeatWeekDao()
                .queryBuilder()
                .where(RepeatWeekDao.Properties.Medic_id.eq(MyApplication.flbId),
                        RepeatWeekDao.Properties.Number.eq(number));
        if (builder.buildCount().count() == 1) {
            RepeatWeek repeatWeek = builder.build().unique();
            if (repeatWeek.getIsEvery()) {//当有选择每天时
                String weeks = sb.append(getString(R.string.EVE)).append(" ").toString();
                for (int i = 0; i < cWeeks.length; i++) {
                    cWeeks[i] = true;
                }
                mTvWeek.setText(weeks);
            } else {
                if (repeatWeek.getIsMorn() || getString(R.string.MON).equals(cWeek) || getString(R.string.MON_X)
                        .equals(cWeek)) {
                    sb.append(getString(R.string.MON)).append(" ");
                    cWeeks[1] = true;
                }
                if (repeatWeek.getIsTue() || getString(R.string.TUE).equals(cWeek) || getString(R.string.TUE_X)
                        .equals(cWeek)) {
                    sb.append(getString(R.string.TUE)).append(" ");
                    cWeeks[2] = true;
                }
                if (repeatWeek.getIsWed() || getString(R.string.WED).equals(cWeek) || getString(R.string.WED_X)
                        .equals(cWeek)) {
                    sb.append(getString(R.string.WED)).append(" ");
                    cWeeks[3] = true;
                }
                if (repeatWeek.getIsThu() || getString(R.string.THU).equals(cWeek) || getString(R.string.THU_X)
                        .equals(cWeek)) {
                    sb.append(getString(R.string.THU)).append(" ");
                    cWeeks[4] = true;
                }
                if (repeatWeek.getIsFri() || getString(R.string.FRI).equals(cWeek) || getString(R.string.FRI_X)
                        .equals(cWeek)) {
                    sb.append(getString(R.string.FRI)).append(" ");
                    cWeeks[5] = true;
                }
                if (repeatWeek.getIsSat() || getString(R.string.SAT).equals(cWeek) || getString(R.string.SAT_X)
                        .equals(cWeek)) {
                    sb.append(getString(R.string.SAT)).append(" ");
                    cWeeks[6] = true;
                }
                if (repeatWeek.getIsSun() || getString(R.string.SUN).equals(cWeek) || getString(R.string.SUN_X)
                        .equals(cWeek)) {
                    sb.append(getString(R.string.SUN)).append(" ");
                    cWeeks[7] = true;
                }
                String weeks = sb.toString();
                mTvWeek.setText(weeks);
            }
        } else {
            if (getString(R.string.MON).equals(cWeek) || getString(R.string.MON_X).equals(cWeek)) {
                sb.append(getString(R.string.MON)).append(" ");
                cWeeks[1] = true;
            } else if (getString(R.string.TUE).equals(cWeek) || getString(R.string.TUE_X).equals(cWeek)) {
                sb.append(getString(R.string.TUE)).append(" ");
                cWeeks[2] = true;
            } else if (getString(R.string.WED).equals(cWeek) || getString(R.string.WED_X).equals(cWeek)) {
                sb.append(getString(R.string.WED)).append(" ");
                cWeeks[3] = true;
            } else if (getString(R.string.THU).equals(cWeek) || getString(R.string.THU_X).equals(cWeek)) {
                sb.append(getString(R.string.THU)).append(" ");
                cWeeks[4] = true;
            } else if (getString(R.string.FRI).equals(cWeek) || getString(R.string.FRI_X).equals(cWeek)) {
                sb.append(getString(R.string.FRI)).append(" ");
                cWeeks[5] = true;
            } else if (getString(R.string.SAT).equals(cWeek) || getString(R.string.SAT_X).equals(cWeek)) {
                sb.append(getString(R.string.SAT)).append(" ");
                cWeeks[6] = true;
            } else if (getString(R.string.SUN).equals(cWeek) || getString(R.string.SUN_X).equals(cWeek)) {
                sb.append(getString(R.string.SUN)).append(" ");
                cWeeks[7] = true;
            }
            String weeks = sb.toString();
            mTvWeek.setText(weeks);
        }
    }

    /**
     * 初始化时间选择器的数据
     *
     * @param inDay 一天当中的四个档位
     */
    private void initTimeOfData(int inDay) {
        //分钟
        ArrayList<String> items_01 = new ArrayList<String>();
        ArrayList<String> items_02 = new ArrayList<String>();
        for (int i = 0; i < 60; i++) {
            if (i < 10) {
                items_01.add("0" + String.valueOf(i));
                items_02.add("0" + String.valueOf(i));
            } else {
                items_01.add(String.valueOf(i));
                items_02.add(String.valueOf(i));
            }
        }
        time2Items.add(items_01);
        time2Items.add(items_02);
        //小时
        switch (inDay) {
            case 0://上午
                for (int i = 0; i < 11; i++) {
                    if (i < 10) {
                        time1Items.add("0" + String.valueOf(i));
                    } else {
                        time1Items.add(String.valueOf(i));
                    }
                }
                break;
            case 1://中午
                for (int i = 11; i < 14; i++) {
                    if (i < 10) {
                        time1Items.add("0" + String.valueOf(i));
                    } else {
                        time1Items.add(String.valueOf(i));
                    }
                }
                break;
            case 2://下午
                for (int i = 14; i < 19; i++) {
                    if (i < 10) {
                        time1Items.add("0" + String.valueOf(i));
                    } else {
                        time1Items.add(String.valueOf(i));
                    }
                }
                break;
            case 3://睡前
                for (int i = 19; i < 24; i++) {
                    if (i < 10) {
                        time1Items.add("0" + String.valueOf(i));
                    } else {
                        time1Items.add(String.valueOf(i));
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void initListener() {
        mRlSetWeek.setOnClickListener(this);
        mRlRepeatWeek.setOnClickListener(this);
        //返回键
        llTopBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBackDown();
            }
        });

        //添加弹出框
        tvMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSweetDialog();
            }
        });

        //item 长按删除
        mListViewMedic.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, final View view, final int
                    position, long id) {
                View contentView = LayoutInflater.from(context).inflate(R.layout.popupwin_listview_item, null);
                TextView textView = (TextView) contentView.findViewById(R.id.textView);

                final PopupWindow popupWindow = new PopupWindow(contentView, LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT, true);
                popupWindow.setTouchable(true);
                popupWindow.setTouchInterceptor(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return false;
                    }
                });
                popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.pupowin_list_top));

                int[] location = new int[2];
                view.getLocationOnScreen(location);
                //[0]--left,[1]--top
                Rect anchorRect = new Rect(location[0], location[1],
                        location[0] + view.getWidth(), location[1] + view.getHeight());
                popupWindow.showAtLocation(view, Gravity.NO_GRAVITY,
                        (anchorRect.right - anchorRect.left) / 2, anchorRect.top - view.getHeight());

                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (popupWindow.isShowing())
                            popupWindow.dismiss();
                        adapter.deleteMedicineItem(position,
                                (SwipeLayout) view.findViewById(adapter.getSwipeLayoutResourceId(position)));
                    }
                });
                //刷新状态
                popupWindow.update();
                return true;
            }
        });
    }

    /**
     * 添加药物窗口
     */
    private void showSweetDialog() {
        if (mTvTime.getText().toString().equals(getString(R.string.reminder_time))) {
            ToastUtil.showShort(getString(R.string.set_medication_time));
        } else {
            if (isShow) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(getString(R.string.reminder))
                        .setMessage(getString(R.string.set_medic_dialog))
                        .setPositiveButton(getString(R.string.i_know_it), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                builder.create().dismiss();
                                new SetMedicPopWindow(context, pointInDay, cWeeks, mTvTime.getText().toString(),
                                        handler, cWeek).showAtBottom(mListViewMedic);
                                isShow = false;
                            }
                        }).show();
            } else {
                new SetMedicPopWindow(context, pointInDay, cWeeks, mTvTime.getText().toString(),
                        handler, cWeek).showAtBottom(mListViewMedic);
            }
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 100:
                    adapter.notifyDataSetChanged();
                    adapter.closeAllItems();
                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        if (!mList.isEmpty()) {
            SetRepeatDrug.mList.clear();
        }
        if (!time1Items.isEmpty()) {
            time1Items.clear();
        }
        if (!time2Items.isEmpty()) {
            time2Items.clear();
        }
        transferNumber = null;
        if (!checkMap.isEmpty()) {
            checkMap.clear();
        }
        ButterKnife.unbind(this);
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_HOME:
                return true;
            case KeyEvent.KEYCODE_BACK:
                setBackDown();
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void setBackDown() {
        if (mTvTime.getText().toString().equals(getString(R.string.reminder_time)) && mList.size() > 0) {
            ToastUtil.showShort(getString(R.string.set_medication_time));
        } else if (!mTvTime.getText().toString().equals(getString(R.string.reminder_time)) &&
                mList.size() == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(getString(R.string.important_notification))
                    .setMessage(getString(R.string.time_setting_failed))
                    .setPositiveButton(getString(R.string.add), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            showSweetDialog();
                        }
                    })
                    .setNegativeButton(getString(R.string.give_up), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            EventBus.getDefault().postSticky(new MedicineInfoEvent(true));
                            AppManager.getAppManager().finishActivity(SetRepeatDrug.class);
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            EventBus.getDefault().postSticky(new MedicineInfoEvent(true));
            AppManager.getAppManager().finishActivity(SetRepeatDrug.class);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_set_week:
                pwTime.showAtLocation(mRlRepeatWeek, Gravity.BOTTOM, 0, 0);
                chooseTimeData();
                break;
            case R.id.rl_repeat_week://周期重复框
                if (mTvTime.getText().toString().equals(getString(R.string.reminder_time))) {
                    ToastUtil.showShort(getString(R.string.set_medication_time));
                } else {
                    showChoiceWeek();
                }
                break;
        }
    }

    private void chooseTimeData() {
        //初始化pwBrith的信息,false关闭联动
        pwTime.setPicker(time1Items, time2Items, false);
        //开启循环
        pwTime.setCyclic(false);
        //设置初始位置
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        pwTime.setSelectOptions(hour, minute);
        pwTime.setOnoptionsSelectListener(new TimePopupWindow.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2) {
                String time = time1Items.get(options1) + ":" + time2Items.get(0).get(option2);
                if (SetRepeatDrug.mList.size() > 0) {//有数据时更新时间
                    if (mTvTime.getText().toString().equals(getString(R.string.reminder_time))) {//提醒时间时，提交数据
                        mTvTime.setText(time);
                        List<Integer> list = new ArrayList<>();
                        list.add(Integer.parseInt(transferNumber));
                        uploadDrugsEveryDay(list, null);
                    } else {//更新时间
                        List<Integer> numbers = getIntegers();
                        for (int number : numbers) {
                            updateSetTime(time, number);
                        }
                    }
                } else {
                    mTvTime.setText(time);
                }
                mTvTime.setTextColor(getResources().getColor(R.color.btn_textColor));
                mImageTime.setImageResource(R.drawable.set_time_yes);
            }
        });
    }

    /**
     * 更新设置时间
     *
     * @param time   08:00
     * @param number
     */
    private void updateSetTime(final String time, final int number) {
        RequestParams requestParams = new RequestParams();
        String accessToken = SharedPreUtil.getString("access_token");
        requestParams.addHeader("Authorization", "Bearer " + accessToken);
        requestParams.addBodyParameter("device_uid", MyApplication.flbId);
        requestParams.addBodyParameter("number", String.valueOf(number));
        requestParams.addBodyParameter("eat_time", time);
        HttpUtils httpUtils = new HttpUtils();
        httpUtils.send(HttpRequest.HttpMethod.POST, MHttpUtils.UPDATE_TIME, requestParams,
                new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        KLog.json(responseInfo.result);
                        JsonElement code = null;
                        try {
                            JsonElement jsonElement = new JsonParser().parse(responseInfo.result);
                            JsonObject jsonObject = jsonElement.getAsJsonObject();
                            JsonElement response = jsonObject.get("response");
                            JsonObject jObject = response.getAsJsonObject();
                            code = jObject.get("code");
                        } catch (JsonSyntaxException e) {
                            e.printStackTrace();
                        }
                        if (code != null &&code.getAsInt()== 1) {
                            //更新数据库时间数据
                            List<SetMedicTime> setMedicTimes = MyApplication.getDaoSession().getSetMedicTimeDao()
                                    .queryBuilder().where(SetMedicTimeDao.Properties.Number.eq(number),
                                            SetMedicTimeDao.Properties.Device_uid.eq(MyApplication.flbId)).build()
                                    .forCurrentThread().list();
                            for (SetMedicTime smt : setMedicTimes) {
                                smt.setEat_time(time);
                            }
                            MyApplication.getDaoSession().getSetMedicTimeDao().updateInTx(setMedicTimes);
                            if (String.valueOf(number).equals(transferNumber)) {
                                mTvTime.setText(time);
                            }
                        } else if (code != null &&code.getAsInt()== 0) {
                            ToastUtil.showShort(getString(R.string.submit_error));
                        }
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        ToastUtil.showShort(getString(R.string.submit_error));
                    }
                });
    }


    /**
     * 星期复选框
     */
    private void showChoiceWeek() {
        View view = View.inflate(context, R.layout.week_alert_dialog, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);
        final AlertDialog mDialog = builder.create();
        mDialog.show();
        mDialog.setCanceledOnTouchOutside(false);
        final CheckBox checkBox_1 = (CheckBox) view.findViewById(R.id.cb_1);
        final CheckBox checkBox_2 = (CheckBox) view.findViewById(R.id.cb_2);
        final CheckBox checkBox_3 = (CheckBox) view.findViewById(R.id.cb_3);
        final CheckBox checkBox_4 = (CheckBox) view.findViewById(R.id.cb_4);
        final CheckBox checkBox_5 = (CheckBox) view.findViewById(R.id.cb_5);
        final CheckBox checkBox_6 = (CheckBox) view.findViewById(R.id.cb_6);
        final CheckBox checkBox_7 = (CheckBox) view.findViewById(R.id.cb_7);
        CheckBox checkBox_e = (CheckBox) view.findViewById(R.id.cb_every);
        checkBox_1.setOnCheckedChangeListener(this);
        checkBox_2.setOnCheckedChangeListener(this);
        checkBox_3.setOnCheckedChangeListener(this);
        checkBox_4.setOnCheckedChangeListener(this);
        checkBox_5.setOnCheckedChangeListener(this);
        checkBox_6.setOnCheckedChangeListener(this);
        checkBox_7.setOnCheckedChangeListener(this);

        setCurrentDayEnable(checkBox_1, checkBox_2, checkBox_3, checkBox_4, checkBox_5,
                checkBox_6, checkBox_7);

        if (cWeeks[0]) {
            checkBox_e.setChecked(true);
            checkBox_1.setChecked(true);
            checkBox_2.setChecked(true);
            checkBox_3.setChecked(true);
            checkBox_4.setChecked(true);
            checkBox_5.setChecked(true);
            checkBox_6.setChecked(true);
            checkBox_7.setChecked(true);
        } else {
            if (cWeeks[1])
                checkBox_1.setChecked(true);
            if (cWeeks[2])
                checkBox_2.setChecked(true);
            if (cWeeks[3])
                checkBox_3.setChecked(true);
            if (cWeeks[4])
                checkBox_4.setChecked(true);
            if (cWeeks[5])
                checkBox_5.setChecked(true);
            if (cWeeks[6])
                checkBox_6.setChecked(true);
            if (cWeeks[7])
                checkBox_7.setChecked(true);
        }

        checkBox_e.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    checkBox_1.setChecked(true);
                    checkBox_2.setChecked(true);
                    checkBox_3.setChecked(true);
                    checkBox_4.setChecked(true);
                    checkBox_5.setChecked(true);
                    checkBox_6.setChecked(true);
                    checkBox_7.setChecked(true);
                    cWeeks[0] = true;
                } else {
                    checkBox_1.setChecked(false);
                    checkBox_2.setChecked(false);
                    checkBox_3.setChecked(false);
                    checkBox_4.setChecked(false);
                    checkBox_5.setChecked(false);
                    checkBox_6.setChecked(false);
                    checkBox_7.setChecked(false);
                    cWeeks[0] = false;
                    setCurrentDayEnable(checkBox_1, checkBox_2, checkBox_3,
                            checkBox_4, checkBox_5, checkBox_6, checkBox_7);
                }
            }
        });


        Button ok = (Button) view.findViewById(R.id.btn_ok);
        ok.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        checkBoxOperationRecord(mDialog);
                    }
                }
        );
        Button no = (Button) view.findViewById(R.id.btn_no);
        no.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDialog.dismiss();
                    }
                }
        );
    }

    /**
     * 对星期复选框选择记录
     *
     * @param mDialog
     */
    private void checkBoxOperationRecord(AlertDialog mDialog) {
        //当有设置时间时且有药物，则上传药物
        if (!mTvWeek.getText().toString().equals(getString(R.string.reminder_time)) &&
                SetRepeatDrug.mList.size() > 0) {
            List<Integer> numbers = getIntegers();
            //先删除对应number的数据
            deleteDateByNumbers(numbers);
        }
        mDialog.dismiss();
        RepeatWeek repeatWeek = MyApplication.getDaoSession().getRepeatWeekDao().queryBuilder()
                .where(RepeatWeekDao.Properties.Medic_id.eq(MyApplication.flbId),
                        RepeatWeekDao.Properties.Number.eq(transferNumber)).build().unique();
        if (cWeeks[1] && cWeeks[2] && cWeeks[3] && cWeeks[4] && cWeeks[5] && cWeeks[6] && cWeeks[7]) {
            cWeeks[0] = true;
        } else {
            cWeeks[0] = false;
        }
        if (repeatWeek == null) {
            RepeatWeek newRweek = new RepeatWeek(null, MyApplication.flbId, transferNumber,
                    cWeeks[0], cWeeks[1], cWeeks[2], cWeeks[3], cWeeks[4], cWeeks[5], cWeeks[6], cWeeks[7]);
            MyApplication.getDaoSession().getRepeatWeekDao().insertInTx(newRweek);
        } else {
            repeatWeek.setIsEvery(cWeeks[0]);
            repeatWeek.setIsMorn(cWeeks[1]);
            repeatWeek.setIsTue(cWeeks[2]);
            repeatWeek.setIsWed(cWeeks[3]);
            repeatWeek.setIsThu(cWeeks[4]);
            repeatWeek.setIsFri(cWeeks[5]);
            repeatWeek.setIsSat(cWeeks[6]);
            repeatWeek.setIsSun(cWeeks[7]);
            MyApplication.getDaoSession().getRepeatWeekDao().updateInTx(repeatWeek);
        }
        //重新显示星期
        chooseWeeksDisplay(transferNumber);
    }

    /**
     * 获取要修改数据的number
     *
     * @return
     */
    @NonNull
    private List<Integer> getIntegers() {
        List<Integer> numbers = new ArrayList<>();//要上传的number[0,4...]
        for (int i = 1; i < cWeeks.length; i++) {
            if (cWeeks[i]) {
                Box box = MyApplication.getDaoSession().getBoxDao().queryBuilder().where
                        (BoxDao.Properties.Point_in_time.eq(pointInDay), BoxDao.Properties
                                .Day_in_week.eq(i)).build().forCurrentThread().unique();
                numbers.add(box.getNumber());
            }
        }
        return numbers;
    }

    /**
     * 删除对应numbers上的数据
     *
     * @param numbers
     */
    private void deleteDateByNumbers(final List<Integer> numbers) {
        //等待框
        View view = View.inflate(context, R.layout.duplicate_alert_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);
        final AlertDialog mDialog = builder.create();
        mDialog.show();
        mDialog.setCanceledOnTouchOutside(false);

        StringBuilder sb = new StringBuilder();
        for (int i : numbers) {
            sb.append(i).append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        RequestParams requestParams = new RequestParams();
        String accessToken = SharedPreUtil.getString("access_token");
        requestParams.addHeader("Authorization", "Bearer " + accessToken);
        requestParams.addBodyParameter("device_uid", MyApplication.flbId);
        requestParams.addBodyParameter("numbers", sb.toString());
        HttpUtils httpUtils = new HttpUtils();
        httpUtils.configCurrentHttpCacheExpiry(100);
        httpUtils.send(HttpRequest.HttpMethod.POST, MHttpUtils.DELETE_MORE_MEDIC,
                requestParams, new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        KLog.json(responseInfo.result);
                        try {
                            JsonElement jsonElement = new JsonParser().parse(responseInfo.result);
                            JsonObject jsonObject = jsonElement.getAsJsonObject();
                            JsonElement element = jsonObject.get("response");
                            JsonObject jObject = element.getAsJsonObject();
                            JsonElement codeElement = jObject.get("code");
                            int code = codeElement.getAsInt();
                            if (code == 1) {
                                uploadDrugsEveryDay(numbers, mDialog);
                            }
                        } catch (JsonSyntaxException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        KLog.d(s);
                        mDialog.dismiss();
                        ToastUtil.showShort(getString(R.string.submit_error));
                    }
                });
    }

    /**
     * 上传每天设置的药物
     *
     * @param numbers [0,4,8...]
     * @param mDialog
     */
    private void uploadDrugsEveryDay(final List<Integer> numbers, final AlertDialog mDialog) {
        String accessToken = SharedPreUtil.getString("access_token");
        PullDownMedicine pullDownMedicine = new PullDownMedicine();
        pullDownMedicine.setDevice_uid(MyApplication.flbId);
        pullDownMedicine.setNumber(numbers);
        pullDownMedicine.setEat_time(mTvTime.getText().toString());
        pullDownMedicine.setUnit("粒");
        ArrayList<PullDownMedicine.MedicineListEntity> medicine_list = new ArrayList<>();
        for (String string : SetRepeatDrug.mList) {
            PullDownMedicine.MedicineListEntity medicineListEntity = new PullDownMedicine.MedicineListEntity();
            medicineListEntity.setMedicine_name(Integer.valueOf(string.split("/")[2]));
            medicineListEntity.setQuantity(Integer.valueOf(string.split("/")[1]));
            medicine_list.add(medicineListEntity);
        }
        pullDownMedicine.setMedicine_list(medicine_list);
        RequestParams requestParams = new RequestParams();
        try {
            String json = new Gson().toJson(pullDownMedicine);
            requestParams.addHeader("Authorization", "Bearer " + accessToken);
            requestParams.addHeader("Content-Type", "application/json");
            requestParams.setBodyEntity(new StringEntity(json, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        HttpUtils httpUtils = new HttpUtils();
        httpUtils.configCurrentHttpCacheExpiry(100);
        httpUtils.send(HttpRequest.HttpMethod.POST, MHttpUtils.LOADING_EVERY_DAY, requestParams, new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        KLog.json(responseInfo.result);
                        int code = 0;
                        try {
                            JsonElement jsonElement = new JsonParser().parse(responseInfo.result);
                            if (jsonElement != null && jsonElement.isJsonObject()) {
                                JsonObject jsonObject = jsonElement.getAsJsonObject();
                                JsonElement element = jsonObject.get("response");
                                if (element != null && element.isJsonObject()) {
                                    JsonObject jObject = element.getAsJsonObject();
                                    JsonElement codeElement = jObject.get("code");
                                    code = codeElement.getAsInt();
                                }
                            }
                            if (code == 1) {
                                boolean flag = MyApplication.getDaoSession().callInTx(new Callable<Boolean>() {
                                    @Override
                                    public Boolean call() throws Exception {
                                        for (int number : numbers) {
                                            deleteSetMedicData(number);
                                        }
                                        return true;
                                    }
                                });
                                if (flag && mDialog != null) {
                                    mDialog.dismiss();
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        ToastUtil.showShort(getString(R.string.submit_error));
                    }
                }
        );
    }

    private void deleteSetMedicData(int number) {
        QueryBuilder<SetMedicTime> b = DaoHolder.getSetMedicTimeDao(
                SetMedicTimeDao.Properties.Device_uid.eq(MyApplication.flbId),
                SetMedicTimeDao.Properties.Number.eq(number));
        if (b.buildCount().count() > 0) {
            List<SetMedicTime> list = b.build().forCurrentThread().list();
            MyApplication.getDaoSession().getSetMedicTimeDao().deleteInTx(list);
        }
    }


    private void setCurrentDayEnable(CheckBox checkBox_1, CheckBox checkBox_2,
                                     CheckBox checkBox_3, CheckBox checkBox_4, CheckBox checkBox_5,
                                     CheckBox checkBox_6, CheckBox checkBox_7) {
        if (getString(R.string.MON).equals(cWeek)) {
            checkBox_1.setChecked(true);
            checkBox_1.setEnabled(false);
        } else if (getString(R.string.TUE).equals(cWeek)) {
            checkBox_2.setChecked(true);
            checkBox_2.setEnabled(false);
        } else if (getString(R.string.WED).equals(cWeek)) {
            checkBox_3.setChecked(true);
            checkBox_3.setEnabled(false);
        } else if (getString(R.string.THU).equals(cWeek)) {
            checkBox_4.setChecked(true);
            checkBox_4.setEnabled(false);
        } else if (getString(R.string.FRI).equals(cWeek)) {
            checkBox_5.setChecked(true);
            checkBox_5.setEnabled(false);
        } else if (getString(R.string.SAT).equals(cWeek)) {
            checkBox_6.setChecked(true);
            checkBox_6.setEnabled(false);
        } else if (getString(R.string.SUN).equals(cWeek)) {
            checkBox_7.setChecked(true);
            checkBox_7.setEnabled(false);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.cb_1:
                if (isChecked) {
                    cWeeks[1] = true;
                } else {
                    cWeeks[1] = false;
                }
                break;
            case R.id.cb_2:
                if (isChecked) {
                    cWeeks[2] = true;
                } else {
                    cWeeks[2] = false;
                }
                break;
            case R.id.cb_3:
                if (isChecked) {
                    cWeeks[3] = true;
                } else {
                    cWeeks[3] = false;
                }
                break;
            case R.id.cb_4:
                if (isChecked) {
                    cWeeks[4] = true;
                } else {
                    cWeeks[4] = false;
                }
                break;
            case R.id.cb_5:
                if (isChecked) {
                    cWeeks[5] = true;
                } else {
                    cWeeks[5] = false;
                }
                break;
            case R.id.cb_6:
                if (isChecked) {
                    cWeeks[6] = true;
                } else {
                    cWeeks[6] = false;
                }
                break;
            case R.id.cb_7:
                if (isChecked) {
                    cWeeks[7] = true;
                } else {
                    cWeeks[7] = false;
                }
                break;
        }
    }
}
