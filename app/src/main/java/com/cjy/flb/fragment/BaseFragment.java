package com.cjy.flb.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.cjy.flb.R;
import com.cjy.flb.activity.MyApplication;
import com.google.gson.JsonArray;
import com.socks.library.KLog;

import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 2015/12/1 0001.
 */
public abstract class BaseFragment extends Fragment {

    public static JsonArray jsonArray = null;
    protected LoadingPager loadingPage;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        KLog.i("TAG", this + "onCreate");
    }


    /**
     * 请求服务器 获取当前状态
     */
    protected abstract LoadingPager.LoadResult load();

    protected abstract View setView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    protected abstract LoadingPager.LoadResult readDatabase();

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        KLog.i("TAG", this + "onCreateView");
        loadingPage = new LoadingPager(MyApplication.getInstance(), R.layout.loadpage_loading, 0, 0) {
            @Override
            protected LoadResult loadingData()//下载数据，写入数据库
            {
                return BaseFragment.this.load();
            }

            @Override
            protected LoadResult readDatabase()//读取数据库
            {
                return BaseFragment.this.readDatabase();
            }

            @Override
            protected View createSuccessView() {
                return BaseFragment.this.setView(inflater, container, savedInstanceState);
            }
        };

        // 移除frameLayout之前的爹
        //            removeSelfFromParent(loadingPage);

        return loadingPage;
    }

    protected void removeSelfFromParent(LoadingPager view) {
        // 先找到父类，再通过父类移除孩子
        if (view != null) {
            ViewParent parent = view.getParent();
            if (parent != null && parent instanceof ViewGroup) {
                ViewGroup group = (ViewGroup) parent;
                group.removeView(view);
            }
        }
    }

    public void show() {
        if (loadingPage != null) {
            loadingPage.show();
        }
    }

    public int getCurrentState() {
        return loadingPage != null ? getCurrentState() : 2;
    }

    public void intent(Class<?> cls) {
        startActivity(new Intent(getActivity(), cls));
    }

    protected void intent(Class<?> cls, Map<String, String> map) {
        Set<String> set = map.keySet();
        Intent intent = new Intent(getActivity(), cls);
        for (String key : set) {
            intent.putExtra(key, map.get(key));
        }
        startActivity(intent);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        KLog.i("TAG", this + "onActivityCreated");
    }

    @Override
    public void onStart() {
        super.onStart();
        KLog.i("TAG", this + "onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        KLog.i("TAG", this + "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        KLog.i("TAG", this + "onDestroyView");
    }

    @Override
    public void onDestroy() {
        if (jsonArray != null) jsonArray = null;
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        KLog.i("TAG", this + "onDetach");
    }
}