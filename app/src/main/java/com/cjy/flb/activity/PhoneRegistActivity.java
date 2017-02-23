package com.cjy.flb.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cjy.flb.R;
import com.cjy.flb.customView.OsiEditText;
import com.cjy.flb.utils.MHttpUtils;
import com.cjy.flb.utils.ToastUtil;
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
public class PhoneRegistActivity extends BaseActivity implements View.OnClickListener {

    /**
     * xml组件
     */
    private OsiEditText etPhone;
    private OsiEditText etPassword;
    private OsiEditText etPassword2;

    private TextView tvEmailRegist;
    private TextView tvAlready;
    private Button btnRegist;

    //EditText输入的值
    private String mUserName;
    private String mPassword;
    private String mPhone;

    private final PhoneRegHandler mHandler = new PhoneRegHandler(this);

    static class PhoneRegHandler extends Handler {
        WeakReference<Activity> weakReference;

        public PhoneRegHandler(Activity activity) {
            weakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            PhoneRegistActivity activity = (PhoneRegistActivity) weakReference.get();
            if (activity != null) {
                if (msg.what == 1) {
                    //注册成功
                    activity.registSuccess();
                } else if (msg.what == 2) {
                    ToastUtil.showShort(activity.getString(R.string.regist_fail_phone_error));
                    activity.registerAgain();
                } else if (msg.what == 3) {
                    ToastUtil.showShort(activity.getString(R.string.regist_fail_only_letter));
                    activity.registerAgain();
                } else if (msg.what == 4) {
                    ToastUtil.showShort(activity.getString(R.string.regist_fail_phone_exist));
                    activity.registerAgain();
                } else if (msg.what == 5) {
                    ToastUtil.showShort(activity.getString(R.string.regist_fail_password_long));
                    activity.registerAgain();
                } else if (msg.what == 6) {
                    ToastUtil.showShort(activity.getString(R.string.regist_fail_password_error));
                    activity.registerAgain();
                } else if (msg.what == 7) {
                    ToastUtil.showShort(activity.getString(R.string.regist_fail_email_error));
                    activity.registerAgain();
                } else if (msg.what == 8) {
                    ToastUtil.showShort(activity.getString(R.string.regist_fail_error));
                    activity.registerAgain();
                } else if (msg.what == 9) {
                    ToastUtil.showShort(activity.getString(R.string.regist_fail_user_error));
                    activity.registerAgain();
                } else if (msg.what == 10) {
                    ToastUtil.showShort(activity.getString(R.string.regist_fail_email_exist));
                    activity.registerAgain();
                }
            }
        }
    }

    private void registSuccess() {
        btnRegist.setBackgroundResource(R.color.btn_green_normal);
        btnRegist.setEnabled(true);
        Intent intent = new Intent(PhoneRegistActivity.this, PhoneRegistActivity2.class);
        Bundle bundle = new Bundle();
        bundle.putString("phoneNumber", etPhone.getEditText());
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }

    private void registerAgain() {
        btnRegist.setBackgroundResource(R.color.btn_green_normal);
        btnRegist.setText(R.string.reRegister);
        btnRegist.setEnabled(true);
    }

    private TextView tvPrompt;

    @Override
    public void initView() {
        setContentView(R.layout.activity_regist_phone);
        initBar(getString(R.string.phone_registered), false, false);

        etPhone = (OsiEditText) findViewById(R.id.et_regist_phone);
        etPassword = (OsiEditText) findViewById(R.id.et_regist_password);
        etPassword2 = (OsiEditText) findViewById(R.id.et_regist_password2);

        tvEmailRegist = (TextView) findViewById(R.id.tv_regist_email);
        tvAlready = (TextView) findViewById(R.id.tv_regist_already);
        tvPrompt = (TextView) findViewById(R.id.tv_prompt);
        btnRegist = (Button) findViewById(R.id.btn_regist);

    }

