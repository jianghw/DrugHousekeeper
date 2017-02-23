package com.cjy.flb.activity;

import android.app.Activity;
import android.content.Intent;
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

import java.lang.ref.WeakReference;

public class RegistActivity extends BaseActivity implements View.OnClickListener {

    //xml组件
    private OsiEditText etUserName;
    private OsiEditText etPassword;
    private OsiEditText etRePassword;
    private OsiEditText etEmail;
    private TextView tvAlready;
    private Button btnRegist;

    //EditText输入的值
    private String mUserName;
    private String mPassword;
    private String mRePassword;
    private String mEmail;


private final  RegistHandler mHandler=new RegistHandler(this);
    static class RegistHandler extends Handler {

        WeakReference<Activity> weakReference;

        public RegistHandler(Activity activity)
        {
            weakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            RegistActivity activity = (RegistActivity) weakReference.get();
            if (activity != null) {
                if (msg.what == 1) {
                    //注册成功
                    activity.resgistSuccess();
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

    private void resgistSuccess()
    {
        btnRegist.setText(getString(R.string.regist_success));
        btnRegist.setEnabled(true);
        toActivity(LoginActivity.class);
        finish();
    }

    private void registerAgain() {
        btnRegist.setText(getString(R.string.add_box_retry));
        btnRegist.setEnabled(true);
    }

    private TextView tvPrompt;

    @Override
    public void initView() {
        setContentView(R.layout.activity_regist);
        initBar(getString(R.string.user_registered), false, false);
    }

    @Override
    public void initData() {
        etUserName = (OsiEditText) findViewById(R.id.et_regist_userName);
        etPassword = (OsiEditText) findViewById(R.id.et_regist_password);
        etRePassword = (OsiEditText) findViewById(R.id.et_regist_rePassword);
        etEmail = (OsiEditText) findViewById(R.id.et_regist_email);
        tvAlready = (TextView) findViewById(R.id.tv_regist_already);
        tvPrompt = (TextView) findViewById(R.id.tv_prompt);
        btnRegist = (Button) findViewById(R.id.btn_regist);
        setEditText();

        SpannableString sp = new SpannableString(getString(R.string.regist_prompt));
        sp.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.et_hint)), 0, 18, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        sp.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.tv_bottom_unregist)), 18, 32, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        tvPrompt.setText(sp);
    }

    @Override
    public void initListener() {
        tvAlready.setOnClickListener(this);
        btnRegist.setOnClickListener(this);
        tvPrompt.setOnClickListener(this);
    }

    /**
     * 设置自定义OsiEditext的值
     */
    private void setEditText() {
        etUserName.setTitle(getString(R.string.user_name));
        etPassword.setTitle(getString(R.string.password));
        etRePassword.setTitle(getString(R.string.confirm_password));
        etEmail.setTitle("E-mail");
        etUserName.setContentHint(getString(R.string.regist_ac_support));
        etPassword.setContentHint(getString(R.string.regist_ac_support_2));
        etRePassword.setContentHint("");
        etEmail.setContentHint(getString(R.string.regist_ac_email));
        etPassword.initTail();
        etRePassword.initTail();
    }

    @Override
    public void onClick(View v) {
        mUserName = etUserName.getEditText();
        mPassword = etPassword.getEditText();
        mRePassword = etRePassword.getEditText();
        mEmail = etEmail.getEditText();

        switch (v.getId()) {
            case R.id.tv_regist_already:
                toActivity(LoginActivity.class);
                break;
            case R.id.btn_regist:
                if (TextUtils.isEmpty(mUserName) || TextUtils.isEmpty(mPassword) ||
                        TextUtils.isEmpty(mRePassword) || TextUtils.isEmpty(mEmail)) {
                    ToastUtil.showShort(getString(R.string.read_complete_info));
                } else if (!mPassword.equals(mRePassword)) {
                    ToastUtil.showShort(getString(R.string.two_password_consistent));
                } else {
                    if(MHttpUtils.isNetworkAvailable(context)) {
                        MHttpUtils.registUser(mUserName, mPassword, mRePassword, mEmail, mHandler);

                        btnRegist.setText(getString(R.string.regist_loading));
                        btnRegist.setEnabled(false);
                    }else{
                        ToastUtil.showShort(getString(R.string.net_no_connected));
                    }
                }
                break;
            case R.id.tv_prompt:
                startActivity(new Intent(RegistActivity.this, DisclaimerActivity.class));
                break;
            default:break;
        }
    }
}
