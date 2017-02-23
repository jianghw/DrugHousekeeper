package com.cjy.flb.activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cjy.flb.R;
import com.cjy.flb.customView.OsiEditTextButton;
import com.cjy.flb.utils.MHttpUtils;
import com.cjy.flb.utils.SmsHandler;
import com.cjy.flb.utils.SmsObserver;
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

import org.json.JSONObject;

import java.lang.ref.WeakReference;

/**
 * @author lvzhongyi
 *         <p>
 *         description 手机号码注册账号界面
 *         date 16/2/27
 *         email lvzhongyiforchrome@gmail.com
 *         </p>
 */
public class PhoneRegistActivity2 extends BaseActivity implements View.OnClickListener {

    /**
     * xml组件
     */
    private TextView tvPhoneNumber;
    private OsiEditTextButton etRegistAuthCode;
    private TextView tvPrompt;
    private Button btnNext;
    private String phoneNumber = "";

    private final PhoneRegHandler2 mHandler = new PhoneRegHandler2(this);

    static class PhoneRegHandler2 extends Handler {
        WeakReference<Activity> weakReference;

        public PhoneRegHandler2(Activity activity) {
            weakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            PhoneRegistActivity2 activity = (PhoneRegistActivity2) weakReference.get();
            if (activity != null) {
                if (msg.what == 1) {
                    activity.phoneRegSuccess();
                } else if (msg.what == 2) {
                    ToastUtil.showShort(activity.getString(R.string.regist_fail_code_error));
                    activity.registerAgain();
                } else if (msg.what == 3) {
                    ToastUtil.showShort(activity.getString(R.string.regist_fail_code_overdue));
                    activity.registerAgain();
                } else if (msg.what == 4) {
                    ToastUtil.showShort(activity.getString(R.string.regist_fail_phone_overdue));
                    activity.registerAgain();
                } else if (msg.what == 5) {
                    ToastUtil.showShort(activity.getString(R.string.regist_code_success));
                } else if (msg.what == 6) {
                    ToastUtil.showShort(activity.getString(R.string.regist_code_retry));
                } else {
                    ToastUtil.showShort(activity.getString(R.string.regist_fail_error));
                    activity.registerAgain();
                }
            }
        }
    }

    private void phoneRegSuccess() {
        btnNext.setBackgroundResource(R.color.btn_green_normal);
        btnNext.setText(getString(R.string.regist_success));
        btnNext.setEnabled(true);
        toActivity(LoginActivity.class);
        finish();
    }

    private void registerAgain() {
        btnNext.setBackgroundResource(R.color.btn_green_normal);
        btnNext.setText(getString(R.string.next));
        btnNext.setEnabled(true);
    }


    @Override
    public void initView() {
        setContentView(R.layout.activity_regist_phone2);
        this.btnNext = (Button) findViewById(R.id.btnNext);
        this.tvPrompt = (TextView) findViewById(R.id.tvPrompt);
        this.etRegistAuthCode = (OsiEditTextButton) findViewById(R.id.et_regist_auth_code);
        this.tvPhoneNumber = (TextView) findViewById(R.id.tvPhoneNumber);
        initBar(getString(R.string.phone_registered), false, false);
    }

    private ContentResolver resolver;
    private SmsObserver mObserver;

    //设置code
    public void setEtContent(String code) {
        etRegistAuthCode.setEtContent(code);
    }

    //注册监听者
    private void RegisterSmsMonitor() {
        resolver = getContentResolver();
        mObserver = new SmsObserver(this, resolver, new SmsHandler(this, 1));
        resolver.registerContentObserver(Uri.parse("context://sms"), true, mObserver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        resolver.unregisterContentObserver(mObserver);
    }

    @Override
    public void initData() {
        RegisterSmsMonitor();
        setEditText();

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            phoneNumber = bundle.getString("phoneNumber", "");
            if (!phoneNumber.equals("")) {
                tvPhoneNumber.setText(phoneNumber);
                etRegistAuthCode.clock();
            }
        }

        bindEvent();
    }

