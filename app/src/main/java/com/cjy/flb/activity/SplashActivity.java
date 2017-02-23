package com.cjy.flb.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cjy.flb.R;
import com.cjy.flb.domain.AppInfo;
import com.cjy.flb.manager.AppManager;
import com.cjy.flb.manager.DBManager;
import com.cjy.flb.utils.DataCleanManager;
import com.cjy.flb.utils.MHttpUtils;
import com.cjy.flb.utils.SharedPreUtil;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.socks.library.KLog;

import java.io.File;

/**
 * 欢迎界面
 */
public class SplashActivity extends BaseActivity implements View.OnClickListener {

    private RelativeLayout btnLogin;
    private int expires_in;
    private AppInfo appInfo;
    private volatile int count=0;


    @Override
    public void initView() {
        setContentView(R.layout.activity_splash);
        btnLogin = (RelativeLayout) findViewById(R.id.btn_Splash_login);
    }

    @Override
    public void initData() {
        //expires_in的最大值为604800，这里默认为604801，如果相等则不存在token,需要登录
        expires_in = SharedPreUtil.getInteger("expires_in", 604801);
        if (expires_in != 604801) {
            btnLogin.setVisibility(View.GONE);
            whetherToUpdate();
        }
    }

    /**
     * 获取app信息,判断是否需要更新
     */
    public void whetherToUpdate() {
        com.lidroid.xutils.HttpUtils http = new com.lidroid.xutils.HttpUtils();
        RequestParams params = new RequestParams();
        http.send(HttpRequest.HttpMethod.GET, MHttpUtils.url, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                try {
                    JsonElement json = new JsonParser().parse(responseInfo.result);
                    JsonElement jsonElement = null;
                    if (json.isJsonObject()) {
                        JsonObject jsonObject = json.getAsJsonObject();
                        jsonElement = jsonObject.get("response");
                    }
                    if (jsonElement != null && jsonElement.isJsonObject()) {
                        appInfo = new Gson().fromJson(jsonElement, AppInfo.class);
                        //当服务器端版本号大于本地时提示更新
                        if (appInfo.getVersion() > MHttpUtils.getVersionCode(context)) {
                            StringBuilder sb = new StringBuilder();
                            sb.append(appInfo.getName()).append(getString(R.string.update_title)).append("\r\n").append("\r\n");
                            String size = String.valueOf(appInfo.getSize() / 1000D / 1024D).substring(0, 3);
                            sb.append(getString(R.string.software_size)).append(size).append("M").append("\r\n");
                            sb.append(getString(R.string.version)).append(appInfo.getTag()).append("\r\n");
                            sb.append(getString(R.string.update_description)).append(appInfo.getDescription());
                            showWhetherToUpdate(sb.toString());
                        } else {
                            MHttpUtils.tokenLogin(context);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                KLog.d(msg+count);
                count++;
                if(count==10){
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage(context.getString(R.string.on_server));
                    AlertDialog dialog = builder.create();
                    dialog.setCancelable(false);
                    dialog.show();
                }
                if(count==15){
                    AppManager.getAppManager().AppExit(context);
                }
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1000);
                            whetherToUpdate();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        });
    }

    private void showWhetherToUpdate(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.software_update))
                .setMessage(message)
                .setPositiveButton(getString(R.string.download), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (!MHttpUtils.isWifi(context)) {
                            showNoWifiPrompt();
                        } else {
                            showUpdateApp();
                        }
                    }
                })
                .setNegativeButton(getString(R.string.exit_dialog), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        AppManager.getAppManager().AppExit(context);
                    }
                })
                .setCancelable(false)
                .show();
    }

    private void showNoWifiPrompt() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(getString(R.string.update_message))
                .setPositiveButton(getString(R.string.download), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        showUpdateApp();
                    }
                })
                .setNegativeButton(getString(R.string.exit_dialog_system), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AppManager.getAppManager().AppExit(context);
                    }
                }).show();
    }

    private void showUpdateApp() {
        View view = View.inflate(context, R.layout.update_alert_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
        TextView textView = (TextView) view.findViewById(R.id.tv_update_dialog);
        updateApp(dialog, textView);
        try {
            DataCleanManager.cleanDatabaseByName(this, "ygj.db");
            DataCleanManager.cleanDatabaseByName(this, DBManager.DB_NAME);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新下载app
     */
    public void updateApp(final AlertDialog dialog, final TextView textView) {
        final String path = Environment.getExternalStorageDirectory() + "/" + appInfo.getFile_name();
        com.lidroid.xutils.HttpUtils httpUtils = new com.lidroid.xutils.HttpUtils();
        httpUtils.download(appInfo.getUrl(), path, new RequestCallBack<File>() {
            @Override
            public void onLoading(long total, long current, boolean isUploading) {
                // TODO Auto-generated method stub
                super.onLoading(total, current, isUploading);
                textView.setText(getString(R.string.downloading) + current * 100 / appInfo.getSize() + "%");
            }

            @Override
            public void onFailure(HttpException arg0, String arg1) {
                dialog.dismiss();
            }

            @Override
            public void onSuccess(ResponseInfo<File> arg0) {
                dialog.dismiss();
                install(context, path);
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
        intent.setDataAndType(Uri.fromFile(new File(path)), "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    @Override
    public void initListener() {
        btnLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_Splash_login:
                //登陆页
                toActivity(LoginActivity.class);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
