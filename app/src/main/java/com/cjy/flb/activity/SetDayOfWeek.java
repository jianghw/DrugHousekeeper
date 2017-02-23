package com.cjy.flb.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.brithpicker.WeekPopupWindow;
import com.cjy.flb.R;
import com.cjy.flb.dao.BoxDao;
import com.cjy.flb.dao.DaoHolder;
import com.cjy.flb.dao.RepeatWeekDao;
import com.cjy.flb.dao.SetMedicTimeDao;
import com.cjy.flb.db.Box;
import com.cjy.flb.db.RepeatWeek;
import com.cjy.flb.db.SetMedicTime;
import com.cjy.flb.event.PerodOfDayEvent;
import com.cjy.flb.manager.AppManager;
import com.cjy.flb.utils.MHttpUtils;
import com.cjy.flb.utils.SharedPreUtil;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.dao.query.WhereCondition;
import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2015/12/25 0025.
 */
public class SetDayOfWeek extends BaseActivity implements View.OnClickListener, CompoundButton
        .OnCheckedChangeListener {


    @Bind(R.id.ll_top_back)
    LinearLayout mLlTopBack;

    @Bind(R.id.tv_week)
    TextView mTvWeek;
    @Bind(R.id.rl_set_week)
    RelativeLayout mRlSetWeek;
    @Bind(R.id.cbox_m)
    CheckBox mCboxM;
    @Bind(R.id.lrlay_1)
    LinearLayout mLrlay1;
    @Bind(R.id.cbox_n)
    CheckBox mCboxN;
    @Bind(R.id.lrlay_2)
    LinearLayout mLrlay2;
    @Bind(R.id.cbox_a)
    CheckBox mCboxA;
    @Bind(R.id.lrlay_3)
    LinearLayout mLrlay3;
    @Bind(R.id.cbox_e)
    CheckBox mCboxE;
    @Bind(R.id.lrlay_4)
    LinearLayout mLrlay4;

    private HashMap<String, String> initHasMap;
    private HashMap<String, Boolean> cBoxMap;
    //星期选择器相关
    private WeekPopupWindow pwWeek;
    private ArrayList<String> timeWeeks = new ArrayList<>();

    private static final String AM = "MORN";
    private static final String NOON = "NOON";
    private static final String PM = "AFTERNOON";
    private static final String NIGHT = "NIGHT";
    //对应number数据
    private int[] days;

    @Override
    public void initView() {
        setContentView(R.layout.activity_set_day_ofweek);
        ButterKnife.bind(this);

        initBar(getString(R.string.date_medic_times), false, false);
    }

    @Override
    public void initData() {
        //初始化当前日期数据
        initHasMap = new HashMap<>();
        cBoxMap = new HashMap<>();

        Bundle bundle = getIntent().getBundleExtra("bundle");
        String week = bundle.getString("week", getString(R.string.today));

        if (week != null && week.equals(getString(R.string.today))) {
            SimpleDateFormat sdf = new SimpleDateFormat("E");
            mTvWeek.setText(sdf.format(new Date()));
        } else {
            mTvWeek.setText(week);
        }

        if (context != null) {
            //星期选择器
            pwWeek = new WeekPopupWindow(context);
        }
        //初始化星期
        initWeek();
        //初始化对应每天是否有设置的勾选
        initDataIsChoiced();
    }

    @Override
    public void initListener() {
        mRlSetWeek.setOnClickListener(this);

        mCboxM.setOnCheckedChangeListener(this);
        mCboxN.setOnCheckedChangeListener(this);
        mCboxA.setOnCheckedChangeListener(this);
        mCboxE.setOnCheckedChangeListener(this);

        mLrlay1.setOnClickListener(this);
        mLrlay2.setOnClickListener(this);
        mLrlay3.setOnClickListener(this);
        mLrlay4.setOnClickListener(this);

        llTopBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickBack();
            }
        });
    }

    /**
     * 回退键，提交没有勾选的删除数据
     */
    private void onClickBack() {
        if (initHasMap.containsKey(SetDayOfWeek.AM)
                && !cBoxMap.containsKey(SetDayOfWeek.AM)) {
            showAlerDialog();
        } else if (initHasMap.containsKey(SetDayOfWeek.NOON)
                && !cBoxMap.containsKey(SetDayOfWeek.NOON)) {
            showAlerDialog();
        } else if (initHasMap.containsKey(SetDayOfWeek.PM)
                && !cBoxMap.containsKey(SetDayOfWeek.PM)) {
            showAlerDialog();
        } else if (initHasMap.containsKey(SetDayOfWeek.NIGHT)
                && !cBoxMap.containsKey(SetDayOfWeek.NIGHT)) {
            showAlerDialog();
        } else {
            deleteCorrespondData();
        }
    }

    private void showAlerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SetDayOfWeek.this);
        builder.setTitle(getString(R.string.important_notification))
                .setMessage(getString(R.string.important_message))
                .setPositiveButton(getString(R.string.btn_ok), new DialogInterface
                        .OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        deleteCorrespondData();
                    }
                })
                .setNegativeButton(getString(R.string.btn_cancel), new DialogInterface
                        .OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * 按number删除药物数据
     */
    private void deleteCorrespondData() {
        final StringBuilder sb = new StringBuilder();
        if (initHasMap.containsKey(SetDayOfWeek.AM)
                && !cBoxMap.containsKey(SetDayOfWeek.AM)) {
            sb.append(days[0]).append(",");
        }
        if (initHasMap.containsKey(SetDayOfWeek.NOON)
                && !cBoxMap.containsKey(SetDayOfWeek.NOON)) {
            sb.append(days[1]).append(",");
        }
        if (initHasMap.containsKey(SetDayOfWeek.PM)
                && !cBoxMap.containsKey(SetDayOfWeek.PM)) {
            sb.append(days[2]).append(",");
        }
        if (initHasMap.containsKey(SetDayOfWeek.NIGHT)
                && !cBoxMap.containsKey(SetDayOfWeek.NIGHT)) {
            sb.append(days[3]).append(",");
        }

        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }

        if (!sb.toString().isEmpty()) {//要删除项不是空的时候
            final RequestParams requestParams = new RequestParams();
            String accessToken = SharedPreUtil.getString("access_token");
            requestParams.addHeader("Authorization", "Bearer " + accessToken);
            requestParams.addBodyParameter("device_uid", MyApplication.flbId);
            requestParams.addBodyParameter("numbers", sb.toString());
            HttpUtils httpUtils = new HttpUtils();
            httpUtils.configCurrentHttpCacheExpiry(0);
            httpUtils.send(HttpRequest.HttpMethod.POST, MHttpUtils.DELETE_MORE_MEDIC, requestParams,
                    new RequestCallBack<String>() {
                        @Override
                        public void onSuccess(ResponseInfo<String> responseInfo) {
                            KLog.json(responseInfo.result);
                            int code = 0;
                            try {
                                JsonElement jsonElement = new JsonParser().parse(responseInfo.result);
                                JsonObject jsonObject = jsonElement.getAsJsonObject();
                                JsonElement element = jsonObject.get("response");
                                JsonObject jObject = element.getAsJsonObject();
                                JsonElement codeElement = jObject.get("code");
                                code = codeElement.getAsInt();
                            } catch (JsonSyntaxException e) {
                                e.printStackTrace();
                            }
                            if (code == 1) {
                                EventBus.getDefault().postSticky(new PerodOfDayEvent(mTvWeek
                                        .getText().toString(), cBoxMap));
                                //删除数据库中对应id
                                (new AsyncTask<Void, Void, Boolean>() {
                                    @Override
                                    protected Boolean doInBackground(Void... params) {
                                        try {
                                            Pattern p = Pattern.compile("(\\d+)");
                                            Matcher matcher = p.matcher(sb.toString());
                                            while (matcher.find()) {
                                                databaseOperation(matcher);
                                            }
                                            return true;
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            return false;
                                        }
                                    }

                                    @Override
                                    protected void onPostExecute(Boolean flag) {
                                        try {
                                            if (flag) {
                                                AppManager.getAppManager().finishActivity
                                                        (SetDayOfWeek.class);
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }).execute();
                            }
                        }

                        @Override
                        public void onFailure(HttpException e, String s) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(SetDayOfWeek
                                    .this);
                            builder.setMessage(getString(R.string.delete_is_exit))
                                    .setPositiveButton(getString(R.string.continue_up), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    })
                                    .setNegativeButton(getString(R.string.exit_dialog), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            AppManager.getAppManager().finishActivity
                                                    (SetDayOfWeek.class);
                                        }
                                    });
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    });
        } else {
            //判断有无修改时
       /*     if (initHasMap.size() < cBoxMap.size()) {
                EventBus.getDefault().postSticky(new PerodOfDayEvent(mTvWeek.getText().toString()
                , cBoxMap));
            } else if (initHasMap.size() == cBoxMap.size()) {
                SimpleDateFormat sdf = new SimpleDateFormat("E");
                if (mTvWeek.getText().toString().equals(sdf.format(new Date()))) {
                    for (String key : initHasMap.keySet()) {
                        if (!cBoxMap.containsKey(key)) {
                            EventBus.getDefault().postSticky(new PerodOfDayEvent(mTvWeek.getText
                            ().toString(), cBoxMap));
                        }
                    }
                } else {
                    EventBus.getDefault().postSticky(new PerodOfDayEvent(mTvWeek.getText()
                    .toString(), cBoxMap));
                }
            }*/
            EventBus.getDefault().postSticky(new PerodOfDayEvent(mTvWeek.getText().toString(), cBoxMap));
            AppManager.getAppManager().finishActivity(SetDayOfWeek.class);
        }
    }

    private void databaseOperation(Matcher matcher) {
        String find = matcher.group(1);
        QueryBuilder<SetMedicTime> builder = MyApplication.getDaoSession().getSetMedicTimeDao().queryBuilder()
                .where(SetMedicTimeDao.Properties.Device_uid.eq(MyApplication.flbId),
                        SetMedicTimeDao.Properties.Number.eq(find));

        if (builder.buildCount().count() > 0) {
            List<SetMedicTime> list = builder.build().forCurrentThread().list();
            MyApplication.getDaoSession().getSetMedicTimeDao().deleteInTx(list);
        }

        QueryBuilder<Box> builder1 = DaoHolder.getBoxDao(BoxDao.Properties.Number.eq(find));
        if (builder1.buildCount().count() == 1) {
            Box box = builder1.build().forCurrentThread().unique();
            switch (box.getDay_in_week()) {
                case "1":
                    queryToModify(RepeatWeekDao.Properties.IsMorn.eq(true), 1);
                    break;
                case "2":
                    queryToModify(RepeatWeekDao.Properties.IsTue.eq(true), 2);
                    break;
                case "3":
                    queryToModify(RepeatWeekDao.Properties.IsWed.eq(true), 3);
                    break;
                case "4":
                    queryToModify(RepeatWeekDao.Properties.IsThu.eq(true), 4);
                    break;
                case "5":
                    queryToModify(RepeatWeekDao.Properties.IsFri.eq(true), 5);
                    break;
                case "6":
                    queryToModify(RepeatWeekDao.Properties.IsSat.eq(true), 6);
                    break;
                case "7":
                    queryToModify(RepeatWeekDao.Properties.IsSun.eq(true), 7);
                    break;
            }
        }
    }

    private void queryToModify(WhereCondition eq, int i) {
        QueryBuilder<RepeatWeek> builder = MyApplication.getDaoSession().getRepeatWeekDao()
                .queryBuilder().where(RepeatWeekDao.Properties.Medic_id.eq(MyApplication.flbId), eq);
        if (builder.buildCount().count() > 0) {
            List<RepeatWeek> repeatList = builder.build().forCurrentThread().list();
            for (RepeatWeek rw : repeatList) {
                rw.setIsEvery(false);
                switch (i) {
                    case 1:
                        rw.setIsMorn(false);
                        break;
                    case 2:
                        rw.setIsTue(false);
                        break;
                    case 3:
                        rw.setIsWed(false);
                        break;
                    case 4:
                        rw.setIsThu(false);
                        break;
                    case 5:
                        rw.setIsFri(false);
                        break;
                    case 6:
                        rw.setIsSat(false);
                        break;
                    case 7:
                        rw.setIsSun(false);
                        break;
                }
            }
            MyApplication.getDaoSession().getRepeatWeekDao().updateInTx(repeatList);
        }
    }

    @Override
    protected void onDestroy() {
        ButterKnife.unbind(this);
        if (timeWeeks.size() > 0) timeWeeks.clear();
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_HOME:
                return true;
            case KeyEvent.KEYCODE_BACK:
                onClickBack();
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_set_week:
                pwWeek.showAtLocation(mLrlay4, Gravity.BOTTOM, 0, 0);
                initWeekData();
                break;
            case R.id.lrlay_1:
                if (mCboxM.isChecked()) {
                    mCboxM.setChecked(false);
                } else {
                    mCboxM.setChecked(true);
                }
                break;
            case R.id.lrlay_2:
                if (mCboxN.isChecked()) {
                    mCboxN.setChecked(false);
                } else {
                    mCboxN.setChecked(true);
                }
                break;
            case R.id.lrlay_3:
                if (mCboxA.isChecked()) {
                    mCboxA.setChecked(false);
                } else {
                    mCboxA.setChecked(true);
                }
                break;
            case R.id.lrlay_4:
                if (mCboxE.isChecked()) {
                    mCboxE.setChecked(false);
                } else {
                    mCboxE.setChecked(true);
                }
                break;
        }
    }

    private void initWeek() {
        timeWeeks.add(getString(R.string.MON));
        timeWeeks.add(getString(R.string.TUE));
        timeWeeks.add(getString(R.string.WED));
        timeWeeks.add(getString(R.string.THU));
        timeWeeks.add(getString(R.string.FRI));
        timeWeeks.add(getString(R.string.SAT));
        timeWeeks.add(getString(R.string.SUN));
    }

    /**
     * 星期选择器
     */
    private void initWeekData() {
        pwWeek.setPicker(timeWeeks);
        pwWeek.setCyclic(false);
        pwWeek.setSelectOptions(0);
        pwWeek.setOnoptionsSelectListener(new WeekPopupWindow.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2) {
                String week = timeWeeks.get(options1);
                mTvWeek.setText(week);

                if (initHasMap != null && initHasMap.size() > 0) initHasMap.clear();
                if (cBoxMap != null && cBoxMap.size() > 0) cBoxMap.clear();
                initDataIsChoiced();
            }
        });
    }

    /**
     * 初始化勾选框状态
     */
    private void initDataIsChoiced() {
        mCboxM.setChecked(false);
        mCboxN.setChecked(false);
        mCboxA.setChecked(false);
        mCboxE.setChecked(false);

        String week = mTvWeek.getText().toString();
        if (week.equals(getString(R.string.MON)) || week.equals(getString(R.string.MON_X))) {
            days = new int[]{0, 1, 2, 3};
        } else if (week.equals(getString(R.string.TUE)) || week.equals(getString(R.string.TUE_X))) {
            days = new int[]{4, 5, 6, 7};
        } else if (week.equals(getString(R.string.WED)) || week.equals(getString(R.string.WED_X))) {
            days = new int[]{8, 9, 10, 11};
        } else if (week.equals(getString(R.string.THU)) || week.equals(getString(R.string.THU_X))) {
            days = new int[]{12, 13, 14, 15};
        } else if (week.equals(getString(R.string.FRI)) || week.equals(getString(R.string.FRI_X))) {
            days = new int[]{16, 17, 18, 19};
        } else if (week.equals(getString(R.string.SAT)) || week.equals(getString(R.string.SAT_X))) {
            days = new int[]{20, 21, 22, 23};
        } else if (week.equals(getString(R.string.SUN)) || week.equals(getString(R.string.SUN_X))) {
            days = new int[]{24, 25, 26, 27};
        } else {
            days = new int[4];
        }
        for (int i = 0; i < 4; i++) {
            QueryBuilder<SetMedicTime> builder = DaoHolder.getSetMedicTimeDao(
                    SetMedicTimeDao.Properties.Device_uid.eq(MyApplication.flbId),
                    SetMedicTimeDao.Properties.Number.eq(String.valueOf(days[i])));
            if (builder.buildCount().count() > 0) {
                switch (i) {
                    case 0:
                        initHasMap.put(SetDayOfWeek.AM, String.valueOf(days[i]));
                        mCboxM.setChecked(true);
                        cBoxMap.put(SetDayOfWeek.AM, true);
                        break;
                    case 1:
                        initHasMap.put(SetDayOfWeek.NOON, String.valueOf(days[i]));
                        mCboxN.setChecked(true);
                        cBoxMap.put(SetDayOfWeek.NOON, true);
                        break;
                    case 2:
                        initHasMap.put(SetDayOfWeek.PM, String.valueOf(days[i]));
                        mCboxA.setChecked(true);
                        cBoxMap.put(SetDayOfWeek.PM, true);
                        break;
                    case 3:
                        initHasMap.put(SetDayOfWeek.NIGHT, String.valueOf(days[i]));
                        mCboxE.setChecked(true);
                        cBoxMap.put(SetDayOfWeek.NIGHT, true);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.cbox_m:
                if (isChecked) {
                    cBoxMap.put(SetDayOfWeek.AM, true);
                } else {
                    cBoxMap.remove(SetDayOfWeek.AM);
                }
                break;
            case R.id.cbox_n:
                if (isChecked) {
                    cBoxMap.put(SetDayOfWeek.NOON, true);
                } else {
                    cBoxMap.remove(SetDayOfWeek.NOON);
                }
                break;
            case R.id.cbox_a:
                if (isChecked) {
                    cBoxMap.put(SetDayOfWeek.PM, true);
                } else {
                    cBoxMap.remove(SetDayOfWeek.PM);
                }
                break;
            case R.id.cbox_e:
                if (isChecked) {
                    cBoxMap.put(SetDayOfWeek.NIGHT, true);
                } else {
                    cBoxMap.remove(SetDayOfWeek.NIGHT);
                }
                break;
        }
    }
}
