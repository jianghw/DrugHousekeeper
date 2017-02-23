package com.cjy.flb.activity;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.cjy.flb.R;
import com.cjy.flb.customView.OsiEditText;
import com.cjy.flb.utils.MHttpUtils;
import com.cjy.flb.utils.ToastUtil;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.socks.library.KLog;

public class RetrieveActivity extends BaseActivity implements View.OnClickListener {
    //xml组件
    private Button btnSubmit;
    //    private OsiEditText etUserName;
    private OsiEditText etEmail;

    //EditText所输入的值
    private String mUserName;
    private String mEmail;

    @Override
    public void initView() {
        setContentView(R.layout.activity_retrieve);
        initBar(getString(R.string.get_back_password), false, false);
    }

    @Override
    public void initData() {
        //        etUserName = (OsiEditText) findViewById(R.id.et_retrieve_userName);
        etEmail = (OsiEditText) findViewById(R.id.et_retrieve_email);
        btnSubmit = (Button) findViewById(R.id.btn_retrieve);
        setEditText();
    }

    @Override
    public void initListener() {
        btnSubmit.setOnClickListener(this);

    }

    /**
     * 设置自定义OsiEditext的值
     */
    private void setEditText() {
        //        etUserName.setTitle("用户名");
        etEmail.setTitle("E-mail");
    }

    @Override
    public void onClick(View v) {
        //        mUserName = etUserName.getEditText();
        mEmail = etEmail.getEditText();
        KLog.i(mEmail);
        if (TextUtils.isEmpty(mEmail)) {
            ToastUtil.showShort(getString(R.string.info_not_null));
        } else {
            RequestParams requestParams = new RequestParams();
            requestParams.addHeader("Authorization", "Basic " + MHttpUtils.REGISTER_BASIC);
            requestParams.addBodyParameter("email", mEmail);
            requestParams.addBodyParameter("user_type", "sz_flb_user");
            HttpUtils httpUtils = new HttpUtils();
            httpUtils.configCurrentHttpCacheExpiry(0);
            httpUtils.send(HttpRequest.HttpMethod.POST, MHttpUtils.RESET_PASSWORD, requestParams,
                    new RequestCallBack<String>() {
                        @Override
                        public void onSuccess(ResponseInfo<String> responseInfo) {
                            KLog.json(responseInfo.result);
                            String str = null;
                            try {
                                JsonElement jsonElement = new JsonParser().parse(responseInfo.result);
                                JsonObject jsonObject = jsonElement.getAsJsonObject();
                                JsonElement element = jsonObject.get("status");
                                str = element.getAsString();
                            } catch (JsonSyntaxException e) {
                                e.printStackTrace();
                            }
                            if (str != null && str.equals("success")) {
                                ToastUtil.showShort(getString(R.string.submit_ok_email));
                            } else {
                                ToastUtil.showShort(getString(R.string.submit_error));
                            }
                        }

                        @Override
                        public void onFailure(HttpException e, String s) {
                            KLog.d(s);
                            ToastUtil.showShort(getString(R.string.submit_error));
                        }
                    });
        }
    }
}
