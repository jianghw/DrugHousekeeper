package com.cjy.flb.activity;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Base64;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.brithpicker.BrithPopupWindow;
import com.cjy.flb.R;
import com.cjy.flb.bean.GetBoxUserInfo;
import com.cjy.flb.dao.DaoHolder;
import com.cjy.flb.dao.IsNotifDao;
import com.cjy.flb.db.IsNotif;
import com.cjy.flb.manager.AppManager;
import com.cjy.flb.utils.MHttpUtils;
import com.cjy.flb.utils.SharedPreUtil;
import com.cjy.flb.utils.ToastUtil;
import com.google.gson.Gson;
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

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.greenrobot.dao.query.QueryBuilder;

public class ModifyCompleteActivity extends BaseActivity implements View.OnClickListener {
    //xml的控件
    private ImageView imgHead, imgHead2;
    private TextView tvFlbId;
    private EditText etFlbUserName;
    private ImageView imgFlbUser;
    private EditText etFlbUserPhone;
    private TextView tvFlbBrith, tvSex;
    private TextView etContact1, etContact2, etDoctor;
    private ImageView imgContact1, imgContact2, imgDoctor;
    private LinearLayout llSex;

    //通讯录信息
    private String contactName;
    private String contactPhone;
    private boolean isContact = false;

    //添加按钮的标示,用于判断点击的img并且更新EditText
    private int addTag;

    //弹出的生日选择popwindow信息
    private BrithPopupWindow pwBrith;
    private ArrayList<String> brith1Items = new ArrayList<String>();
    private ArrayList<ArrayList<String>> brith2Items = new ArrayList<ArrayList<String>>();

    //完善资料的信息
    private String uId = "";
    private String uName = "";
    private String uPhone = "";
    private String uYear = "";
    private String uMouth = "";
    private String uSex = "";
    private String c1Name = null;
    private String c1Phone = null;
    private String c2Name = null;
    private String c2Phone = null;
    private String dName = null;
    private String dPhone = null;
    private File uAvatar = null;
    private boolean isSex;

    @Override
    public void initView() {
        setContentView(R.layout.activity_complete_modify);
        initBar(getString(R.string.modify_information), false, false);
    }

    @Override
    public void initData() {
        imgHead = (ImageView) findViewById(R.id.img_Flb_head);
        imgHead2 = (ImageView) findViewById(R.id.img_Flb_head2);
        tvFlbId = (TextView) findViewById(R.id.tv_Flb_Id);
        etFlbUserName = (EditText) findViewById(R.id.et_Flb_User);
        imgFlbUser = (ImageView) findViewById(R.id.img_Flb_user);
        etFlbUserPhone = (EditText) findViewById(R.id.et_Flb_Phone);
        tvFlbBrith = (TextView) findViewById(R.id.tv_Flb_Brith);
        etContact1 = (TextView) findViewById(R.id.et_contact1);
        etContact2 = (TextView) findViewById(R.id.et_contact2);
        etDoctor = (TextView) findViewById(R.id.et_doctor);
        imgContact1 = (ImageView) findViewById(R.id.img_contact1);
        imgContact2 = (ImageView) findViewById(R.id.img_contact2);
        imgDoctor = (ImageView) findViewById(R.id.img_doctor);
        llSex = (LinearLayout) findViewById(R.id.ll_sex);
        tvSex = (TextView) findViewById(R.id.tv_Flb_Sex);

        //生日选择器
        pwBrith = new BrithPopupWindow(context);
        uId = MyApplication.flbId;
        tvFlbId.setText(uId);

        updateMedicineUser(uId);
    }

