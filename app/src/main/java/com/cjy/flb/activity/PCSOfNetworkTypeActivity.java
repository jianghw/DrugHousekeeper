package com.cjy.flb.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.cjy.flb.R;
import com.cjy.flb.customView.OsiEditText;
import com.cjy.flb.domain.Flb;
import com.cjy.flb.utils.MHttpUtils;
import com.cjy.flb.utils.SharedPreUtil;
import com.cjy.flb.utils.ToastUtil;
import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.socks.library.KLog;

/**
 * 显示wifi信息页面
 */
public class PCSOfNetworkTypeActivity extends BaseActivity implements View.OnClickListener {

    private OsiEditText etId;
    private OsiEditText etSn;
    private Button nextBtn;

    @Override
    public void initView() {
        setContentView(R.layout.activity_box_net_type);
        initBar(getString(R.string.add_medicine_box), true, false);

        etId = (OsiEditText) findViewById(R.id.et_addFlb_ID);
        etSn = (OsiEditText) findViewById(R.id.et_addFlb_SN);
        nextBtn = (Button) findViewById(R.id.btn_verity_next);
    }

    @Override
    public void initData() {
        etId.setTitle("ID");
        etSn.setTitle("SN");
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void initListener() {
        nextBtn.setOnClickListener(this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_verity_next:
                String mId = etId.getEditText();
                String mSn = etSn.getEditText();
                if (MHttpUtils.isNetworkAvailable(context)) {
                    if (TextUtils.isEmpty(mId) || TextUtils.isEmpty(mSn)) {
                        ToastUtil.showShort(getString(R.string.make_sure_id_ok));
                    } else {
                        addFlb(mId, mSn, context);
                    }
                } else {
                    ToastUtil.showShort(getString(R.string.net_no_connected));
                }
                break;
            default:
                break;
        }
    }

    private void addFlb(final String id, final String sn, final Context context) {
        RequestParams params = new RequestParams();
        String accessToken = SharedPreUtil.getString("access_token");
        params.addHeader("Authorization", "Bearer " + accessToken);
        params.addBodyParameter("device_uid", id);
        params.addBodyParameter("device_sn", sn);
        HttpUtils httpUtils = new HttpUtils();
        httpUtils.send(HttpRequest.HttpMethod.POST, MHttpUtils.addFlbUrl, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                KLog.json(responseInfo.result);
                Flb flb = new Gson().fromJson(responseInfo.result, Flb.class);
                int code = flb.getResponse().getCode();
                if (code == 1) {
                    SharedPreUtil.setString("FlbID", id);
                    SharedPreUtil.setString("FlbSN", sn);
                    if (id.startsWith("1")) {
                        startActivity(new Intent(context, GSMActivity.class));
                    } else if (id.startsWith("2")) {
                        Intent intent = new Intent(context, WifiChoiceActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("from", "no");
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                } else {
                    KLog.i("第一步绑定失败");
                    String str = flb.getResponse().getFailure_index();
                    if (str.equals("Is binding")) {
                        ToastUtil.showShort(getString(R.string.has_been_bound));
                        MHttpUtils.getBoxUserInfo(context, "noDay", id, false);
                    } else if (str.equals("The uid code or sn code is not correct")) {
                        ToastUtil.showShort(getString(R.string.code_not_matched));
                    }
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                ToastUtil.showShort(getString(R.string.submit_error));
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //处理扫描结果（在界面上显示）
        if (resultCode == RESULT_OK && data != null) {
            Bundle bundle = data.getExtras();
            String scanResult = bundle.getString("result", "ID:10000101,SN:123456");
            if (!TextUtils.isEmpty(scanResult) && scanResult.contains("ID")) {
                String[] str = scanResult.split(",");
                etId.setEtContent(str[0].split(":")[1]);
                etSn.setEtContent(str[1].split(":")[1]);
            }
        }
    }
}