    private void bindEvent() {
        etRegistAuthCode.setBtnClockListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getAuthCode();
            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onNext();
            }
        });
    }

    /**
     * 点击下一步
     */
    private void onNext() {
        String phone = tvPhoneNumber.getText().toString().trim();
        if (phone.equals("")) {
            ToastUtil.showShort(getString(R.string.account_information_lost));
            return;
        }
        String authCode = etRegistAuthCode.getEditText();
        if (authCode == null || authCode.length() != 4) {
            ToastUtil.showShort(getString(R.string.incorrect_verify_code));
            return;
        }
        btnNext.setEnabled(false);
        final Message msg = mHandler.obtainMessage();
        RequestParams requestParams = getRequestParams(phone, authCode);
        HttpUtils httpUtils = new HttpUtils();
        httpUtils.send(HttpRequest.HttpMethod.POST, MHttpUtils.PHONE_ACTIVATE, requestParams, new
                RequestCallBack<String>() {

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        KLog.json(responseInfo.result);
                        //服务器数据返回的长度,个数不一致,gson无解,只能手动
                        try {
                            JsonElement json = new JsonParser().parse(responseInfo.result);
                            JsonObject jsonObject = json.getAsJsonObject();
                            JsonElement jsonElement = jsonObject.get("response");
                            JsonObject object = jsonElement.getAsJsonObject();
                            JsonElement element = object.get("code");
                            if (element.getAsInt() == 1) {
                                msg.what = 1;
                            } else {
                                JsonElement element2 = object.get("failure_index");
                                msg.what = 0;
                                if (element2.getAsString().equals("code is already active")) {
                                    msg.what = 1;
                                }
                            }
                        } catch (Exception e) {
                            msg.what = 0;
                        }
                        mHandler.sendMessage(msg);
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        msg.what = 0;
                        mHandler.sendMessage(msg);
                        KLog.i(s);
                    }
                });
    }

    /**
     * get request params
     *
     * @return rqeust params
     */
    private RequestParams getRequestParams(String mPhone, String mAuthCode) {
        RequestParams requestParams = new RequestParams();
        requestParams.addHeader("Authorization", "Basic " + MHttpUtils.PHONE_BASIC);
        requestParams.addBodyParameter("phone_number", mPhone);
        requestParams.addBodyParameter("active_code", mAuthCode);
        return requestParams;
    }

    /**
     * get request params
     *
     * @return rqeust params
     */
    private RequestParams getRequestParams(String mPhone) {
        RequestParams requestParams = new RequestParams();
        requestParams.addHeader("Authorization", "Basic " + MHttpUtils.PHONE_BASIC);
        requestParams.addBodyParameter("phone_number", mPhone);
        return requestParams;
    }

    /**
     * 得到手机验证码
     */
    public void getAuthCode() {
        String phone = tvPhoneNumber.getText().toString().trim();
        etRegistAuthCode.clock();
        final Message msg = mHandler.obtainMessage();
        RequestParams requestParams = getRequestParams(phone);
        HttpUtils httpUtils = new HttpUtils();
        httpUtils.send(HttpRequest.HttpMethod.POST, MHttpUtils.RE_GET_AUTH_CODE, requestParams,
                new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        String result = responseInfo.result;
                        //服务器数据返回的长度,个数不一致,gson无解,只能手动
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            int code = jsonObject.getInt("code");
                            if (code == 1) {
                                msg.what = 5;
                            } else {
                                msg.what = 6;
                            }
                        } catch (Exception e) {
                            msg.what = 6;
                        }
                        mHandler.sendMessage(msg);
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        msg.what = 6;
                        mHandler.sendMessage(msg);
                    }
                });

    }

    @Override
    public void initListener() {
        tvPrompt.setOnClickListener(this);
    }

    /**
     * 设置自定义OsiEditext的值
     */
    private void setEditText() {
        etRegistAuthCode.setTitle(getString(R.string.auth_code));
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.tv_regist_email:
                toActivity(RegistActivity.class);
                break;
            case R.id.tv_regist_already:
                toActivity(LoginActivity.class);
                break;
            case R.id.tv_prompt:
                startActivity(new Intent(PhoneRegistActivity2.this, DisclaimerActivity.class));
                break;
        }
    }
}
