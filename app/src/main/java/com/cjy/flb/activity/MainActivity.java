package com.cjy.flb.activity;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.cjy.flb.R;
import com.cjy.flb.customView.UITableBottom;
import com.cjy.flb.fragment.FLBFragment;
import com.cjy.flb.fragment.MeFragment2;
import com.cjy.flb.fragment.NoteFragment;
import com.cjy.flb.fragment.SetFragment4;
import com.cjy.flb.utils.MHttpUtils;
import com.cjy.flb.utils.SharedPreUtil;
import com.cjy.flb.utils.ToastUtil;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.socks.library.KLog;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.jpush.android.api.JPushInterface;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.uibottom)
    UITableBottom mUibottom;
    @Bind(R.id.fragment_main_activity)
    FrameLayout mFragmentMainActivity;
    @Bind(R.id.toolbar)
    ImageView mToolbar;

    public FLBFragment flbFragment;
    public SetFragment4 setFragment;
    public NoteFragment noteFragment;
    public MeFragment2 meFragment;
    //默认显示位置
    public static int currentPosition = 0;
    /**
     * 状态栏高度
     */
    private int sbHeight;
    /**
     * actionbar 高度
     */
    private int actionBarHeight;

    /**
     * 更多
     */
    private PopupWindow popupWindow;
    private long exitTime = 0;
    private Thread timeThread;//用于循环检查在线的线程