    @Override
    public void initListener() {
        imgFlbUser.setOnClickListener(this);
        imgContact1.setOnClickListener(this);
        imgContact2.setOnClickListener(this);
        imgDoctor.setOnClickListener(this);

        tvFlbBrith.setOnClickListener(this);
        imgHead2.setOnClickListener(this);
        llSex.setOnClickListener(this);

        etContact1.setOnClickListener(this);
        etContact2.setOnClickListener(this);
        etDoctor.setOnClickListener(this);

        llTopBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBack();
            }
        });
    }

    private void showBack() {
        if (etFlbUserName.getText().toString().trim().length() <= 0 ||
                etFlbUserPhone.getText().toString().trim().length() <= 0 ||
                tvFlbBrith.getText().toString().trim().length() <= 0 ||
                tvSex.getText().toString().trim().length() <= 0) {
            ToastUtil.showShort(getString(R.string.read_complete_info));
        } else {
            llTopBack.setEnabled(false);
            new Thread() {
                @Override
                public void run() {
                    uName = etFlbUserName.getText().toString().trim();
                    uPhone = etFlbUserPhone.getText().toString().trim();
                    MyApplication.DEVICE_USER_NAME = uName;
                    MyApplication.DEVICE_USER_PHONE = uPhone;
                    if (!TextUtils.isEmpty(etContact1.getText().toString().trim())) {
                        c1Name = etContact1.getText().toString().trim().split("/")[0];
                        c1Phone = etContact1.getText().toString().trim().split("/")[1];
                    }
                    if (!TextUtils.isEmpty(etContact2.getText().toString().trim())) {
                        c2Name = etContact2.getText().toString().trim().split("/")[0];
                        c2Phone = etContact2.getText().toString().trim().split("/")[1];
                    }
                    if (!TextUtils.isEmpty(etDoctor.getText().toString().trim())) {
                        dName = etDoctor.getText().toString().trim().split("/")[0];
                        dPhone = etDoctor.getText().toString().trim().split("/")[1];
                    }

                    RequestParams params = new RequestParams();
                    String accessToken = SharedPreUtil.getString("access_token");
                    params.addHeader("Authorization", "Bearer " + accessToken);

                    params.addBodyParameter("device_uid", MyApplication.flbId);
                    params.addBodyParameter("device_use_name", uName);
                    params.addBodyParameter("device_use_phone", uPhone);
                    params.addBodyParameter("year", uYear);
                    params.addBodyParameter("mouth", uMouth);

                    params.addBodyParameter("gender", String.valueOf(isSex));

                    if (c1Name != null) {
                        params.addBodyParameter("emergency_contact_1_name", c1Name);
                    }
                    if (c1Phone != null) {
                        params.addBodyParameter("emergency_contact_1_phone", c1Phone);
                    }
                    if (c2Name != null) {
                        params.addBodyParameter("emergency_contact_2_name", c2Name);
                    }
                    if (c2Phone != null) {
                        params.addBodyParameter("emergency_contact_2_phone", c2Phone);
                    }
                    if (dName != null) {
                        params.addBodyParameter("visiting_staff_name", dName);
                    }
                    if (dPhone != null) {
                        params.addBodyParameter("visiting_staff_phone", dPhone);
                    }
                    if (uAvatar != null) {
                        try {
                            String strings = MHttpUtils.toByteArray(uAvatar.getAbsolutePath());
                            params.addBodyParameter("avatar", strings);
                            MyApplication.head = Base64.decode(strings, Base64.DEFAULT);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    HttpUtils httpUtils = new HttpUtils();
                    httpUtils.configCurrentHttpCacheExpiry(1000);
                    httpUtils.send(HttpRequest.HttpMethod.PUT, MHttpUtils.DEVISE_INFO, params,
                            new RequestCallBack<String>() {
                                @Override
                                public void onSuccess(ResponseInfo<String> responseInfo) {
                                    KLog.json(responseInfo.result);
                                    MyApplication.list.clear();//删除数据，重新加载！
                                    MyApplication.hashMap.clear();

                                    AppManager.getAppManager().finishActivity(MainActivity.class);
                                    llTopBack.setEnabled(true);
                                    //成功跳转到主界面
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.SIMPLIFIED_CHINESE);
                                    QueryBuilder<IsNotif> builder = DaoHolder.getIsNotifDao(
                                            IsNotifDao.Properties.Medic_id.eq(MyApplication.flbId),
                                            IsNotifDao.Properties.Today.eq(sdf.format(new Date())));
                                    long count = builder != null ? builder.buildCount().count() : 0;
                                    if (count > 0) {
                                        IsNotif isNotif = builder.build().forCurrentThread().unique();
                                        isNotif.setMedic_name(uName);
                                        isNotif.setMedic_phone(uPhone);
                                        DaoHolder.updateInTxIsNotif(isNotif);
                                    } else {
                                        DaoHolder.insertInTxIsNotif(sdf.format(new Date()), MyApplication.flbId, uName, uPhone);
                                    }

                                    AppManager.getAppManager().finishActivity(ModifyCompleteActivity.class);
                                    Intent intent = new Intent(context, MainActivity.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putInt("currentPosition", MainActivity.currentPosition);
                                    intent.putExtras(bundle);
                                    context.startActivity(intent);
                                }

                                @Override
                                public void onFailure(HttpException e, String s) {
                                    llTopBack.setEnabled(true);
                                    ToastUtil.showShort(getString(R.string.submit_error));
                                    AppManager.getAppManager().finishActivity(ModifyCompleteActivity.class);
                                }
                            });
                }
            }.start();
        }
    }

    /**
     * 根据id获取用户名
     *
     * @param flbID
     */
    public void updateMedicineUser(final String flbID) {
        RequestParams requestParams = new RequestParams();
        String accessToken = SharedPreUtil.getString("access_token");
        requestParams.addHeader("Authorization", "Bearer " + accessToken);
        requestParams.addQueryStringParameter("device_uid", flbID);
        MyApplication.getHttpUtils().send(HttpRequest.HttpMethod.GET, MHttpUtils.MEDIC_USER_INFO,
                requestParams, new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        KLog.json(responseInfo.result);
                        try {
                            JsonElement json = new JsonParser().parse(responseInfo.result);
                            JsonElement jsonElement = null;
                            if (json.isJsonObject()) {
                                JsonObject jsonObject = json.getAsJsonObject();
                                jsonElement = jsonObject.get("response");
                            }
                            if (jsonElement != null && jsonElement.isJsonObject()) {
                                if (!jsonElement.getAsJsonObject().has("message")) {
                                    GetBoxUserInfo getBoxUserInfo = new Gson().fromJson(jsonElement, GetBoxUserInfo.class);
                                    setUserInfo(getBoxUserInfo);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        ToastUtil.showShort(getString(R.string.submit_error));
                    }
                }

        );
    }

    private void setUserInfo(GetBoxUserInfo getBoxUserInfo) {
        etFlbUserName.setText(getBoxUserInfo.getDevice_use_name());
        uName = getBoxUserInfo.getDevice_use_name();
        etFlbUserPhone.setText(getBoxUserInfo.getDevice_use_phone());
        uPhone = getBoxUserInfo.getDevice_use_phone();
        tvFlbBrith.setText(new StringBuilder()
                .append(getBoxUserInfo.getYear())
                .append("年").append(getBoxUserInfo.getMouth()).append("月").toString());
        uYear = getBoxUserInfo.getYear();
        uMouth = getBoxUserInfo.getMouth();
        if (getBoxUserInfo.isGender()) {
            tvSex.setText(getString(R.string.tv_man));
            isSex = true;
        } else {
            tvSex.setText(getString(R.string.tv_woman));
            isSex = false;
        }
        if (!TextUtils.isEmpty((String) getBoxUserInfo.getEmergency_contact_1_phone()) &&
                !TextUtils.isEmpty((String) getBoxUserInfo.getEmergency_contact_1_name())) {
            etContact1.setText(new StringBuilder()
                    .append(getBoxUserInfo.getEmergency_contact_1_name().toString())
                    .append("/").append(getBoxUserInfo.getEmergency_contact_1_phone().toString())
                    .toString());

            c1Name = getBoxUserInfo.getEmergency_contact_1_name().toString();
            c1Phone = getBoxUserInfo.getEmergency_contact_1_phone().toString();
        }
        if (!TextUtils.isEmpty((String) getBoxUserInfo.getEmergency_contact_2_name()) &&
                !TextUtils.isEmpty((String) getBoxUserInfo.getEmergency_contact_2_phone()
                )) {
            etContact2.setText(new StringBuilder()
                    .append(getBoxUserInfo.getEmergency_contact_2_name().toString())
                    .append("/").append(getBoxUserInfo.getEmergency_contact_2_phone().toString())
                    .toString());
            c2Name = getBoxUserInfo.getEmergency_contact_2_name().toString();
            c2Phone = getBoxUserInfo.getEmergency_contact_2_phone().toString();
        }
        if (!TextUtils.isEmpty((String) getBoxUserInfo.getVisiting_staff_name()) &&
                !TextUtils.isEmpty((String) getBoxUserInfo.getVisiting_staff_phone())) {
            etDoctor.setText(new StringBuilder()
                    .append(getBoxUserInfo.getVisiting_staff_name().toString())
                    .append("/").append(getBoxUserInfo.getVisiting_staff_phone().toString())
                    .toString());
            dName = getBoxUserInfo.getVisiting_staff_name().toString();
            dPhone = getBoxUserInfo.getVisiting_staff_phone().toString();
        }

        String str = (String) getBoxUserInfo.getAvatar();
        if (str != null) {
            byte[] bytes = Base64.decode(str, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            imgHead.setImageBitmap(bitmap);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.et_contact1:
                showDialog(etContact1, 0);
                break;
            case R.id.et_contact2:
                showDialog(etContact2, 1);
                break;
            case R.id.et_doctor:
                showDialog(etDoctor, 2);
                break;
            case R.id.img_Flb_user://药盒使用者
                addTag = 1;
                getContacts();
                isContact = true;
                break;

            case R.id.img_contact1://监护人1
                addTag = 2;
                getContacts();
                isContact = true;
                break;

            case R.id.img_contact2://监护人2
                addTag = 3;
                getContacts();
                isContact = true;
                break;

            case R.id.img_doctor://医生
                addTag = 4;
                getContacts();
                isContact = true;
                break;
            case R.id.tv_Flb_Brith://向控件的正下方弹出选择器
                pwBrith.showAtLocation(tvFlbBrith, Gravity.BOTTOM, 0, 0);
                chooseData();
                break;

            case R.id.img_Flb_head2://拍照
                startActivityForResult(new Intent(context, PhotoActivity.class), 101);
                isContact = false;
                break;

            case R.id.ll_sex://性别
                startActivityForResult(new Intent(context, ChooseSexActivity.class), 102);
                isContact = false;
                break;
        }
    }

    private void showDialog(final TextView etContact, int i) {
        View view = View.inflate(this, R.layout.complete_alert_dialog, null);
        TextView title = (TextView) view.findViewById(R.id.tv_tilte);
        final EditText nameEdit = (EditText) view.findViewById(R.id.edit_name);
        final EditText phoneEdit = (EditText) view.findViewById(R.id.edit_phone);
        Button noBtn = (Button) view.findViewById(R.id.btn_no);
        Button okBtn = (Button) view.findViewById(R.id.btn_ok);
        switch (i) {
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
        AlertDialog.Builder builder = new AlertDialog.Builder(ModifyCompleteActivity.this);
        builder.setView(view);
        final AlertDialog mDialog = builder.create();
        mDialog.show();

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(nameEdit.getText().toString().trim()) || TextUtils.isEmpty(phoneEdit.getText()
                        .toString().trim())) {

                    ToastUtil.showShort(getString(R.string.info_not_null));
                } else {
                    etContact.setText(new StringBuilder()
                            .append(nameEdit.getText().toString().trim())
                            .append("/").append(phoneEdit.getText().toString().trim())
                            .toString());
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

    /**
     * 得到系统通讯录的信息
     */
    private void getContacts() {
        startActivityForResult(new Intent(Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI), 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (isContact) {
            if (data != null) {
                ContentResolver contentResolver = getContentResolver();
                Uri contactData = data.getData();
                //query()的参数分别为，文件的Uri地址，需要查询的字段，筛选的条件，筛选的参数，排序方式
                Cursor cursor = managedQuery(contactData, null, null, null, null);
                cursor.moveToFirst();

                contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));

                Cursor phone = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId,
                        null,
                        null);
                while (phone != null && phone.moveToNext()) {
                    contactPhone = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    Pattern p = Pattern.compile("(\\d+)");
                    Matcher matcher = p.matcher(contactPhone);
                    StringBuilder sb = new StringBuilder();
                    while (matcher.find()) {
                        String find = matcher.group(1);
                        sb.append(find);
                    }
                    contactPhone = sb.toString();
                }

                if (addTag == 1) {
                    setUser(etFlbUserName, etFlbUserPhone, contactName, contactPhone);
                    uName = contactName;
                    uPhone = contactPhone;
                } else if (addTag == 2) {
                    setContact(etContact1, contactName, contactPhone);
                    c1Name = contactName;
                    c1Phone = contactPhone;
                    KLog.i(c1Name);
                } else if (addTag == 3) {
                    setContact(etContact2, contactName, contactPhone);
                    c2Name = contactName;
                    c2Phone = contactPhone;
                } else if (addTag == 4) {
                    setContact(etDoctor, contactName, contactPhone);
                    dName = contactName;
                    dPhone = contactPhone;
                }
            }
        } else {
            switch (requestCode) {
                case 101:
                    if (MyApplication.imgHead != null) {

                        Bitmap bitmap = BitmapFactory.decodeFile(MyApplication.imgHead.getAbsolutePath());
                        imgHead.setImageBitmap(bitmap);
                        uAvatar = MyApplication.imgHead;
                    }
                    break;

                case 102:
                    tvSex.setText(ChooseSexActivity.sex != null ? ChooseSexActivity.sex : getString(R.string.tv_man));
                    uSex = tvSex.getText().toString();
                    if (uSex.equals(getString(R.string.tv_man))) {
                        isSex = true;
                    } else {
                        isSex = false;
                    }
                    break;
            }
        }
    }

    /**
     * 设置药盒使用者
     *
     * @param etUaser 药盒使用者姓名输入框
     * @param etPhone 药盒使用者电话输入框
     * @param name    药盒使用者姓名
     * @param phone   药盒使用者电话
     */
    private void setUser(EditText etUaser, EditText etPhone, String name, String phone) {
        etUaser.setText(name);
        etPhone.setText(phone);
    }

    /**
     * 设置联系人
     *
     * @param etContact 联系人输入框
     * @param name      联系人姓名
     * @param phone     联系人电话
     */
    private void setContact(TextView etContact, String name, String phone) {
        etContact.setText(name + "/" + phone);
    }

    /**
     * 开启时间选择的pw
     */
    private void chooseData() {
        initDatas();
        //初始化pwBrith的信息,关闭联动
        pwBrith.setPicker(brith1Items, brith2Items, false);

        //设置初始位置
        pwBrith.setSelectOptions(50, 0);

        //设置pwBrith的监听事件
        pwBrith.setOnoptionsSelectListener(new BrithPopupWindow.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2) {
                String brith = brith1Items.get(options1) + "年" +
                        brith2Items.get(0).get(option2) + "月";

                uYear = brith1Items.get(options1);
                uMouth = brith2Items.get(0).get(option2);

                tvFlbBrith.setText(brith);
            }
        });
    }

    private void initDatas() {
        //得到当前年号
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);

        //年
        for (int i = 1900; i < year; i++) {
            brith1Items.add(String.valueOf(i));
        }

        //月
        ArrayList<String> brith2Items_01 = new ArrayList<String>();
        ArrayList<String> brith2Items_02 = new ArrayList<String>();
        for (int i = 1; i <= 12; i++) {
            brith2Items_01.add(String.valueOf(i));
            brith2Items_02.add(String.valueOf(i));
        }
        brith2Items.add(brith2Items_01);
        brith2Items.add(brith2Items_02);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_HOME:
                return true;
            case KeyEvent.KEYCODE_BACK:
                showBack();
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
