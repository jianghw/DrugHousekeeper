package com.cjy.flb.activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.cjy.flb.R;
import com.cjy.flb.adapter.DatabaseAdapter;
import com.cjy.flb.adapter.MProblemAdapter;
import com.cjy.flb.db.SetMedicTime;
import com.cjy.flb.manager.AppManager;
import com.cjy.flb.utils.MHttpUtils;
import com.cjy.flb.utils.SharedPreUtil;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2015/12/7 0007.
 */
public class MProblemActivity extends BaseActivity implements View.OnClickListener {


    @Bind(R.id.edit_content)
    EditText mEditContent;
    @Bind(R.id.tv_medicine_name)
    TextView mTvMedicineName;
    @Bind(R.id.edit_medicine_company)
    TextView mEditMedicineCompany;
    @Bind(R.id.btn_ok_setf)
    Button mBtnOkSetf;
    @Bind(R.id.lv_text_name)
    LinearLayout mLvTextName;
    @Bind(R.id.lv_edit)
    LinearLayout mLvEdit;
    /**
     * 存放药物的
     */
    private HashMap<Integer, Boolean> hasMap;
    private ArrayList<String> medicList;
    //选中的id
    private String curId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initView() {
        setContentView(R.layout.activity_mproblem);
        ButterKnife.bind(this);
    }

    @Override
    public void initData() {
        initBar(getString(R.string.medicine_hamful_feedback), false, false);

        hasMap = new HashMap<>();
        List<SetMedicTime> smtList = MyApplication.getDaoSession().getSetMedicTimeDao().loadAll();
        for (SetMedicTime smt : smtList) {
            hasMap.put(smt.getMedicine_id(), true);
        }
        medicList = new ArrayList<>();
        for (Integer id : hasMap.keySet()) {
            String text = DatabaseAdapter.getIntance(context).queryInfoByIdForProblem(String.valueOf(id));
            medicList.add(text);
        }

    }

    @Override
    public void initListener() {
        mLvTextName.setOnClickListener(this);
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
            case R.id.lv_text_name:
                View contentView = LayoutInflater.from(MProblemActivity.this).inflate(
                        R.layout.popu_mproblem_activity, null);
                ListView listView = (ListView) contentView.findViewById(R.id.listView);
                MProblemAdapter adapter = new MProblemAdapter(MProblemActivity.this, medicList);
                listView.setAdapter(adapter);

                final PopupWindow popupWindow = new PopupWindow(contentView, v.getWidth(),
                        ViewGroup.LayoutParams.WRAP_CONTENT, true);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String[] string = medicList.get(position).split("/");
                        mTvMedicineName.setText(string[0]);
                        mEditMedicineCompany.setText(string[1]);
                        curId = string[2];
                        if (popupWindow.isShowing()) {
                            popupWindow.dismiss();
                        }
                    }
                });
                popupWindow.setTouchable(true);
                popupWindow.setTouchInterceptor(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return false;
                    }
                });
                popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.tv_bg_dialog));
                popupWindow.showAsDropDown(v);

                break;
            case R.id.lv_edit:
                mEditContent.setFocusable(true);
                mEditContent.setFocusableInTouchMode(true);
                mEditContent.requestFocus();
                mEditContent.requestFocusFromTouch();

                ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).showSoftInput(mEditContent, 0);
                break;
            case R.id.btn_ok_setf:

                if (!mTvMedicineName.getText().equals(getString(R.string.medicine_name)) &&
                        !TextUtils.isEmpty(mEditContent.getText().toString().trim())) {
                    RequestParams requestParams = new RequestParams();
                    String accessToken = SharedPreUtil.getString("access_token");
                    requestParams.addHeader("Authorization", "Bearer " + accessToken);
                    requestParams.addBodyParameter("content", mEditContent.getText().toString().trim());
                    requestParams.addBodyParameter("medicine_id", curId);
                    HttpUtils httpUtils = new HttpUtils();
                    httpUtils.configCurrentHttpCacheExpiry(0);
                    httpUtils.send(HttpRequest.HttpMethod.POST, MHttpUtils.MEDIC_FEEDBACK, requestParams,
                            new RequestCallBack<String>() {
                                @Override
                                public void onSuccess(ResponseInfo<String> responseInfo) {
                                    try {
                                        onSubmitSucceed(responseInfo);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onFailure(HttpException e, String s) {
                                    KLog.d(s);
                                    AlertDialog.Builder builder = new AlertDialog.Builder(MProblemActivity.this);
                                    builder.setMessage(getString(R.string.submit_error));
                                    final AlertDialog mDialog = builder.create();
                                    mDialog.show();
                                    mDialog.setCanceledOnTouchOutside(false);
                                    new Thread() {
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

    private void onSubmitSucceed(ResponseInfo<String> responseInfo) throws Exception{
        JsonElement json = new JsonParser().parse(responseInfo.result);
        int code = 0;
        JsonObject object = null;
        if (json.isJsonObject()) {
            JsonObject jsonObject = json.getAsJsonObject();
            JsonElement jsonElement = jsonObject.get("response");
            if (jsonElement.isJsonObject()) {
                object = jsonElement.getAsJsonObject();
                JsonElement codeElement = object.get("code");
                code = codeElement.getAsInt();
            }
        }
        if (code == 1) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MProblemActivity.this);
            builder.setMessage(getString(R.string.submit_ok));
            final AlertDialog mDialog = builder.create();
            mDialog.show();
            mDialog.setCanceledOnTouchOutside(false);
            new Thread() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1500);
                        mDialog.dismiss();
                        AppManager.getAppManager().finishActivity(MProblemActivity.this);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }.start();

        } else {
            JsonElement failureElement = object != null ? object.get("failure_index") : null;
            String failure = failureElement != null ? failureElement.getAsString() : getString(R.string.submit_error);
            AlertDialog.Builder builder = new AlertDialog.Builder(MProblemActivity.this);
            builder.setMessage(failure);
            final AlertDialog mDialog = builder.create();
            mDialog.show();
            mDialog.setCanceledOnTouchOutside(false);
            new Thread() {
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
    }


}
