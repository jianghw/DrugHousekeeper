package com.cjy.flb.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.cjy.flb.R;
import com.cjy.flb.activity.MyApplication;
import com.cjy.flb.activity.SetRepeatDrug;
import com.cjy.flb.dao.SetMedicTimeDao;
import com.cjy.flb.db.SetMedicTime;
import com.cjy.flb.utils.MHttpUtils;
import com.cjy.flb.utils.SharedPreUtil;
import com.cjy.flb.utils.UIUtils;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;
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

import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by Administrator on 2016/3/1 0001.
 */
public class SetRepeatDrugAdapter extends BaseSwipeAdapter {
    private Handler handler;
    private Context context;

    public SetRepeatDrugAdapter(Context context, Handler handler)
    {
        this.context = context;
        this.handler = handler;
    }

    @Override
    public int getSwipeLayoutResourceId(int i)
    {
        return R.id.swipe;
    }

    @Override
    public int getCount()
    {
        return SetRepeatDrug.mList.size();
    }

    @Override
    public Object getItem(int position)
    {
        return SetRepeatDrug.mList.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    /**
     * 返回item的布局
     *
     * @param position
     * @param viewGroup
     * @return
     */
    @Override
    public View generateView(int position, ViewGroup viewGroup)
    {
        View convertView = LayoutInflater.from(context).inflate(
                R.layout.item_add_medic_adapter, null);
        return convertView;
    }

    @Override
    public void fillValues(final int position, View convertView)
    {
        TextView mTextID = (TextView) convertView.findViewById(R.id.tv_position);
        TextView mTextName = (TextView) convertView.findViewById(R.id.tv_body);
        TextView mTextCount = (TextView) convertView.findViewById(R.id.tv_count);
        TextView mTextUnit = (TextView) convertView.findViewById(R.id.tv_unit);
        mTextID.setText(String.valueOf(position + 1) + "、");
        String[] str = SetRepeatDrug.mList.get(position).split("/");
        if (str.length > 2) {
            mTextName.setText(str[0]);
            mTextCount.setText(str[1]);
        }
        mTextUnit.setText("PCS");

        final SwipeLayout swipeLayout = (SwipeLayout) convertView.findViewById(getSwipeLayoutResourceId(position));
        convertView.findViewById(R.id.trash).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                deleteMedicineItem(position, swipeLayout);
            }
        });
    }

    /**
     * 删除一个药物
     *
     * @param position
     * @param swipeLayout
     */
    public void deleteMedicineItem(final int position, final SwipeLayout swipeLayout)
    {

        RequestParams requestParams = new RequestParams();
        String accessToken = SharedPreUtil.getString("access_token");
        requestParams.addHeader("Authorization", "Bearer " + accessToken);
        requestParams.addBodyParameter("device_uid", MyApplication.flbId);
        requestParams.addBodyParameter("number", SetRepeatDrug.transferNumber);
        final String[] strings = SetRepeatDrug.mList.get(position).split("/");
        requestParams.addBodyParameter("medicine_id", strings[2]);
        requestParams.addBodyParameter("unit", "粒");
        HttpUtils httpUtils = new HttpUtils();
        httpUtils.configCurrentHttpCacheExpiry(10);
        httpUtils.send(HttpRequest.HttpMethod.POST, MHttpUtils.DELETE_MEDIC, requestParams,
                new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo)
                    {
                        KLog.json(responseInfo.result);
                        JsonObject jObject = null;
                        JsonElement code = null;
                        try {
                            JsonElement jsonElement = new JsonParser().parse(responseInfo.result);
                            JsonObject jsonObject = jsonElement.getAsJsonObject();
                            JsonElement response = jsonObject.get("response");
                            jObject = response.getAsJsonObject();
                            code = jObject.get("code");
                        } catch (JsonSyntaxException e) {
                            e.printStackTrace();
                        }
                        if ((code != null ? code.getAsInt() : 0) == 1) {
                            Message message = Message.obtain();
                            message.what = 100;
                            handler.sendMessage(message);
                        } else {
                            JsonElement failure = jObject != null ? jObject.get("failure_index") : null;
                            if (failure != null && failure.getAsString().equals("medicine not have")) {
                                Message message = Message.obtain();
                                message.what = 100;
                                handler.sendMessage(message);
                            }
                        }

                        QueryBuilder<SetMedicTime> builder = MyApplication.getDaoSession().getSetMedicTimeDao()
                                .queryBuilder().where(
                                SetMedicTimeDao.Properties.Device_uid.eq(MyApplication.flbId),
                                SetMedicTimeDao.Properties.Number.eq(SetRepeatDrug.transferNumber),
                                SetMedicTimeDao.Properties.Medicine_id.eq(strings[2]));
                        if (builder.buildCount().count() > 0) {
                            SetMedicTime smt = builder.build().forCurrentThread().unique();
                            MyApplication.getDaoSession().getSetMedicTimeDao().delete(smt);
                        }

                        SetRepeatDrug.mList.remove(position);
                    }

                    @Override
                    public void onFailure(HttpException e, String s)
                    {
                        Toast.makeText(UIUtils.getContext(), "删除失败" + s, Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
