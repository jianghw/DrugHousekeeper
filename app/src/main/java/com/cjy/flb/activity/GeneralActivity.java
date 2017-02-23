package com.cjy.flb.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cjy.flb.R;
import com.cjy.flb.domain.AppInfo;
import com.cjy.flb.manager.AppManager;
import com.cjy.flb.utils.MHttpUtils;
import com.cjy.flb.utils.SharedPreUtil;
import com.google.gson.Gson;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.jpush.android.api.JPushInterface;

/**
 * Created by Administrator on 2015/12/4 0004.
 */
public class GeneralActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.textView_message)
    RelativeLayout mTextViewMessage;
    @Bind(R.id.textView_language)
    RelativeLayout mTextViewLanguage;
    @Bind(R.id.textView_font)
    RelativeLayout mTextViewFont;
    @Bind(R.id.img_general_new)
    ImageView mImgGeneralNew;
    @Bind(R.id.textView_operational)
    RelativeLayout mTextViewOperational;
    @Bind(R.id.textView_updates)
    RelativeLayout mTextViewUpdates;
    @Bind(R.id.textView_problems)
    RelativeLayout mTextViewProblems;
    @Bind(R.id.textView_us)
    RelativeLayout mTextViewUs;
    @Bind(R.id.textView_exit)
    RelativeLayout mTextViewExit;

    private AlertDialog mDialog;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initView() {
        setContentView(R.layout.activity_general);
        ButterKnife.bind(this);
    }

    @Override
    public void initData() {
        initBar(getString(R.string.common_use), false, false);
        boolean isGeneral = SharedPreUtil.getBoolean("operational", false);
        if (isGeneral) {
            mImgGeneralNew.setVisibility(View.GONE);
        }
    }

    @Override
    public void initListener() {
        mTextViewMessage.setOnClickListener(this);
        mTextViewLanguage.setOnClickListener(this);
        mTextViewFont.setOnClickListener(this);
        mTextViewOperational.setOnClickListener(this);

        mTextViewUpdates.setOnClickListener(this);
        mTextViewProblems.setOnClickListener(this);
        mTextViewUs.setOnClickListener(this);
        mTextViewExit.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textView_message://消息通知
                Intent intentMessage = new Intent(context, MessageNotificationActivity.class);
                startActivity(intentMessage);
                break;
            case R.id.textView_language://语言选择
                Intent intentLanguage = new Intent(context, LanguageActivity.class);
                startActivity(intentLanguage);
                break;
            case R.id.textView_font://字体大小
                Intent intentFont = new Intent(context, FontSizeActivity.class);
                startActivity(intentFont);
                break;
            case R.id.textView_operational://使用指南
                SharedPreUtil.setBoolean("operational", true);
                Intent intentOperational = new Intent(this, GuideActivity.class);
                startActivity(intentOperational);
                mImgGeneralNew.setVisibility(View.GONE);
                break;
            case R.id.textView_updates://检测更新
                udpateApp();
                break;
            case R.id.textView_problems://问题与意见反馈
                Intent intentProblem = new Intent(context, ProblemActivity.class);
                startActivity(intentProblem);
                break;
            case R.id.textView_us://关于我们
                Intent intentUS = new Intent(context, AboutUsActivity.class);
                startActivity(intentUS);
                break;
            case R.id.textView_exit://退出登陆
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(getString(R.string.make_sure_exit))
                        .setNegativeButton(getString(R.string.btn_cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                builder.create().dismiss();
                            }
                        })
                        .setPositiveButton(getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                (new AsyncTask<Void, Void, Boolean>() {
                                    @Override
                                    protected Boolean doInBackground(Void... params) {
                                        try {
                                            MyApplication.getDaoSession().getIsNotifDao().deleteAll();
                                            MyApplication.getDaoSession().getEatTimeDao().deleteAll();
                                            MyApplication.getDaoSession().getRepeatWeekDao().deleteAll();
                                            MyApplication.getDaoSession().getSetMedicTimeDao().deleteAll();

                                            SharedPreUtil.setString("access_token", "");
                                            SharedPreUtil.setString("refresh_token", "");
                                            SharedPreUtil.setInteger("expires_in", 604801);
                                            SharedPreUtil.setString("login_name", "");

                                            SharedPreUtil.setBoolean("general", true);
                                            SharedPreUtil.setBoolean("operational", true);

                                            MyApplication.list.clear();
                                            MyApplication.hashMap.clear();

                                            MyApplication.head = null;
                                            MyApplication.Email = null;
                                            return true;
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            return null;
                                        }
                                    }

                                    @Override
                                    protected void onPostExecute(Boolean flag) {
                                        try {
                                            if (flag) {
                                                JPushInterface.stopPush(MyApplication.getInstance());

                                                MainActivity.currentPosition = 0;
                                                AppManager.getAppManager().finishAllActivity();
                                                startActivity(new Intent(GeneralActivity.this, SplashActivity.class));
                                            } else {

                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }).execute();
                            }

                        }).show();
                break;
        }
    }

    private void udpateApp() {
        View view = View.inflate(context, R.layout.update_alert_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);
        mDialog = builder.create();
        mDialog.show();
        mDialog.setCanceledOnTouchOutside(false);

        textView = (TextView) view.findViewById(R.id.tv_update_dialog);
        int versionCode = MHttpUtils.getVersionCode(context);
        getAppInfo(textView, versionCode);
    }

    /**
     * 获取app信息
     *
     * @param textView
     * @param versionCode
     */
    public void getAppInfo(final TextView textView, final int versionCode) {
        com.lidroid.xutils.HttpUtils http = new com.lidroid.xutils.HttpUtils();
        RequestParams params = new RequestParams();
        http.send(HttpRequest.HttpMethod.GET, MHttpUtils.url, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String dataString = responseInfo.result.substring(12, responseInfo.result.length() - 1);
                AppInfo appInfo = new Gson().fromJson(dataString, AppInfo.class);
                //当服务器端版本号大于本地时提示更新
                if (appInfo.getVersion() > versionCode) {
                    updateApp(context, appInfo);
                } else {
                    new Thread() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    textView.setText(getString(R.string.latest_version));
                                }
                            });
                            try {
                                Thread.sleep(2000);
                                mDialog.dismiss();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                }
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                textView.setText(getString(R.string.connection_error));
                mDialog.dismiss();
            }
        });
    }

    /**
     * 更新app
     *
     * @param context
     * @param appInfo
     */
    public void updateApp(final Context context, final AppInfo appInfo) {
        final String path = Environment.getExternalStorageDirectory() + "/" + appInfo.getFile_name();
        com.lidroid.xutils.HttpUtils httpUtils = new com.lidroid.xutils.HttpUtils();
        httpUtils.download(appInfo.getUrl(), path, new RequestCallBack<File>() {
            @Override
            public void onLoading(long total, final long current, boolean isUploading) {
                // TODO Auto-generated method stub
                super.onLoading(total, current, isUploading);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textView.setText(getString(R.string.downloading) + current * 100 / appInfo.getSize() + "%");
                    }
                });
            }

            @Override
            public void onFailure(HttpException arg0, String arg1) {
                // TODO Auto-generated method stub
                mDialog.dismiss();
            }

            @Override
            public void onSuccess(ResponseInfo<File> arg0) {
                // TODO Auto-generated method stub
                install(context, path);
                mDialog.dismiss();
            }
        });
    }

    /**
     * 安装
     *
     * @param context 接收外部传进来的context
     */
    private static void install(Context context, String path) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(path)),
                "application/vnd.android.package-archive");
        context.startActivity(intent);
    }
}
