package com.cjy.flb.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cjy.flb.R;
import com.cjy.flb.activity.GeneralActivity;
import com.cjy.flb.activity.MProblemActivity;
import com.cjy.flb.activity.MedicineManagerActivity;
import com.cjy.flb.activity.MyApplication;
import com.cjy.flb.utils.MHttpUtils;
import com.cjy.flb.utils.SharedPreUtil;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseStream;
import com.lidroid.xutils.http.client.HttpRequest;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2015/12/1 0001.
 */
public class MeFragment2 extends BaseFragment implements View.OnClickListener {

    @Bind(R.id.img_head_mefra)
    ImageView mImgHeadMefra;
    @Bind(R.id.tv_me_account)
    TextView mTvMeAccount;
    @Bind(R.id.tv_me_email)
    TextView mTvMeEmail;
    @Bind(R.id.textView_account)
    LinearLayout mTextViewAccount;
    @Bind(R.id.textView_medicine_manager)
    RelativeLayout mTextViewMedicineManager;
    @Bind(R.id.textView_problems_medic)
    RelativeLayout mTextViewProblemsMedic;
    @Bind(R.id.img_mymeal_new)
    ImageView mImgMymealNew;
    @Bind(R.id.textView_my_meal)
    RelativeLayout mTextViewMyMeal;
    @Bind(R.id.img_general_new)
    ImageView mImgGeneralNew;
    @Bind(R.id.textView_general)
    RelativeLayout mTextViewGeneral;

    @Override
    protected View setView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_me_2, container, false);
        ButterKnife.bind(this, view);
        if (isAdded()) {
            initListener();
        }
        return view;
    }

    @Override
    protected LoadingPager.LoadResult readDatabase()
    {
        String name = SharedPreUtil.getString("login_name");
        mTvMeAccount.setText(name);
        if (MyApplication.Email != null) {
            mTvMeEmail.setText("E-mail: " + MyApplication.Email);
        } else {
            mTvMeEmail.setText("E-mail: ");
        }
        try {
            if (MyApplication.head != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(MyApplication.head, 0, MyApplication.head.length);
                mImgHeadMefra.setImageBitmap(bitmap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        boolean isGeneral = SharedPreUtil.getBoolean("general", false);
        if (isGeneral) {
            mImgGeneralNew.setVisibility(View.GONE);
        }
        return LoadingPager.LoadResult.READ;
    }

    public void initListener()
    {
        mTextViewMedicineManager.setOnClickListener(this);
        mTextViewProblemsMedic.setOnClickListener(this);
        mTextViewMyMeal.setOnClickListener(this);
        mTextViewGeneral.setOnClickListener(this);
    }

    @Override
    public void onDestroyView()
    {
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId()) {
            case R.id.textView_medicine_manager://药盒管理
                Intent intentMedic = new Intent(getActivity(), MedicineManagerActivity.class);
                startActivity(intentMedic);
                break;
            case R.id.textView_problems_medic://药物反馈
                Intent intentProblemM = new Intent(getActivity(), MProblemActivity.class);
                startActivity(intentProblemM);
                break;
            case R.id.textView_my_meal://我的套餐

                break;
            case R.id.textView_general://通用
                SharedPreUtil.setBoolean("general", true);
                Intent intentUS = new Intent(getActivity(), GeneralActivity.class);
                startActivity(intentUS);
                mImgGeneralNew.setVisibility(View.GONE);
                break;
            default:break;
        }
    }

    /**
     * email 数据
     *
     * @return
     */
    @Override
    protected LoadingPager.LoadResult load()
    {
        RequestParams requestParams = new RequestParams();
        String accessToken = SharedPreUtil.getString("access_token");
        requestParams.addHeader("Authorization", "Bearer " + accessToken);
        HttpUtils httpUtils = new HttpUtils();
        httpUtils.configCurrentHttpCacheExpiry(0);
        try {
            ResponseStream responseStream = httpUtils.sendSync(HttpRequest.HttpMethod.GET,
                    MHttpUtils.FIND_EMAIL, requestParams);
            String entity = responseStream.readString();
            JsonElement json = new JsonParser().parse(entity);
            JsonElement jsonElement = null;
            if (json.isJsonObject()) {
                JsonObject jsonObject = json.getAsJsonObject();
                jsonElement = jsonObject.get("response");
            }
            if (jsonElement != null && jsonElement.isJsonObject()) {
                //保存数据库
                JsonObject jObject = jsonElement.getAsJsonObject();
                JsonElement code = jObject.get("email");
                MyApplication.Email = code.getAsString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return LoadingPager.LoadResult.WRITE;
    }
}