    @Override
    public void initData() {
        setEditText();

        SpannableString sp = new SpannableString(getString(R.string.regist_prompt));
        sp.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.et_hint)), 0, 21,
                Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        sp.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.tv_bottom_unregist)),
                21, 31, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        tvPrompt.setText(sp);

        bindEvent();
    }

    private void bindEvent() {
        btnRegist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                phoneRegister();
            }
        });
    }

    /**
     * 注册
     */
    private void phoneRegister() {
        String phone = etPhone.getEditText();
        String pwd1 = etPassword.getEditText();
        String pwd2 = etPassword2.getEditText();

        if (phone.length() != 11) {
            ToastUtil.showShort(getString(R.string.entry_phone_ok));
        } else if (pwd1 == null || pwd1.length() < 6) {
            ToastUtil.showShort(getString(R.string.regist_ac_support_2));
        } else if (!pwd1.equals(pwd2)) {
            ToastUtil.showShort(getString(R.string.two_password_consistent));
        } else {
            btnRegist.setEnabled(false);
            RequestParams requestParams = getRequestParams(phone, pwd1);
            final Message message = Message.obtain();
            HttpUtils httpUtils = new HttpUtils();
            httpUtils.send(HttpRequest.HttpMethod.POST, MHttpUtils.PHONE_REGISTER, requestParams,
                    new RequestCallBack<String>() {
                        @Override
                        public void onSuccess(ResponseInfo<String> responseInfo) {
                            KLog.json(responseInfo.result);
                            String result = responseInfo.result;
                            //服务器数据返回的长度,个数不一致,gson无解,只能手动
                            try {
                                JSONObject jsonObject = new JSONObject(result);
                                int code = jsonObject.getInt("code");
                                if (code == 1) {
                                    message.what = 1;
                                } else {
                                    if (result.contains("login")) {
                                        if (result.contains("Phone number format error")) {
                                            message.what = 2;
                                        } else if (result.contains("Exist")) {
                                            message.what = 4;
                                        } else {
                                            message.what = 8;
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                message.what = 8;
                            }
                            mHandler.sendMessage(message);
                        }

                        @Override
                        public void onFailure(HttpException e, String s) {
                            message.what = 8;
                            mHandler.sendMessage(message);
                        }
                    });
        }
    }


    /**
     * get request params
     *
     * @return rqeust params
     */
    private RequestParams getRequestParams(String mPhone, String mPassword) {
        RequestParams requestParams = new RequestParams();
        requestParams.addHeader("Authorization", "Basic " + MHttpUtils.PHONE_BASIC);
        requestParams.addBodyParameter("login", mPhone);
        requestParams.addBodyParameter("password", mPassword);
        requestParams.addBodyParameter("password_confirmation", mPassword);
        requestParams.addBodyParameter("user_type", "sz_flb_user");
        requestParams.addBodyParameter("key_type", "phone_number");
        return requestParams;
    }


    @Override
    public void initListener() {
        tvAlready.setOnClickListener(this);
        tvPrompt.setOnClickListener(this);
        tvEmailRegist.setOnClickListener(this);
    }

    /**
     * 设置自定义OsiEditext的值
     */
    private void setEditText() {
        etPassword.setTitle(getString(R.string.password));
        etPassword2.setTitle(getString(R.string.confirm_password));
        etPhone.setTitle("+86");

        etPassword.setContentHint(getString(R.string.regist_ac_support_2));
        etPassword2.setContentHint(getString(R.string.regist_ac_support_2));
        etPhone.setContentHint(getString(R.string.regist_ac_phone));

        etPassword.initTail();
        etPassword2.initTail();

    }

    @Override
    public void onClick(View v) {
        mPassword = etPassword.getEditText();
        mPhone = etPhone.getEditText();

        switch (v.getId()) {
            case R.id.tv_regist_email:
                toActivity(RegistActivity.class);
                break;
            case R.id.tv_regist_already:
                toActivity(LoginActivity.class);
                break;
            case R.id.btn_regist:
                if (TextUtils.isEmpty(mUserName) || TextUtils.isEmpty(mPassword) || TextUtils
                        .isEmpty(mPhone)) {
                    ToastUtil.showShort(getString(R.string.info_not_null));
                } else {
                    if (MHttpUtils.isNetworkAvailable(context)) {

                        btnRegist.setBackgroundResource(R.color.btn_green_pressed);
                        btnRegist.setText(getString(R.string.regist_loading));
                        btnRegist.setEnabled(false);
                    } else {
                        ToastUtil.showShort(getString(R.string.net_no_connected));
                    }
                }
                break;
            case R.id.tv_prompt:
                startActivity(new Intent(PhoneRegistActivity.this, DisclaimerActivity.class));
                break;
            default:
                break;
        }
    }


}