//    public static boolean isOnline = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            onSolveOverlapByTag();
        }
    }

    protected void onSolveOverlapByTag() {
        flbFragment = (FLBFragment) getSupportFragmentManager().findFragmentByTag("0");
        setFragment = (SetFragment4) getSupportFragmentManager().findFragmentByTag("1");
        noteFragment = (NoteFragment) getSupportFragmentManager().findFragmentByTag("2");
        meFragment = (MeFragment2) getSupportFragmentManager().findFragmentByTag("3");
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        hideAllFragments(transaction);
        switch (currentPosition) {
            case 0:
                transaction.show(flbFragment);
                break;
            case 1:
                transaction.show(setFragment);
                break;
            case 2:
                transaction.show(noteFragment);
                break;
            case 3:
                transaction.show(meFragment);
                break;
            default:break;
        }
        transaction.commit();
    }

    @Override
    public void initView() {
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            currentPosition = bundle.getInt("currentPosition");
        }
        String id = JPushInterface.getRegistrationID(this);
        Log.i("id", id);

        mUibottom.setmViewPager(this, currentPosition);

        getStatusBarHeight();
        getActionBarHeight();
    }

    @Override
    public void initData() {
        MHttpUtils.getBoxUserInfo(context, "mainDay", MyApplication.flbId, false);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fragment_main_activity,
                getItem(currentPosition),
                String.valueOf(currentPosition))
                .commit();

        theAuroraPush();

        Intent i = new Intent(context, com.cjy.flb.service.AlarmClockService.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startService(i);
    }

    /**
     * 极光推送
     */
    private void theAuroraPush() {
        RequestParams requestParams = new RequestParams();
        String accessToken = SharedPreUtil.getString("access_token");
        requestParams.addHeader("Authorization", "Bearer " + accessToken);
        KLog.a(accessToken);
        requestParams.addQueryStringParameter("push_id", JPushInterface.getRegistrationID(this));
        HttpUtils httpUtils = new HttpUtils();
        httpUtils.send(HttpRequest.HttpMethod.POST, MHttpUtils.AURORA_PUSH, requestParams,
                new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        KLog.json(responseInfo.result);
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        KLog.d(s);
                    }
                });
    }

    @Override
    public void initListener() {
        mToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popUpMyOverflow();
            }
        });
    }

    public void showAlerDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.box_has_removed))
                .setNegativeButton(getString(R.string.i_know_it), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(MainActivity.this, MedicineSwitchActivity.class));
                    }
                })
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        final AlertDialog d = getAlertDialog();
        timeThread = new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        determineWhetherOnline(d);
                        sleep(1000 * 60 * 5);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        timeThread.start();
    }

    private AlertDialog getAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        return builder.setTitle(getString(R.string.important_notification))
                .setMessage(getString(R.string.box_unline))
                .setPositiveButton(getString(R.string.i_know_it), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog1, int which) {
                        dialog1.dismiss();
                    }
                })
                .setNegativeButton(getString(R.string.net_connected), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog1, int which) {
                        dialog1.dismiss();
                        if (MyApplication.flbId.startsWith("1")) {
                            Intent intent = new Intent(context, GSMActivityByMain.class);
                            startActivity(intent);
                        } else if (MyApplication.flbId.startsWith("2")) {
                            Intent intent = new Intent(context, WifiChoiceActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("from", "main");
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                    }
                })
                .create();
    }

    private void determineWhetherOnline(final AlertDialog dialog) {
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
                            onDeterIsOnline(responseInfo, dialog);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        KLog.d(s);
//                        isOnline = false;
                    }
                });
    }

    private void onDeterIsOnline(ResponseInfo<String> responseInfo, AlertDialog dialog) throws Exception {
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
//                isOnline = true;
            } else {
//                isOnline = false;
                if (dialog != null && !dialog.isShowing()) {
                    dialog.show();
                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        KLog.i("onStop()");
        if (timeThread != null) timeThread.interrupt();
    }

    @Override
    protected void onDestroy() {
        KLog.i("onDestroy()");
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                if (flbFragment == null) {
                    flbFragment = new FLBFragment();
                }
                return flbFragment;
            case 1:
                if (setFragment == null) {
                    setFragment = new SetFragment4();
                }
                return setFragment;
            case 2:
                if (noteFragment == null) {
                    noteFragment = new NoteFragment();
                }
                return noteFragment;
            case 3:
                if (meFragment == null) {
                    meFragment = new MeFragment2();
                }
                return meFragment;
            default:
                return null;
        }
    }

    /**
     * 底部栏滑动监听加载
     *
     * @param i
     * @param b
     */
    public void setCurrentItem(int i, boolean b) {
        if (i != currentPosition) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            hideAllFragments(fragmentTransaction);
            switch (i) {
                case 0:
                    if (flbFragment == null) {
                        fragmentTransaction.add(R.id.fragment_main_activity, getItem(i), "0");
                    } else {
                        fragmentTransaction.show(flbFragment);
                    }
                    break;
                case 1:
                    if (setFragment == null) {
                        fragmentTransaction.add(R.id.fragment_main_activity, getItem(i), "1");
                    } else {
                        fragmentTransaction.show(setFragment);
                    }
                    break;
                case 2:
                    if (noteFragment == null) {
                        fragmentTransaction.add(R.id.fragment_main_activity, getItem(i), "2");
                    } else {
                        fragmentTransaction.show(noteFragment);
                    }
                    break;
                case 3:
                    if (meFragment == null) {
                        fragmentTransaction.add(R.id.fragment_main_activity, getItem(i), "3");
                    } else {
                        fragmentTransaction.show(meFragment);
                    }
                    break;
                default:
                    break;
            }
            fragmentTransaction.commit();
            currentPosition = i;
        }
    }

    private void hideAllFragments(FragmentTransaction fragmentTransaction) {

        if (flbFragment != null)
            fragmentTransaction.hide(flbFragment);
        if (setFragment != null)
            fragmentTransaction.hide(setFragment);
        if (noteFragment != null)
            fragmentTransaction.hide(noteFragment);
        if (meFragment != null)
            fragmentTransaction.hide(meFragment);
    }

    private void getActionBarHeight() {
        TypedValue value = new TypedValue();
        if (getTheme().resolveAttribute(
                android.R.attr.actionBarSize, value, true)) {// 如果资源是存在的、有效的
            actionBarHeight = TypedValue.complexToDimensionPixelSize(
                    value.data, getResources().getDisplayMetrics());
            //xhdpi--56dp--112px
        }
    }

    private void getStatusBarHeight() {
        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object obj = c.newInstance();
            Field field = c.getField("status_bar_height");
            int x = Integer.parseInt(field.get(obj).toString());
            sbHeight = getResources().getDimensionPixelSize(x);
            //xhdpi--50px
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        return super.onOptionsItemSelected(item);
    }

    /**
     * 标题栏弹出自定义的popu
     */
    private void popUpMyOverflow() {
        View popuView = getLayoutInflater().inflate(R.layout.activity_actionbar_popu, null);
        popupWindow = new PopupWindow(popuView, LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, true);
        LinearLayout addLy = (LinearLayout) popuView.findViewById(R.id.ly_add_flb);
        LinearLayout chooseLy = (LinearLayout) popuView.findViewById(R.id.ly_choose_flb);
        LinearLayout callLy = (LinearLayout) popuView.findViewById(R.id.ly_call_flb);
        LinearLayout alterLy = (LinearLayout) popuView.findViewById(R.id.ly_alter_flb);
        addLy.setOnClickListener(this);
        chooseLy.setOnClickListener(this);
        callLy.setOnClickListener(this);
        alterLy.setOnClickListener(this);
        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.toolbar_bg_popu));
        popupWindow.setOutsideTouchable(true);

        popupWindow.showAtLocation(mToolbar, Gravity.RIGHT | Gravity.TOP, Dp2Px(this, 8f), sbHeight + actionBarHeight
                - 25);
    }

    public int Dp2Px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ly_add_flb:
                startActivity(new Intent(this, PCSOfNetworkTypeActivity.class));
                if (popupWindow != null) {
                    popupWindow.dismiss();
                }
                break;
            case R.id.ly_choose_flb:
                startActivity(new Intent(this, MedicineSwitchActivity.class));
                if (popupWindow != null) {
                    popupWindow.dismiss();
                }
                break;
            case R.id.ly_call_flb:
                startActivity(new Intent(this, DistressCallActivity.class));
                if (popupWindow != null) {
                    popupWindow.dismiss();
                }
                break;
            case R.id.ly_alter_flb:
                startActivity(new Intent(this, ModifyCompleteActivity.class));
                if (popupWindow != null) {
                    popupWindow.dismiss();
                }
                break;
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                ToastUtil.showShort(getString(R.string.entry_again_to_exit));
                exitTime = System.currentTimeMillis();
            } else {
                //                AppManager.getAppManager().AppExit(context);
                return super.onKeyDown(keyCode, event);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        //实现Home键效果
        //super.onBackPressed();这句话一定要注掉,不然又去调用默认的back处理方式了
        Intent i = new Intent(Intent.ACTION_MAIN);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addCategory(Intent.CATEGORY_HOME);
        startActivity(i);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
    }
}
