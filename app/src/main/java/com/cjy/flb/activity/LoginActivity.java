package com.cjy.flb.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cjy.flb.R;
import com.cjy.flb.bean.QQLoginBean;
import com.cjy.flb.customView.OsiEditText;
import com.cjy.flb.utils.ConvertUtils;
import com.cjy.flb.utils.MHttpUtils;
import com.cjy.flb.utils.SharedPreUtil;
import com.cjy.flb.utils.ToastUtil;
import com.socks.library.KLog;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import java.lang.ref.WeakReference;

import cn.jpush.android.api.JPushInterface;

public class LoginActivity extends BaseActivity implements View.OnClickListener {
    //xml组件
    private Button btnLogin;
    private TextView tvForget;
    private TextView tvRegist;
    private OsiEditText etUserName;
    private OsiEditText etPassword;

    //EditText所输入的值
    private String userName;
    private String password;
    private boolean isShow = false;
    //    private ImageButton btnQQLogin;
    //    private ImageButton btnWeChatLogin;

    private Tencent mTencent;
    public static final String QQ_ID = "1105155828";

    //注册微信
    private IWXAPI api;
    public final static String WE_CHAT_ID = "wx60a39610a25e1188";
    public final static String APP_SECRET = "0a9d5fffeef38bfca91ec5d1ad92473a";


    /**
     * 初始化腾讯的第三方登陆(QQ,微信)
     */
    private void initTencent() {
        mTencent = Tencent.createInstance(QQ_ID, this.getApplicationContext());

        api = WXAPIFactory.createWXAPI(this, WE_CHAT_ID, true);
        api.registerApp(WE_CHAT_ID);
    }

    @Override
    public void initView() {
        setContentView(R.layout.activity_login);

        //        this.btnWeChatLogin = (ImageButton) findViewById(R.id.btnWeChatLogin);
        //        this.btnQQLogin = (ImageButton) findViewById(R.id.btnQQLogin);
        initBar(getString(R.string.login), false, false);

        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            isShow = bundle.getBoolean("show_token", false);
        }
    }

    @Override
    public void initData() {
        btnLogin = (Button) findViewById(R.id.btn_login);
        tvForget = (TextView) findViewById(R.id.tv_login_forget);
        tvRegist = (TextView) findViewById(R.id.tv_login_regist);
        etUserName = (OsiEditText) findViewById(R.id.et_userName);
        etPassword = (OsiEditText) findViewById(R.id.et_password);

        setEditText();
        initTencent();

        if (isShow) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            builder.setMessage(getString(R.string.login_no_long))
                    .setPositiveButton(getString(R.string.i_know_it), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            builder.create().dismiss();
                            isShow = false;
                        }
                    }).show();
        }
    }

    @Override
    public void initListener() {
        tvForget.setOnClickListener(this);
        tvRegist.setOnClickListener(this);
        btnLogin.setOnClickListener(this);

      /*  btnQQLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                qqLogin();
            }
        });
        btnWeChatLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                weChatLogin();
            }
        });*/
    }

    /**
     * qq登陆
     */
    private void qqLogin() {
        if (!mTencent.isSessionValid()) {
            mTencent.login(this, "all", new IUiListener() {
                /**
                 * 成功
                 * @param o
                 */
                @Override
                public void onComplete(Object o) {
                    QQLoginBean qqLoginBean = ConvertUtils.obj2Bean(o, QQLoginBean.class);
                    if (qqLoginBean != null) {
                        KLog.i(qqLoginBean.toString());
                    }
                }

                /**
                 * 失败
                 * @param uiError
                 */
                @Override
                public void onError(UiError uiError) {

                }

                /**
                 * 取消
                 */
                @Override
                public void onCancel() {

                }
            });
        }
    }

    /**
     * 微信登陆
     */
    private void weChatLogin() {
        // send oauth request
        final SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "wechat_sdk_demo_test";
        //        T.showShort(this, api.sendReq(req) + "");
    }

    /**
     * 设置自定义OsiEditext的值
     */
    private void setEditText() {
        etUserName.setTitle(getString(R.string.user_name));
        etPassword.setTitle(getString(R.string.password));
        etPassword.initTail();

        etUserName.setContentHint(getString(R.string.confirm_pone));
    }

    private final LoginHandler handler = new LoginHandler(this);

    static class LoginHandler extends Handler {
        WeakReference<Activity> weakReference;

        public LoginHandler(Activity activity) {
            weakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            LoginActivity activity = (LoginActivity) weakReference.get();
            if (activity != null) {
                switch (msg.what) {
                    case 100:
                        activity.onRetryLogin();
                        break;
                    case 200:
                        activity.onLoginSucced();
                        break;
                    default:
                        break;
                }
            }
            super.handleMessage(msg);
        }
    }

    private void onRetryLogin() {
        ToastUtil.showShort(getString(R.string.user_name_error));
        btnLogin.setEnabled(true);
        btnLogin.setBackgroundResource(R.color.btn_green_normal);
        btnLogin.setText(getString(R.string.longin_retry));
    }

    private void onLoginSucced() {
        btnLogin.setEnabled(true);
        btnLogin.setBackgroundResource(R.color.btn_green_normal);
        btnLogin.setText(getString(R.string.longin_succeed));
        JPushInterface.resumePush(MyApplication.getInstance());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onClick(View v) {
        userName = etUserName.getEditText();
        password = etPassword.getEditText();
        switch (v.getId()) {
            case R.id.tv_login_forget:
                toActivity(RetrievePhoneActivity.class);
                break;

            case R.id.tv_login_regist:
                toActivity(PhoneRegistActivity.class);
                break;

            case R.id.btn_login:

                if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(password)) {
                    ToastUtil.showShort(getString(R.string.info_not_null));
                } else {
                    if (MHttpUtils.isNetworkAvailable(context)) {
                        MHttpUtils.initLoginApp(userName, password, context, handler);
                        SharedPreUtil.setString("login_name", etUserName.getEditText());

                        btnLogin.setBackgroundResource(R.color.btn_green_pressed);
                        btnLogin.setText(getString(R.string.logining));
                        btnLogin.setEnabled(false);
                    } else {
                        ToastUtil.showShort(getString(R.string.net_no_connected));
                    }
                }
                break;
        }
    }

    /**
     * 性能差的手机,可能该页面会被kill掉,qq登陆的回调需要该页面
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Tencent.onActivityResultData(requestCode, resultCode, data, new IUiListener() {
            @Override
            public void onComplete(Object o) {

            }

            @Override
            public void onError(UiError uiError) {

            }

            @Override
            public void onCancel() {

            }
        });
        //        if (requestCode == Constants.REQUEST_LOGIN){
        //            Tencent.onActivityResultData(requestCode,resultCode,data,loginListener);
        //        }

    }
}
