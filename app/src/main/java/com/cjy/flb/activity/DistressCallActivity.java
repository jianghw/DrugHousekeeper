package com.cjy.flb.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.cjy.flb.R;
import com.cjy.flb.adapter.DistressCallAdapter;
import com.cjy.flb.bean.GetBoxUserInfo;
import com.cjy.flb.bean.LinkmanID;
import com.cjy.flb.manager.AppManager;
import com.cjy.flb.utils.MHttpUtils;
import com.cjy.flb.utils.SharedPreUtil;
import com.cjy.flb.utils.ToastUtil;
import com.google.gson.Gson;
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

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2015/12/7 0007.
 */
public class DistressCallActivity extends BaseActivity {

    @Bind(R.id.listView)
    ListView mListView;

    private ArrayList<LinkmanID> list = new ArrayList<>();
    private DistressCallAdapter adapter;
    private boolean gender;
    private String year;
    private String mouth;
    private String name0;
    private String phone0;
    private String name1;
    private String phone1;
    private String name2;
    private String phone2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initView() {
        setContentView(R.layout.activity_distress_call);
        ButterKnife.bind(this);
        initBar(getString(R.string.emergency_call), false, false);
    }

    @Override
    public void initData() {
        list.add(new LinkmanID(0, "", ""));
        list.add(new LinkmanID(1, "", ""));
        list.add(new LinkmanID(2, "", ""));

        adapter = new DistressCallAdapter(this, list);
        mListView.setAdapter(adapter);

        updateMedicineUser();
    }

