package com.cjy.flb.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cjy.flb.R;
import com.cjy.flb.activity.MedicineManagerActivity;
import com.cjy.flb.activity.MyApplication;
import com.cjy.flb.bean.BindingID;
import com.cjy.flb.dao.EatTimeDao;
import com.cjy.flb.dao.IsNotifDao;
import com.cjy.flb.db.EatTime;
import com.cjy.flb.db.IsNotif;
import com.cjy.flb.utils.MHttpUtils;
import com.cjy.flb.utils.SharedPreUtil;
import com.cjy.flb.utils.ToastUtil;
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

import java.util.Vector;

/**
 * Created by Administrator on 2015/12/7 0007.
 */
public class MedicManagerAdapter extends BaseAdapter {
//    private final Vector<BindingID> list;
    private final LayoutInflater inflater;
    private final Context context;
    /**
     * 是否操作删除
     */
    private boolean isDelete = false;

    public MedicManagerAdapter(Context context, Vector<BindingID> list) {
        this.context=context;
//        this.list = list;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return MyApplication.list.size();
    }

    @Override
    public Object getItem(int position) {
        return MyApplication.list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_media_manager_adapter, null);
            holder.mImageHead = (ImageView) convertView.findViewById(R.id.img_head);
            holder.mImageChoose = (ImageView) convertView.findViewById(R.id.img_choose);
            holder.mTextName = (TextView) convertView.findViewById(R.id.tv_name);
            holder.mTextID = (TextView) convertView.findViewById(R.id.tv_medicID);
            holder.mImageMedic = (ImageView) convertView.findViewById(R.id.trash);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final BindingID bID = MyApplication.list.get(position);
        holder.mTextName.setText(bID.getName());
        holder.mTextID.setText(bID.getMedicId());
        if (bID.getMedicId().equals(MyApplication.flbId)) {
            holder.mImageChoose.setVisibility(View.VISIBLE);
        } else {
            holder.mImageChoose.setVisibility(View.INVISIBLE);
        }
        String str = bID.getUrl();
        if (str != null) {
            byte[] bytes = Base64.decode(str, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            holder.mImageHead.setImageBitmap(bitmap);
        } else {
            Bitmap bitmap = BitmapFactory.decodeResource(MyApplication.getInstance().getResources(), R.drawable.ic_medicm_head);
            holder.mImageHead.setImageBitmap(bitmap);
        }
        if (isDelete) {
            holder.mImageMedic.setVisibility(View.VISIBLE);
            holder.mImageMedic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final RequestParams requestParams = new RequestParams();
                    String accessToken = SharedPreUtil.getString("access_token");
                    requestParams.addHeader("Authorization", "Bearer " + accessToken);
                    requestParams.addQueryStringParameter("device_uid", bID.getMedicId());
                    if (bID.getMedicId().equals(MyApplication.flbId)) {
                        ToastUtil.showShort(context.getString(R.string.switch_and_delete));
                    } else {
                        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage(context.getString(R.string.delete_box))
                                .setNegativeButton(context.getString(R.string.btn_cancel), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        builder.create().dismiss();
                                    }
                                })
                                .setPositiveButton(context.getString(R.string.delete), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        builder.create().dismiss();
                                        deleteMedicmenBox(requestParams, position, bID);
                                    }
                                }).show();
                    }
                }

            });
        } else {
            holder.mImageMedic.setVisibility(View.GONE);
        }

        return convertView;
    }

    private void deleteMedicmenBox(RequestParams requestParams, final int position, final BindingID bID) {
        MyApplication.getHttpUtils().send(HttpRequest.HttpMethod.POST, MHttpUtils.REMOVE_DEVICE,
                requestParams,
                new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        KLog.json(responseInfo.result);
                        JsonElement codeElement = null;
                        try {
                            JsonElement json = new JsonParser().parse(responseInfo.result);
                            JsonObject jsonObject = json.getAsJsonObject();
                            JsonElement jsonElement = jsonObject.get("response");
                            JsonObject object = jsonElement.getAsJsonObject();
                            codeElement = object.get("code");
                        } catch (JsonSyntaxException e) {
                            e.printStackTrace();
                        }
                        if ((codeElement != null ? codeElement.getAsInt() : 0) == 1) {
                            MyApplication.list.remove(position);
                            MyApplication.hashMap.remove(bID.getMedicId());
                            MedicineManagerActivity.adapter.notifyDataSetChanged();

                            IsNotif isNotif = MyApplication.getDaoSession().getIsNotifDao()
                                    .queryBuilder().where(IsNotifDao.Properties.Medic_id.eq(bID.getMedicId()))
                                    .build().forCurrentThread().unique();
                            EatTime eatTime = MyApplication.getDaoSession().getEatTimeDao()
                                    .queryBuilder().where(EatTimeDao.Properties.Medic_id.eq(bID.getMedicId()))
                                    .build().forCurrentThread().unique();

                            if (isNotif != null) {
                                MyApplication.getDaoSession().getIsNotifDao().deleteByKeyInTx(isNotif.getId());
                            }
                            if(eatTime!=null){
                                MyApplication.getDaoSession().getEatTimeDao().deleteByKeyInTx(eatTime.getId());
                            }
                        }
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        e.printStackTrace();

                        KLog.d(s+"==="+ e.getMessage());
                        KLog.d(e.getExceptionCode());
                        KLog.d(e.getCause());
                        KLog.d(e.getLocalizedMessage());
                        KLog.d(e.getStackTrace());
                        ToastUtil.showShort(context.getString(R.string.submit_error));
                    }
                });
    }

    public void setCanDelete(boolean b) {
        isDelete = b;
    }

    class ViewHolder {
        ImageView mImageHead;
        ImageView mImageChoose;
        TextView mTextName;
        TextView mTextID;
        ImageView mImageMedic;
    }
}
