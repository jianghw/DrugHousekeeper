package com.cjy.flb.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cjy.flb.R;
import com.cjy.flb.activity.GuideActivity;
import com.cjy.flb.manager.AppManager;
import com.socks.library.KLog;

import java.util.ArrayList;

/**
 * 项目名称：
 * 类描述：引导页小圆点适配器
 * 创建人：
 * 创建时间：2015/10/9 0009 9:05
 * 修改人：
 * 修改时间：
 * 修改备注：
 * version:
 */
public class GuidePagerAdapter extends PagerAdapter {
    private static final String SHAREDPREFERENCES_NAME = "first_sharedpref";
    //界面列表
    private final ArrayList<View> views;
    private final Activity activity;

    public GuidePagerAdapter(ArrayList<View> views, Activity activity) {
        this.views = views;
        this.activity = activity;
    }

    //获取当前页面数
    @Override
    public int getCount() {
        if (views != null) {
            return views.size();
        }
        return 0;
    }

    // 判断是否由对象生成界面
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == object);
    }

    /**
     * 初始化position位置的界面item
     *
     * @param container
     * @param position
     * @return
     */
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(views.get(position), 0);

        //TODO
    /*    if (position == views.size() - 1) {
            TextView imageButton = (TextView) container.findViewById(R.id.tv_back);
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 设置为已经引导
                    //                    setGuided();
                    //                    goHome();

                    AppManager.getAppManager().finishActivity(GuideActivity.class);
                }
            });
        }*/
        //        return super.instantiateItem(container, position);
        return views.get(position);
    }

    //设置已经引导过了，下次启动不用再次引导
    private void setGuided() {
        SharedPreferences preferences = activity.getSharedPreferences(SHAREDPREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        // 存入数据
        editor.putBoolean("isFirstIn", false);
        // 提交修改
        editor.commit();
    }

    private void goHome() {
      /*  Intent intent = new Intent(activity, MainActivity.class);
        activity.startActivity(intent);
        activity.finish();*/
    }

    /**
     * 销毁position位置的界面
     *
     * @param container
     * @param position
     * @param object
     */
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        //        super.destroyItem(container, position, object);
        container.removeView(views.get(position));
    }

    @Override
    public void finishUpdate(ViewGroup container) {
        //        super.finishUpdate(container);
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
        //        super.restoreState(state, loader);
    }

    @Override
    public Parcelable saveState() {
        //        return super.saveState();
        return null;
    }

    @Override
    public void startUpdate(ViewGroup container) {
        //        super.startUpdate(container);
    }
}
