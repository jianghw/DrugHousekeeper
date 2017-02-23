package com.cjy.flb.activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cjy.flb.R;
import com.cjy.flb.customView.OsiEditText;
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

public class RetrievePhoneActivity extends BaseActivity implements View.OnClickListener {


    private OsiEditText etPhone;
    private OsiEditTextButton etRegistAuthCode;
    private OsiEditText etPassword;
    private Button btnRegist;
    private TextView tvEmail;

    private final RetriHandler mHandler = new RetriHandler(this);

    static class RetriHandler extends Handler {
        WeakReference<Activity> weakReference;

        public RetriHandler(Activity activity) {
            weakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            RetrievePhoneActivity activity = (RetrievePhoneActivity) weakReference.get();
            if (activity != null) {
                if (msg.what == 1) {
                    activity.onRetrieSuccess();
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

    private void onRetrieSuccess() {
        btnRegist.setText(getString(R.string.submit_ok));
        btnRegist.setEnabled(true);
        finish();
        toActivity(LoginActivity.class);
    }

    @Override
    public void initView() {
        setContentView(R.layout.activity_retrieve_phone);
        initBar(getString(R.string.get_back_password), false, false);

        etPhone = (OsiEditText) findViewById(R.id.et_regist_phone);
        etRegistAuthCode = (OsiEditTextButton) findViewById(R.id.et_regist_auth_code);
        etPassword = (OsiEditText) findViewById(R.id.et_regist_password);

        btnRegist = (Button) findViewById(R.id.btn_regist);
        tvEmail = (TextView) findViewById(R.id.tv_regist_email);
    }

    @Override
    public void initData() {
        etPhone.setTitle("+86");
        etRegistAuthCode.setTitle(getString(R.string.auth_code));
        etPassword.setTitle(getString(R.string.new_password));

        etPhone.setContentHint(getString(R.string.regist_ac_phone));
        etPassword.setContentHint(getString(R.string.regist_ac_support_2));
        etPassword.initTail();

        RegisterSmsMonitor();
    }

    @Override
    public void initListener() {
        btnRegist.setOnClickListener(this);
        tvEmail.setOnClickListener(this);

        etRegistAuthCode.setBtnClockListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone = etPhone.getEditText();
                if (phone.length() == 11) {
                    getAuthCode(phone);
                } else {
                    ToastUtil.showShort(getString(R.string.entry_phone_ok));
                }
            }
        });
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
        mObserver = new SmsObserver(this, resolver, new SmsHandler(this, 2));
        resolver.registerContentObserver(Uri.parse("context://sms"), true, mObserver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        resolver.unregisterContentObserver(mObserver);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_regist_email:
                toActivity(RetrieveActivity.class);
                break;
            case R.id.btn_regist:
                onNext();
                break;
        }
    }

    private void registerAgain() {
        btnRegist.setBackgroundResource(R.color.btn_Sgreen_normal);
        btnRegist.setText(getString(R.string.register));
        btnRegist.setEnabled(true);
    }

    /**
     * 得到手机验证码
     *
     * @param phone
     */
    public void getAuthCode(String phone) {
        etRegistAuthCode.clock();
        final Message msg = mHandler.obtainMessage();
        RequestParams requestParams = getRequestParams(phone);
        HttpUtils httpUtils = new HttpUtils();
        httpUtils.send(HttpRequest.HttpMethod.POST, MHttpUtils.PASSWORD_GET_AUTH_CODE, requestParams,
                new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        String result = responseInfo.result;
                        KLog.json(result);
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            JSONObject object = jsonObject.getJSONObject("response");
                            JSONObject object2 = new JSONObject(object.toString());
                            int code = object2.getInt("code");
                            if (code == 1) {
                                msg.what = 5;
                            } else {
                                JSONObject jsonObject1 = object2.getJSONObject("failure_index");
                                msg.what = 6;
                            }
                        } catch (Exception e) {
                            msg.what = 6;
                        }
                        mHandler.sendMessage(msg);
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        KLog.d(s);
                        msg.what = 6;
                        mHandler.sendMessage(msg);
                    }
                });
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
     * get request params
     *
     * @return rqeust params
     */
    private RequestParams getRequestParams(String mPhone, String mAuthCode, String password) {
        RequestParams requestParams = new RequestParams();
        requestParams.addHeader("Authorization", "Basic " + MHttpUtils.PHONE_BASIC);
        requestParams.addBodyParameter("phone_number", mPhone);
        requestParams.addBodyParameter("valid_code", mAuthCode);
        requestParams.addBodyParameter("password", password);
        requestParams.addBodyParameter("password_confirmation", password);
        requestParams.addBodyParameter("user_type", "sz_flb_user");
        requestParams.addBodyParameter("key_type", "phone_number");
        return requestParams;
    }

    /**
     * 点击下一步
     */
    private void onNext() {
        String phone = etPhone.getEditText();
        if (phone.equals("")) {
            ToastUtil.showShort(getString(R.string.entry_phone_ok));
            return;
        }
        String authCode = etRegistAuthCode.getEditText();
        if (authCode == null || authCode.length() != 4) {
            ToastUtil.showShort(getString(R.string.incorrect_verify_code));
            return;
        }
        String password = etPassword.getEditText();
        if (password == null || password.length() < 6) {
            ToastUtil.showShort(getString(R.string.regist_fail_password_long));
            return;
        }
        btnRegist.setEnabled(false);
        final Message msg = mHandler.obtainMessage();
        RequestParams requestParams = getRequestParams(phone, authCode, password);
        HttpUtils httpUtils = new HttpUtils();
        httpUtils.send(HttpRequest.HttpMethod.PUT, MHttpUtils.RE_PASSWORD_GET_AUTH_CODE,
                requestParams,
                new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        KLog.json(responseInfo.result);
                        //服务器数据返回的长度,个数不一致,gson无解,只能手动
                        try {
                            JsonElement json = new JsonParser().parse(responseInfo.result);
                            JsonObject jsonObject = json.getAsJsonObject();
                            JsonElement element = jsonObject.get("code");
                            if (element.getAsInt() == 1) {
                                msg.what = 1;
                            } else {
                                JsonElement element2 = jsonObject.get("failure_index");
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

}
