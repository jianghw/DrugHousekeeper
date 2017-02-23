package com.cjy.flb.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cjy.flb.R;
import com.cjy.flb.manager.AppManager;
import com.cjy.flb.utils.MHttpUtils;
import com.cjy.flb.utils.SharedPreUtil;
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

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2015/12/4 0004.
 */
public class AccountActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.tv_userName)
    TextView mTvUserName;
    @Bind(R.id.tv_email)
    TextView mTvEmail;
    @Bind(R.id.btn_ok_setf)
    Button mBtnOkSetf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initView() {
        setContentView(R.layout.activity_account);
        ButterKnife.bind(this);
    }

    @Override
    public void initData() {
        initBar(getString(R.string.account), false, false);
        String str = SharedPreUtil.getString("login_name");
        mTvUserName.setText(str);

        RequestParams requestParams = new RequestParams();
        String accessToken = SharedPreUtil.getString("access_token");
        requestParams.addHeader("Authorization", "Bearer " + accessToken);
        HttpUtils httpUtils = new HttpUtils();
        httpUtils.configCurrentHttpCacheExpiry(1000);
        httpUtils.send(HttpRequest.HttpMethod.GET, MHttpUtils.FIND_EMAIL, requestParams,
                new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        KLog.json(responseInfo.result);
                        JsonElement jsonElement = new JsonParser().parse(responseInfo.result);
                        try {
                            if (jsonElement.isJsonObject()) {
                                JsonObject jsonObject = jsonElement.getAsJsonObject();
                                JsonElement response = jsonObject.get("response");
                                if (jsonObject.isJsonObject()) {
                                    JsonObject jObject = response.getAsJsonObject();
                                    JsonElement code = jObject.get("email");
                                    mTvEmail.setText(code.getAsString());
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

    @Override
    public void initListener() {
        mBtnOkSetf.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ok_setf:
                MyApplication.getDaoSession().getIsNotifDao().deleteAll();

                SharedPreUtil.setString("access_token", "");
                SharedPreUtil.setString("refresh_token", "");
                SharedPreUtil.setInteger("expires_in", 604801);
                SharedPreUtil.setString("login_name", "");

                MainActivity.currentPosition = 0;
                AppManager.getAppManager().finishAllActivity();
                startActivity(new Intent(this, SplashActivity.class));
                break;
            default:
                break;
        }
    }
}
