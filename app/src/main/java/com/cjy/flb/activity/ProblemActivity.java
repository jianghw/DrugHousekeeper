package com.cjy.flb.activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.cjy.flb.R;
import com.cjy.flb.manager.AppManager;
import com.cjy.flb.utils.MHttpUtils;
import com.cjy.flb.utils.SharedPreUtil;
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

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2015/12/7 0007.
 */
public class ProblemActivity extends BaseActivity implements View.OnClickListener {


    @Bind(R.id.edit_content)
    EditText mEditContent;
    @Bind(R.id.lv_edit)
    LinearLayout mLvEdit;
    @Bind(R.id.edit_medicine_company)
    EditText mEditMedicineCompany;
    @Bind(R.id.btn_ok_setf)
    Button mBtnOkSetf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initView() {
        setContentView(R.layout.activity_problem);
        ButterKnife.bind(this);
    }

    @Override
    public void initData() {
        initBar(getString(R.string.procedure_feedback), false, false);
    }

    @Override
    public void initListener() {
        mBtnOkSetf.setOnClickListener(this);
        mLvEdit.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lv_edit:
                mEditContent.setFocusable(true);
                mEditContent.setFocusableInTouchMode(true);
                mEditContent.requestFocus();
                mEditContent.requestFocusFromTouch();
                ((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).showSoftInput(mEditContent, 0);
                break;
            case R.id.btn_ok_setf:

                if (!TextUtils.isEmpty(mEditContent.getText().toString().trim()) &&
                        !TextUtils.isEmpty(mEditMedicineCompany.getText().toString().trim())) {
                    RequestParams requestParams = new RequestParams();
                    String accessToken = SharedPreUtil.getString("access_token");
                    requestParams.addHeader("Authorization", "Bearer " + accessToken);
                    requestParams.addBodyParameter("content", mEditContent.getText().toString().trim());
                    requestParams.addBodyParameter("contact_information", mEditMedicineCompany.getText().toString().trim());
                    HttpUtils httpUtils = new HttpUtils();
                    httpUtils.configCurrentHttpCacheExpiry(0);
                    httpUtils.send(HttpRequest.HttpMethod.POST, MHttpUtils.DIEVE_FEEDBACK, requestParams,
                            new RequestCallBack<String>() {
                                @Override
                                public void onSuccess(ResponseInfo<String> responseInfo) {
                                    KLog.json(responseInfo.result);
                                    int code = 0;
                                    try {
                                        JsonElement json = new JsonParser().parse(responseInfo.result);
                                        JsonObject jsonObject = json.getAsJsonObject();
                                        JsonElement jsonElement = jsonObject.get("response");
                                        JsonObject object = jsonElement.getAsJsonObject();
                                        JsonElement codeElement = object.get("code");
                                        code = codeElement.getAsInt();
                                    } catch (JsonSyntaxException e) {
                                        e.printStackTrace();
                                    }
                                    if (code == 1) {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(ProblemActivity.this);
                                        builder.setMessage(getString(R.string.submit_ok));
                                        final AlertDialog mDialog = builder.create();
                                        mDialog.show();
                                        mDialog.setCanceledOnTouchOutside(false);
                                        new Thread(){
                                            @Override
                                            public void run() {
                                                try {
                                                    Thread.sleep(1500);
                                                    mDialog.dismiss();
                                                    AppManager.getAppManager().finishActivity(ProblemActivity.this);
                                                } catch (InterruptedException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }.start();
                                    }else{
                                        ToastUtil.showShort(getString(R.string.submit_error));
                                    }
                                }

                                @Override
                                public void onFailure(HttpException e, String s) {
                                    KLog.d(s);
                                    AlertDialog.Builder builder = new AlertDialog.Builder(ProblemActivity.this);
                                    builder.setMessage(getString(R.string.submit_error));
                                    final AlertDialog mDialog = builder.create();
                                    mDialog.show();
                                    mDialog.setCanceledOnTouchOutside(false);
                                    new Thread(){
                                        @Override
                                        public void run() {
                                            try {
                                                Thread.sleep(1500);
                                                mDialog.dismiss();
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }.start();
                                }
                            });
                } else {
                    ToastUtil.showShort(getString(R.string.info_not_null));
                }
                break;
        }
    }


}
