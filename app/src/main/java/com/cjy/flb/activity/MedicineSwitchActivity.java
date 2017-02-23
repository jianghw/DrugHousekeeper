package com.cjy.flb.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.cjy.flb.R;
import com.cjy.flb.adapter.MedicManagerAdapter;
import com.cjy.flb.bean.BindingID;
import com.cjy.flb.bean.GetBoxUserInfo;
import com.cjy.flb.dao.DaoHolder;
import com.cjy.flb.dao.IsNotifDao;
import com.cjy.flb.db.IsNotif;
import com.cjy.flb.domain.BindedFlb;
import com.cjy.flb.manager.AppManager;
import com.cjy.flb.utils.MHttpUtils;
import com.cjy.flb.utils.SharedPreUtil;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by Administrator on 2015/12/7 0007.
 * 药盒管理
 */
public class MedicineSwitchActivity extends BaseActivity {
    @Bind(R.id.listView)
    ListView mListView;

    //    private Vector<BindingID> list = new Vector<>();
    public static MedicManagerAdapter adapter;
    private List<String> mDatas = new ArrayList<>();

    /**
     * 存放药盒信息
     */
    //    private HashMap<String, MedicineUser> hashMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initView() {
        setContentView(R.layout.activity_medicine_manager);
        ButterKnife.bind(this);
        initBar(getString(R.string.switch_medicine_box), false, false);
    }

    @Override
    public void initData() {
        adapter = new MedicManagerAdapter(MedicineSwitchActivity.this, MyApplication.list);
        mListView.setAdapter(adapter);

        //获取药盒id
        if (MyApplication.list.size() > 0) {
            Collections.sort(MyApplication.list, new Comparator<BindingID>() {
                @Override
                public int compare(BindingID lhs, BindingID rhs) {
                    return lhs.getMedicId().compareTo(rhs.getMedicId());
                }
            });
            adapter.notifyDataSetChanged();
        } else {
            accessToPCSId();
        }
    }

    private void accessToPCSId() {
        RequestParams requestParams = new RequestParams();
        String accessToken = SharedPreUtil.getString("access_token");
        requestParams.addHeader("Authorization", "Bearer " + accessToken);
        HttpUtils httpUtils = new HttpUtils();
        httpUtils.configDefaultHttpCacheExpiry(0);
        httpUtils.send(HttpRequest.HttpMethod.GET, MHttpUtils.isBindFlbUrl,
                requestParams,
                new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        try {
                            JsonElement json = new JsonParser().parse(responseInfo.result);
                            JsonElement jsonElement = null;
                            if (json.isJsonObject()) {
                                JsonObject jsonObject = json.getAsJsonObject();
                                jsonElement = jsonObject.get("response");
                            }
                            if (jsonElement != null && jsonElement.isJsonArray()) {
                                BindedFlb bindedFlb = new Gson().fromJson(responseInfo.result, BindedFlb.class);
                                //如果当前已绑定药盒
                                mDatas = bindedFlb.getResponse();
                                for (String id : mDatas) {
                                    updateMedicineUser(id);
                                }
                            }
                        } catch (JsonSyntaxException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        KLog.d("s");
                    }
                });
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
                            onNetworkDataProcessing(responseInfo, flbID);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        KLog.d(s);
                    }
                }

        );
    }

    private void onNetworkDataProcessing(ResponseInfo<String> responseInfo, String flbID) {
        JsonElement json = new JsonParser().parse(responseInfo.result);
        JsonElement jsonElement = null;
        if (json.isJsonObject()) {
            JsonObject jsonObject = json.getAsJsonObject();
            jsonElement = jsonObject.get("response");
        }
        if (jsonElement != null && jsonElement.isJsonObject()) {
            if (!jsonElement.getAsJsonObject().has("message")) {
                GetBoxUserInfo getBoxUserInfo = new Gson().fromJson(jsonElement, GetBoxUserInfo.class);
                BindingID bindingID = new BindingID("null", "null", "null");
                bindingID.setName(getBoxUserInfo.getDevice_use_name());
                bindingID.setMedicId(flbID);
                bindingID.setUrl((String) getBoxUserInfo.getAvatar());
                MyApplication.hashMap.put(flbID, getBoxUserInfo);

                MyApplication.list.add(bindingID);
                Collections.sort(MyApplication.list, new Comparator<BindingID>() {
                    @Override
                    public int compare(BindingID lhs, BindingID rhs) {
                        return lhs.getMedicId().compareTo(rhs.getMedicId());
                    }
                });
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void initListener() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(MedicineSwitchActivity.this);
                builder.setMessage(getString(R.string.switch_the_box))
                        .setPositiveButton(getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                BindingID bindingID = MyApplication.list.get(position);

                                MyApplication.flbId = bindingID.getMedicId();
                                SharedPreUtil.setString("flbId", MyApplication.flbId);

                                MyApplication.DEVICE_USER_NAME = MyApplication.hashMap.get(MyApplication.flbId)
                                        .getDevice_use_name();
                                MyApplication.DEVICE_USER_PHONE = MyApplication.hashMap.get(MyApplication.flbId)
                                        .getDevice_use_phone();
                                String strings = (String) MyApplication.hashMap.get(MyApplication.flbId).getAvatar();

                                if (strings != null && !TextUtils.isEmpty(strings)) {
                                    MyApplication.head = Base64.decode(strings, Base64.DEFAULT);
                                } else {
                                    MyApplication.head = null;
                                }

                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.SIMPLIFIED_CHINESE);
                                QueryBuilder<IsNotif> builder = DaoHolder.getIsNotifDao(
                                        IsNotifDao.Properties.Medic_id.eq(MyApplication.flbId),
                                        IsNotifDao.Properties.Today.eq(sdf.format(new Date())));
                                long count = builder != null ? builder.buildCount().count() : 0;
                                if (count == 1) {
                                    IsNotif isNotif = builder.build().forCurrentThread().unique();
                                    isNotif.setMedic_name(MyApplication.DEVICE_USER_NAME);
                                    isNotif.setMedic_phone(MyApplication.DEVICE_USER_PHONE);
                                    DaoHolder.updateInTxIsNotif(isNotif);
                                } else if (count == 0) {
                                    DaoHolder.insertInTxIsNotif(sdf.format(new Date()), MyApplication.flbId,
                                            MyApplication.DEVICE_USER_NAME, MyApplication.DEVICE_USER_PHONE);
                                }

                                AppManager.getAppManager().finishActivity(MainActivity.class);
                                //拉取2个月的网络数据
                                MHttpUtils.pullTwoMonthMedinInfo(context, MyApplication.flbId);

                                dialog.dismiss();
                                AppManager.getAppManager().finishActivity(MedicineSwitchActivity.class);
                                Intent intent = new Intent(context, MainActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putInt("currentPosition", MainActivity.currentPosition);
                                intent.putExtras(bundle);
                                context.startActivity(intent);
                            }
                        })
                        .setNegativeButton(getString(R.string.btn_cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

}
