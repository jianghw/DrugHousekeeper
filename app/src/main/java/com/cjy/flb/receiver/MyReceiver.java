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
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.cjy.flb.R;
import com.cjy.flb.activity.MainActivity;
import com.cjy.flb.activity.MyApplication;
import com.cjy.flb.dao.DaoHolder;
import com.cjy.flb.dao.IsNotifDao;
import com.cjy.flb.db.EatTime;
import com.cjy.flb.db.IsNotif;
import com.cjy.flb.utils.ExampleUtil;
import com.cjy.flb.utils.SharedPreUtil;
import com.cjy.flb.utils.UIUtils;
import com.socks.library.KLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.jpush.android.api.JPushInterface;
import de.greenrobot.dao.query.QueryBuilder;

/**
 * 自定义接收器
 * <p>
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
public class MyReceiver extends BroadcastReceiver {
    private static final String TAG = "JPush";


    @Override
    public void onReceive(Context context, Intent intent) {
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

        Bundle bundle = intent.getExtras();
        //        Log.d(TAG, "[MyReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));

        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            Log.d(TAG, "[MyReceiver] 接收Registration Id : " + regId);
            //send the Registration Id to your server...
        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            //进入这里
            Log.d(TAG, "[MyReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
            processCustomMessage(context, bundle);
        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 接收到推送下来的通知");
            int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
            Log.d(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);
            //TODO 清初除通知
            //            JPushInterface.clearNotificationById(context, notifactionId);
        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 用户点击打开了通知");
            if (!isAppOnForeground()) {
                //打开自定义的Activity
                Intent i = new Intent(context, MainActivity.class);
                i.putExtras(bundle);
                //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(i);
            }

        } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
            //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..

        } else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
            boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
            Log.w(TAG, "[MyReceiver]" + intent.getAction() + " connected state change to " + connected);
        } else {
            Log.d(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
        }
    }

    public boolean isAppOnForeground() {
        // Returns a list of application processes that are running on the
        // device
        ActivityManager activityManager = (ActivityManager) UIUtils.getContext().getApplicationContext()
                .getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = UIUtils.getContext().getApplicationContext().getPackageName();
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        if (appProcesses == null)
            return false;
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            // The name of the process that this object is associated with.
            if (appProcess.processName.equals(packageName)
                    && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }
        return false;
    }

    private void showTimeOutAlert(final Context context, String time, String name, final String phone) {
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
            public void onClick(View v) {
                String num = phone;
                if (!num.equals("")) {
                    Pattern p = Pattern.compile("\\d+?");
                    Matcher match = p.matcher(num);
                    //正则验证输入的是否为数字
                    if (match.matches()) {
                        Intent intent = new Intent("android.intent.action.CALL", Uri.parse("tel:" + num));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                    mDialog.dismiss();
                }
            }
        });
        Button iBtn = (Button) view.findViewById(R.id.btn_ignore);
        iBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
    }

    public void setNotification(Context context, String time, String name) {
        //不是前台时
        //获取NotificationManager实例
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        //构造Notification.Builder 对象
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        //设置Notification图标
        builder.setSmallIcon(R.drawable.ic_ygj_medic);
        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_ygj));
        //        设置Notification tickertext
        builder.setTicker(name + " " + time +context.getString(R.string.not_take_on_time)+"!!");
        //设置通知的题目
        builder.setContentTitle(name);
        //设置通知的内容
        builder.setContentText(time +context.getString(R.string.not_take_on_time)+"!!");
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
            builder.setVibrate(new long[]{1000, 1000, 1000, 1000});
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

    // 打印所有的 intent extra 数据
    private static String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
            } else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
            } else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
                if (bundle.getString(JPushInterface.EXTRA_EXTRA).isEmpty()) {
                    Log.i(TAG, "This message has no Extra data");
                    continue;
                }
                try {
                    JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
                    Iterator<String> it = json.keys();
                    while (it.hasNext()) {
                        String myKey = it.next().toString();
                        sb.append("\nkey:" + key + ", value: [" +
                                myKey + " - " + json.optString(myKey) + "]");
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Get message extra JSON error!");
                }
            } else {
                sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
                if (key.equals("cn.jpush.android.ALERT")) {
                }
            }
        }
        return sb.toString();
    }

    //send msg to MainActivity
    private void processCustomMessage(Context context, Bundle bundle) {
        SimpleDateFormat sdmm = new SimpleDateFormat("HH:mm", Locale.SIMPLIFIED_CHINESE);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.SIMPLIFIED_CHINESE);
        String curTime = sdmm.format(new Date());//现在这个点的时间 08:00
        try {
            long mTime = sdmm.parse("10:59").getTime();
            long nTime = sdmm.parse("13:59").getTime();
            long aTime = sdmm.parse("18:59").getTime();
            long eTime = sdmm.parse("23:59").getTime();

            String today = sdf.format(new Date());
            String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
            String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
            KLog.i(message);
            //收到一条在 11:55 未及时服药通知 2016-02-22
            //收到一条已服药通知 2016-04-13 08:36

            if (!ExampleUtil.isEmpty(extras) && !ExampleUtil.isEmpty(message)) {
                String eat = message.substring(12, 13);
                String day;
                String time;
                if (eat.equals("未")) {
                    day = message.substring(20, 30);
                    time = message.substring(6, 11);
                } else {
                    day = message.substring(10, 20);
                    time = message.substring(21, 26);
                }
                long T = sdmm.parse(time).getTime();//截取通知的时间
                long curT = sdmm.parse(curTime).getTime();//当前时间截取的时间段

                String id = extras.substring(1, extras.length() - 1).split(":")[1].trim();
                String mID = id.substring(1, id.length() - 1).trim();

                KLog.i(day);
                KLog.i(time);
                KLog.i(mID);

                QueryBuilder<IsNotif> builder = DaoHolder.getIsNotifDao(
                        IsNotifDao.Properties.Today.eq(today),
                        IsNotifDao.Properties.Medic_id.eq(mID));
                if (builder.buildCount().count() == 1) {
                    IsNotif isNotif = builder.build().forCurrentThread().unique();
                    if (day.equals(today)) {
                        //T<10:59&&T>当前时间-10分钟&&T<当前时间+35分钟
                        if (T <= mTime && curT <= T + 1000 * 60 * 40 && curT >= T) {
                            if (eat.equals("未")) {
                                if (isNotif.getIsMOut()) {//标记为true时
                                    setNotification(context, time, isNotif.getMedic_name());
                                    showTimeOutAlert(context, time, isNotif.getMedic_name(), isNotif.getMedic_phone());
                                    isNotif.setIsMOut(false);
                                    isNotif.setIsM(1);
                                }
                            } else {//服药通知
                                isNotif.setIsMOut(false);
                                isNotif.setIsM(2);
                            }
                        } else if (T <= nTime && curT <= T + 1000 * 60 * 40 && curT >= T) {
                            if (eat.equals("未")) {
                                if (isNotif.getIsNOut()) {
                                    setNotification(context, time, isNotif.getMedic_name());
                                    showTimeOutAlert(context, time, isNotif.getMedic_name(), isNotif.getMedic_phone());
                                    isNotif.setIsNOut(false);
                                    isNotif.setIsN(1);
                                }
                            } else {//服药通知
                                isNotif.setIsNOut(false);
                                isNotif.setIsN(2);
                            }
                        } else if (T <= aTime && curT <= T + 1000 * 60 * 40 && curT >= T) {
                            if (eat.equals("未")) {
                                if (isNotif.getIsAOut()) {
                                    setNotification(context, time, isNotif.getMedic_name());
                                    showTimeOutAlert(context, time, isNotif.getMedic_name(), isNotif.getMedic_phone());
                                    isNotif.setIsAOut(false);
                                    isNotif.setIsA(1);
                                }
                            } else {//服药通知
                                isNotif.setIsAOut(false);
                                isNotif.setIsA(2);
                            }
                        } else if (T <= eTime && curT <= T + 1000 * 60 * 40 && curT >= T) {
                            if (eat.equals("未")) {
                                if (isNotif.getIsEOut()) {
                                    setNotification(context, time, isNotif.getMedic_name());
                                    showTimeOutAlert(context, time, isNotif.getMedic_name(), isNotif.getMedic_phone());
                                    isNotif.setIsEOut(false);
                                    isNotif.setIsE(1);
                                }
                            } else {//服药通知
                                isNotif.setIsEOut(false);
                                isNotif.setIsE(2);
                            }
                        }
                        MyApplication.getDaoSession().getIsNotifDao().updateInTx(isNotif);
                    }
                } else {//隔天
                    MyApplication.getDaoSession().getIsNotifDao().deleteAll();
                    List<EatTime> list = MyApplication.getDaoSession().getEatTimeDao().loadAll();

                    if (list.size() > 0) {
                        for (EatTime eatTime : list) {
                            QueryBuilder<IsNotif> builder2 = MyApplication.getDaoSession().getIsNotifDao()
                                    .queryBuilder()
                                    .where(IsNotifDao.Properties.Medic_id.eq(eatTime.getMedic_id()),
                                            IsNotifDao.Properties.Today.eq(today));
                            if (builder2.buildCount().count() == 0) {
                                IsNotif isNotif2 = new IsNotif(null, today, eatTime.getMedic_id(), eatTime.getName(),
                                        eatTime.getPhone(), true, 0, true, 0, true, 0, true, 0);
                                MyApplication.getDaoSession().getIsNotifDao().insertInTx(isNotif2);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
