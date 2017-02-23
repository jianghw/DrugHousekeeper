package com.cjy.flb.receiver;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.cjy.flb.R;
import com.cjy.flb.activity.MainActivity;
import com.cjy.flb.activity.MyApplication;
import com.cjy.flb.bean.PullSetDrugsBean;
import com.cjy.flb.dao.BoxDao;
import com.cjy.flb.dao.DaoHolder;
import com.cjy.flb.dao.EatTimeDao;
import com.cjy.flb.dao.IsNotifDao;
import com.cjy.flb.db.Box;
import com.cjy.flb.db.EatTime;
import com.cjy.flb.db.IsNotif;
import com.cjy.flb.service.AlarmClockService;
import com.cjy.flb.utils.MHttpUtils;
import com.cjy.flb.utils.SharedPreUtil;
import com.cjy.flb.utils.ToastUtil;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.socks.library.KLog;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by 蒋宏伟 on 2016/1/26.
 */
public class ManytimeAlarmReceiver extends BroadcastReceiver {
    private String accessToken;
    private Context context;

    @Override
    public void onReceive(final Context context, Intent intent)
    {
        boolean isServiceRunning = false;
        ActivityManager manager = (ActivityManager) MyApplication.getInstance().getSystemService(Context
                .ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("com.cjy.flb.service.AlarmClockService".equals(service.service.getClassName())) {
                isServiceRunning = true;
            }
        }
        KLog.i(isServiceRunning);
        if (!isServiceRunning) {
            Intent i = new Intent(context, com.cjy.flb.service.AlarmClockService.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startService(i);
        }

        final PendingResult result = goAsync();
        AlarmAlertWakeLock.acquireCpuWakeLock(context);
        this.context = context;

        Calendar mCalendar = Calendar.getInstance();
        final long cTime = mCalendar.getTimeInMillis();

        DateFormat mDateFormat = SimpleDateFormat.getTimeInstance();
        final String time = mDateFormat.format(mCalendar.getTime());
        AsyncHandler.post(new Runnable() {
            @Override
            public void run()
            {
                interceptNotificationTimer(context, time);
                result.finish();
                AlarmClockService.scheduleInstanceStateChange(context, cTime);
                AlarmAlertWakeLock.releaseCpuLock();
            }
        });

    }

    /**
     * 获取本手机绑定的药盒id
     *
     * @param context
     * @param time
     */
    private void interceptNotificationTimer(Context context, String time)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.SIMPLIFIED_CHINESE);
        final String day = sdf.format(new Date());
        QueryBuilder<IsNotif> buidlder = DaoHolder.getIsNotifDao(IsNotifDao.Properties.Today.eq(day));
        if (buidlder.buildCount().count() > 0) {
            List<IsNotif> isNotifs = buidlder.build().forCurrentThread().list();
            accessToken = SharedPreUtil.getString("access_token");
            if (accessToken != null) {
                MyApplication.accessToken = accessToken;
            }
            if (isNotifs != null) {
                //查询每个药盒对应的信息
                try {
                    for (IsNotif isNotif : isNotifs) {
                        synchronized (this){
                            getTheData(isNotif);
                        }
                        Thread.sleep(5 * 1000);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {//隔天查询，更新数据
            MyApplication.getDaoSession().getIsNotifDao().deleteAll();
            List<EatTime> list = MyApplication.getDaoSession().getEatTimeDao().loadAll();
            if (list.size() > 0) {
                try {
                    for (EatTime eatTime : list) {
                        QueryBuilder<IsNotif> builder = DaoHolder.getIsNotifDao(
                                IsNotifDao.Properties.Medic_id.eq(eatTime.getMedic_id()),
                                IsNotifDao.Properties.Today.eq(day));
                        if (builder.buildCount().count() == 0) {
                            IsNotif isNotif = new IsNotif(null, day, eatTime.getMedic_id(), eatTime.getName(),
                                    eatTime.getPhone(), true, 0, true, 0, true, 0, true, 0);
                            MyApplication.getDaoSession().getIsNotifDao().insertInTx(isNotif);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void getTheData(final IsNotif isNotif)
    {
        final Box mBox = MyApplication.getDaoSession().getBoxDao().queryBuilder()
                .where(BoxDao.Properties.Day_in_week.eq(getWeekStringToInt()),
                        BoxDao.Properties.Point_in_time.eq("MORN")).build().unique();
        final Box nBox = MyApplication.getDaoSession().getBoxDao().queryBuilder()
                .where(BoxDao.Properties.Day_in_week.eq(getWeekStringToInt()),
                        BoxDao.Properties.Point_in_time.eq("NOON")).build().unique();
        final Box aBox = MyApplication.getDaoSession().getBoxDao().queryBuilder()
                .where(BoxDao.Properties.Day_in_week.eq(getWeekStringToInt()),
                        BoxDao.Properties.Point_in_time.eq("AFTERNOON")).build().unique();
        final Box eBox = MyApplication.getDaoSession().getBoxDao().queryBuilder()
                .where(BoxDao.Properties.Day_in_week.eq(getWeekStringToInt()),
                        BoxDao.Properties.Point_in_time.eq("NIGHT")).build().unique();

        final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.SIMPLIFIED_CHINESE);
        final String string = sdf.format(new Date());

        RequestParams requestParams = new RequestParams();
        requestParams.addHeader("Authorization", "Bearer " + SharedPreUtil.getString("access_token"));
        MyApplication.getHttpUtils().send(HttpRequest.HttpMethod.GET,
                MHttpUtils.PULL_DOWN_URL + isNotif.getMedic_id(), requestParams,
                new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo)
                    {
                        JsonElement jsonElement = new JsonParser().parse(responseInfo.result);
                        JsonObject jsonObject = null;
                        if (jsonElement.isJsonObject()) {
                            jsonObject = jsonElement.getAsJsonObject();
                        }
                        if (jsonObject != null) {
                            JsonElement element = jsonObject.get("response");
                            if (element != null && element.isJsonArray()) {
                                //保存数据
                                JsonArray array = element.getAsJsonArray();
                                //默认当前时间毫秒级
                                long T = 0;
                                long mT = -1000000000000000000L;
                                long nT = -1000000000000000000L;
                                long aT = -1000000000000000000L;
                                long eT = -1000000000000000000L;
                                String mTime = null, nTime = null, aTime = null, eTime = null;

                                for (JsonElement element1 : array) {
                                    PullSetDrugsBean dateQueryMedic = new Gson().fromJson(element1, PullSetDrugsBean
                                            .class);
                                    int number = dateQueryMedic.getNumber();
                                    String time = dateQueryMedic.getEat_time();
                                    if (time != null && !TextUtils.isEmpty(time)) {
                                        if (number == mBox.getNumber()) {
                                            mTime = time;
                                        } else if (number == nBox.getNumber()) {
                                            nTime = time;
                                        } else if (number == aBox.getNumber()) {
                                            aTime = time;
                                        } else if (number == eBox.getNumber()) {
                                            eTime = time;
                                        }
                                    }
                                    try {
                                        T = sdf.parse(string).getTime();
                                        mT = sdf.parse(mTime).getTime();
                                        nT = sdf.parse(nTime).getTime();
                                        aT = sdf.parse(aTime).getTime();
                                        eT = sdf.parse(eTime).getTime();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                                updateEatTimeDao(mT, nT, aT, eT, mTime, nTime, aTime, eTime, isNotif);

                                startUpCapital(mT, nT, aT, eT, T, mTime, nTime, aTime, eTime, isNotif);
                            }
                        }
                    }

                    @Override
                    public void onFailure(HttpException e, String s)
                    {
                        KLog.i(s);
                        QueryBuilder<EatTime> builder = DaoHolder.getEatTimeDao(
                                EatTimeDao.Properties.Medic_id.eq(isNotif.getMedic_id()));
                        long count = builder.buildCount().count();
                        if (count == 1) {
                            try {
                                long T = sdf.parse(string).getTime();
                                EatTime eatTime2 = builder.build().forCurrentThread().unique();
                                startUpCapital(eatTime2.getMorn(), eatTime2.getNon(), eatTime2.getAfter(),
                                        eatTime2.getEven(), T, eatTime2.getMornS(), eatTime2.getNonS(),
                                        eatTime2.getAfterS(), eatTime2.getEvenS(), isNotif);
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                });
    }

    private void updateEatTimeDao(long mT, long nT, long aT, long eT,
                                  String mTime, String nTime, String aTime, String eTime, IsNotif isNotif)
    {
        QueryBuilder<EatTime> builder = DaoHolder.getEatTimeDao(
                EatTimeDao.Properties.Medic_id.eq(isNotif.getMedic_id()));
        long count = builder.buildCount().count();
        if (count == 0) {
            EatTime eatTime = new EatTime(null, isNotif.getMedic_id(), mT, nT, aT, eT,
                    mTime, nTime, aTime, eTime, isNotif.getMedic_name(), isNotif
                    .getMedic_phone());
            MyApplication.getDaoSession().getEatTimeDao().insertInTx(eatTime);
        } else if (count == 1) {
            EatTime eatTime2 = builder.build().unique();
            eatTime2.setMorn(mT);
            eatTime2.setNon(nT);
            eatTime2.setAfter(aT);
            eatTime2.setEven(eT);
            eatTime2.setMornS(mTime);
            eatTime2.setNonS(nTime);
            eatTime2.setAfterS(aTime);
            eatTime2.setEvenS(eTime);
            eatTime2.setName(isNotif.getMedic_name());
            eatTime2.setPhone(isNotif.getMedic_phone());
            MyApplication.getDaoSession().getEatTimeDao().updateInTx(eatTime2);
        }
    }

    /**
     * @param mT      上午时间阈值 long .getTime()
     * @param nT
     * @param aT
     * @param eT
     * @param t       当前时间
     * @param mTime
     * @param nTime
     * @param aTime
     * @param eTime
     * @param isNotif
     */
    private void startUpCapital(long mT, long nT, long aT, long eT, long t, String mTime,
                                String nTime, String aTime, String eTime, IsNotif isNotif)
    {
        //当前时间>设定时间+12分钟&&<设定时间+30&&<中午时间
        if (t >= mT + 1000 * 60 * 14 && t <= mT + 1000 * 60 * 35 && mT != -1000000000000000000L) {//08:15-08:30
            //TODO 上午是否收到过通知
            if (isNotif.getIsMOut()) {
                setNotification(mTime, isNotif.getMedic_name());
                showTimeOutAlert(mTime, isNotif.getMedic_name(), isNotif.getMedic_phone());
                isNotif.setIsMOut(false);
                isNotif.setIsM(1);
            }
        } else if (t >= nT + 1000 * 60 * 14 && t <= nT + 1000 * 60 * 35 && nT != -1000000000000000000L) {//中午时段
            if (isNotif.getIsNOut()) {
                setNotification(nTime, isNotif.getMedic_name());
                showTimeOutAlert(nTime, isNotif.getMedic_name(), isNotif.getMedic_phone());
                isNotif.setIsNOut(false);
                isNotif.setIsN(1);
            }
        } else if (t >= aT + 1000 * 60 * 14 && t <= aT + 1000 * 60 * 35 && aT != -1000000000000000000L) {
            if (isNotif.getIsAOut()) {
                setNotification(aTime, isNotif.getMedic_name());
                showTimeOutAlert(aTime, isNotif.getMedic_name(), isNotif.getMedic_phone());
                isNotif.setIsAOut(false);
                isNotif.setIsA(1);
            }
        } else if (t >= eT + 1000 * 60 * 14 && t <= eT + 1000 * 60 * 35 && eT != -1000000000000000000L) {
            if (isNotif.getIsEOut()) {
                setNotification(eTime, isNotif.getMedic_name());
                showTimeOutAlert(eTime, isNotif.getMedic_name(), isNotif.getMedic_phone());
                isNotif.setIsEOut(false);
                isNotif.setIsE(1);
            }
        }
        MyApplication.getDaoSession().getIsNotifDao().updateInTx(isNotif);
    }

    private String getWeekStringToInt()
    {
        Date date = new Date();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        int week = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        String strWeek = null;
        switch (week) {
            case 1:
                strWeek = "1";
                break;
            case 2:
                strWeek = "2";
                break;
            case 3:
                strWeek = "3";
                break;
            case 4:
                strWeek = "4";
                break;
            case 5:
                strWeek = "5";
                break;
            case 6:
                strWeek = "6";
                break;
            case 0:
                strWeek = "7";
                break;
            default:
                break;
        }
        return strWeek;
    }

    public void setNotification(String time, String name)
    {
        //获取NotificationManager实例
        NotificationManager manager = (NotificationManager) context.getApplicationContext().getSystemService(Context
                .NOTIFICATION_SERVICE);
        //构造Notification.Builder 对象
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        //设置Notification图标
        builder.setSmallIcon(R.drawable.ic_ygj_medic);
        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_ygj));
        //        设置Notification tickertext
        builder.setTicker(name + " " + time + context.getString(R.string.not_take_on_time)+"!");
        //设置通知的题目
        builder.setContentTitle(name);
        //设置通知的内容
        builder.setContentText(time + context.getString(R.string.not_take_on_time)+"!");
        //        builder.setContentInfo("Info");
        //设置通知可以被自动取消
        builder.setAutoCancel(true);
        //设置通知栏显示的Notification按时间排序
        builder.setWhen(System.currentTimeMillis());
        //设置其他物理属性，包括通知提示音、震动、屏幕下方LED灯闪烁
        if (SharedPreUtil.getBoolean("message_voice", true)) {
            builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));//这里设置一个本地文件为提示音
        }
        if (SharedPreUtil.getBoolean("message_vibration", true)) {
            builder.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000, 1000});
        }
        builder.setLights(Color.RED, 0, 1);
        //设置该通知点击后将要启动的Intent,这里需要注意PendingIntent的用法,构造方法中的四个参数(context,int requestCode,Intent,int flags);
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(context, 0, intent, 0);
        builder.setContentIntent(pi);
        //实例化Notification
        if (SharedPreUtil.getBoolean("message_notification", true)) {
            Notification notification = builder.build();//notify(int id,notification对象);id用来标示每个notification
            manager.notify(new Random().nextInt(100), notification);
        }
    }

    private void showTimeOutAlert(String time, String name, final String phone)
    {
        View view = View.inflate(context, R.layout.timeout_alert_dialog, null);
        TextView userName = (TextView) view.findViewById(R.id.tv_userName);
        TextView tv = (TextView) view.findViewById(R.id.tv_time);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);
        final AlertDialog mDialog = builder.create();
        mDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);//设定为系统级警告，关键
        mDialog.show();
        mDialog.setCanceledOnTouchOutside(false);
        userName.setText(name);
        tv.setText(time);
        Button callBtn = (Button) view.findViewById(R.id.btn_call);
        callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (!phone.equals("")) {
                    Pattern p = Pattern.compile("\\d+?");
                    Matcher match = p.matcher(phone);
                    //正则验证输入的是否为数字
                    if (match.matches()) {
                        try {
                            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        ToastUtil.showShort(context.getString(R.string.entry_phone_ok));
                    }
                    mDialog.dismiss();
                }
            }
        });
        Button iBtn = (Button) view.findViewById(R.id.btn_ignore);
        iBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                mDialog.dismiss();
            }
        });
    }
}
