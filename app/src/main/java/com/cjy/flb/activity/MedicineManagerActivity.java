package com.cjy.flb.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.cjy.flb.R;
import com.cjy.flb.adapter.MedicManagerAdapter;
import com.cjy.flb.bean.BindingID;
import com.cjy.flb.bean.GetBoxUserInfo;
import com.cjy.flb.domain.BindedFlb;
import com.cjy.flb.utils.MHttpUtils;
import com.cjy.flb.utils.SharedPreUtil;
import com.cjy.flb.utils.ToastUtil;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.socks.library.KLog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2015/12/7 0007.
 * 药盒管理
 */
public class MedicineManagerActivity extends BaseActivity {
    @Bind(R.id.listView)
    ListView mListView;

    //    private Vector<BindingID> list = new Vector<>();
    public static MedicManagerAdapter adapter;
    //    private HashMap<Integer, Boolean> map = new HashMap<>();
    //点击的当前位置
    private int curPosition = -1;
    private List<String> mDatas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initView() {
        setContentView(R.layout.activity_medicine_manager);
        ButterKnife.bind(this);

        initBar(getString(R.string.box_management), false, true);
        tvMore.setVisibility(View.VISIBLE);
        tvMore.setText(getString(R.string.edit));
    }

    @Override
    public void initData() {
        adapter = new MedicManagerAdapter(MedicineManagerActivity.this, MyApplication.list);
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
        MyApplication.getHttpUtils().send(HttpRequest.HttpMethod.GET, MHttpUtils.isBindFlbUrl,
                requestParams,
                new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        KLog.json(responseInfo.result);
                        try {
                            JsonElement jsonElement = new JsonParser().parse(responseInfo.result);
                            JsonObject jsonObject = jsonElement.getAsJsonObject();
                            JsonElement element = jsonObject.get("response");
                            if (element != null && element.isJsonArray()) {
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
                        KLog.d(s);
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

        MyApplication.getHttpUtils().send(HttpRequest.HttpMethod.GET, MHttpUtils.MEDIC_USER_INFO, requestParams,
                new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        KLog.json(responseInfo.result);
                        try {
                            onNetworkDataProcessing(responseInfo, flbID);
                        } catch (JsonSyntaxException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        KLog.i(s);
                    }
                });
    }

    private void onNetworkDataProcessing(ResponseInfo<String> responseInfo, String flbID) {
        JsonElement json = new JsonParser().parse(responseInfo.result);
        JsonObject jsonObject = json.getAsJsonObject();
        JsonElement jsonElement = jsonObject.get("response");
        if (jsonElement.isJsonObject()) {
            JsonObject object = jsonElement.getAsJsonObject();
            if (object.has("message")) {
                ToastUtil.showShort(getString(R.string.box_id_no_info));
            } else {
                BindingID bindingID = new BindingID("null", "null", "null");
                GetBoxUserInfo medicUser = new Gson().fromJson(jsonElement, GetBoxUserInfo.class);
                bindingID.setName(medicUser.getDevice_use_name());
                bindingID.setMedicId(flbID);
                bindingID.setUrl((String) medicUser.getAvatar());
                MyApplication.hashMap.put(flbID, medicUser);
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
        tvMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tvMore.getText().toString().equals(getString(R.string.edit))) {
                    tvMore.setText(getString(R.string.finish));
                    adapter.setCanDelete(true);
                    adapter.notifyDataSetChanged();
                } else if (tvMore.getText().toString().equals(getString(R.string.finish))) {
                    tvMore.setText(getString(R.string.edit));
                    adapter.setCanDelete(false);
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