    @Override
    public void initListener() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final TextView phone = (TextView) view.findViewById(R.id.tv_phone);
                if (TextUtils.isEmpty(phone.getText())) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(DistressCallActivity.this);
                    builder.setMessage(getString(R.string.call_no_phone));
                    builder.setPositiveButton(getString(R.string.btn_cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.setNegativeButton(getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, final int which) {
                            View view = View.inflate(DistressCallActivity.this, R.layout.complete_alert_dialog, null);
                            TextView title = (TextView) view.findViewById(R.id.tv_tilte);
                            final EditText nameEdit = (EditText) view.findViewById(R.id.edit_name);
                            final EditText phoneEdit = (EditText) view.findViewById(R.id.edit_phone);
                            Button noBtn = (Button) view.findViewById(R.id.btn_no);
                            Button okBtn = (Button) view.findViewById(R.id.btn_ok);
                            switch (position) {
                                case 0:
                                    title.setText(getString(R.string.guardian_1));
                                    break;
                                case 1:
                                    title.setText(getString(R.string.guardian_2));
                                    break;
                                case 2:
                                    title.setText(getString(R.string.doctor));
                                    break;
                            }
                            AlertDialog.Builder builder = new AlertDialog.Builder(DistressCallActivity.this);
                            builder.setView(view);
                            final AlertDialog mDialog = builder.create();
                            mDialog.show();
                            okBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (TextUtils.isEmpty(nameEdit.getText().toString().trim())
                                            || TextUtils.isEmpty(phoneEdit.getText().toString().trim())) {
                                        ToastUtil.showShort(getString(R.string.info_not_null));
                                    } else {
                                        list.get(position).setName(nameEdit.getText().toString().trim());
                                        list.get(position).setPhone(phoneEdit.getText().toString().trim());
                                        switch (position) {
                                            case 0:
                                                name0 = nameEdit.getText().toString().trim();
                                                phone0 = phoneEdit.getText().toString().trim();
                                                break;
                                            case 1:
                                                name1 = nameEdit.getText().toString().trim();
                                                phone1 = phoneEdit.getText().toString().trim();
                                                break;
                                            case 2:
                                                name2 = nameEdit.getText().toString().trim();
                                                phone2 = phoneEdit.getText().toString().trim();
                                                break;
                                        }
                                        adapter.notifyDataSetChanged();
                                        mDialog.dismiss();
                                    }
                                }
                            });
                            noBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mDialog.dismiss();
                                }
                            });
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(DistressCallActivity.this);
                    builder.setMessage(getString(R.string.emergency_call));
                    builder.setPositiveButton(getString(R.string.btn_cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.setNegativeButton(getString(R.string.call), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String num = phone.getText().toString();
                            if (!num.equals("")) {
                                Pattern p = Pattern.compile("\\d+?");
                                Matcher match = p.matcher(num);
                                //正则验证输入的是否为数字
                                if (match.matches()) {
                                    try {
                                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + num));
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    ToastUtil.showShort(getString(R.string.entry_phone_ok));
                                }
                            }
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
        //更新用户数据
        llTopBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserDate();
            }
        });
    }

    private void updateUserDate() {
        if (name0 != null || name1 != null || name2 != null) {
            RequestParams params = new RequestParams();
            String accessToken = SharedPreUtil.getString("access_token");
            params.addHeader("Authorization", "Bearer " + accessToken);
            params.addBodyParameter("device_uid", MyApplication.flbId);
            params.addBodyParameter("device_use_name", MyApplication.DEVICE_USER_NAME);
            params.addBodyParameter("device_use_phone", MyApplication.DEVICE_USER_PHONE);
            params.addBodyParameter("year", year);
            params.addBodyParameter("mouth", mouth);
            params.addBodyParameter("gender", String.valueOf(gender));
            if (name0 != null) {
                params.addBodyParameter("emergency_contact_1_name", name0);
            }
            if (phone0 != null) {
                params.addBodyParameter("emergency_contact_1_phone", phone0);
            }
            if (name1 != null) {
                params.addBodyParameter("emergency_contact_2_name", name1);
            }
            if (phone1 != null) {
                params.addBodyParameter("emergency_contact_2_phone", phone1);
            }
            if (name2 != null) {
                params.addBodyParameter("visiting_staff_name", name2);
            }
            if (phone2 != null) {
                params.addBodyParameter("visiting_staff_phone", phone2);
            }
            HttpUtils httpUtils = new HttpUtils();
            httpUtils.configCurrentHttpCacheExpiry(1000);
            httpUtils.send(HttpRequest.HttpMethod.PUT, MHttpUtils.DEVISE_INFO, params,
                    new RequestCallBack<String>() {
                        @Override
                        public void onSuccess(ResponseInfo<String> responseInfo) {
                            //成功跳转到主界面
                            AppManager.getAppManager().finishActivity(DistressCallActivity.class);
                        }

                        @Override
                        public void onFailure(HttpException e, String s) {
                            KLog.d(s);
                            AppManager.getAppManager().finishActivity(DistressCallActivity.class);
                        }
                    });
        } else {
            AppManager.getAppManager().finishActivity(DistressCallActivity.class);
        }
    }

    public void updateMedicineUser() {
        RequestParams requestParams = new RequestParams();
        String accessToken = SharedPreUtil.getString("access_token");
        requestParams.addHeader("Authorization", "Bearer " + accessToken);
        requestParams.addQueryStringParameter("device_uid", MyApplication.flbId);
        HttpUtils httpUtils = new HttpUtils();
        httpUtils.configDefaultHttpCacheExpiry(1000);
        httpUtils.send(HttpRequest.HttpMethod.GET, MHttpUtils.MEDIC_USER_INFO, requestParams,
                new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        try {
                            JsonElement json = new JsonParser().parse(responseInfo.result);
                            JsonObject object = null;
                            if (json.isJsonObject()) {
                                JsonObject jsonObject = json.getAsJsonObject();
                                JsonElement jsonElement = jsonObject.get("response");
                                if (jsonElement.isJsonObject()) {
                                    object = jsonElement.getAsJsonObject();
                                }
                            }
                            if (object != null && object.has("message")) {
                                ToastUtil.showShort(getString(R.string.box_id_no_info));
                            } else if (object != null && !object.has("message")) {
                                onDistributionInformation(object);
                            }
                        } catch (JsonSyntaxException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        KLog.d(s);
                    }
                });
    }

    private void onDistributionInformation(JsonObject object) {
        GetBoxUserInfo medicUser = new Gson().fromJson(object, GetBoxUserInfo.class);
        MyApplication.DEVICE_USER_NAME = medicUser.getDevice_use_name();
        MyApplication.DEVICE_USER_PHONE = medicUser.getDevice_use_phone();
        gender = medicUser.isGender();
        year = medicUser.getYear();
        mouth = medicUser.getMouth();

        if (medicUser.getEmergency_contact_1_name() != null &&
                medicUser.getEmergency_contact_1_phone() != null) {
            list.get(0).setName(medicUser.getEmergency_contact_1_name().toString());
            list.get(0).setPhone(medicUser.getEmergency_contact_1_phone().toString());
        }
        if (medicUser.getEmergency_contact_2_name() != null &&
                medicUser.getEmergency_contact_2_phone() != null) {
            list.get(1).setName(medicUser.getEmergency_contact_2_name().toString());
            list.get(1).setPhone(medicUser.getEmergency_contact_2_phone().toString());
        }
        if (medicUser.getVisiting_staff_name() != null &&
                medicUser.getVisiting_staff_phone() != null) {
            list.get(2).setName(medicUser.getVisiting_staff_name().toString());
            list.get(2).setPhone(medicUser.getVisiting_staff_phone().toString());
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_HOME:
                return true;
            case KeyEvent.KEYCODE_BACK:
                updateUserDate();
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
