package com.cjy.flb.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.cjy.flb.R;
import com.cjy.flb.adapter.WeekFragmentPagerAdapter;
import com.cjy.flb.customView.PagerSlidingTabStrip;
import com.cjy.flb.event.WeekDateEven;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2015/12/2 0002.
 * 用于显示 上周 本周 下周
 */
public class WeekFragment extends BaseFragment {

    @Bind(R.id.viewpager_weekfrment)
    ViewPager mViewpagerWeekfrment;
    @Bind(R.id.tabstrip_weekfrment)
    PagerSlidingTabStrip mTabstripWeekfrment;

    /**
     * 获取当前屏幕的密度
     */
    private DisplayMetrics dm;
    private ArrayList<Fragment> fragmentList = new ArrayList<>();

    @Override
    protected LoadingPager.LoadResult load()
    {
        return LoadingPager.LoadResult.WRITE;
    }

    @Override
    protected View setView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_week, container, false);
        ButterKnife.bind(this, view);
        if (isAdded()) {
            dm = getResources().getDisplayMetrics();
            initListener();
        }
        return view;
    }

    @Override
    protected LoadingPager.LoadResult readDatabase()
    {
        if (fragmentList.size() == 0) {
            fragmentList.add(new LastWeekFragment());
            fragmentList.add(new CWeekFragment());
            fragmentList.add(new NextWeekFragment());
        }
        mViewpagerWeekfrment.setAdapter(new WeekFragmentPagerAdapter(
                getChildFragmentManager(), fragmentList));
        mViewpagerWeekfrment.setCurrentItem(1);

        mTabstripWeekfrment.setViewPager(mViewpagerWeekfrment);
        setTabsValue();
        return LoadingPager.LoadResult.READ;
    }

    private void setTabsValue()
    {
        // 设置Tab是自动填充满屏幕的
        mTabstripWeekfrment.setShouldExpand(true);
        // 设置Tab的分割线是透明的
        mTabstripWeekfrment.setDividerColor(Color.TRANSPARENT);
        // 设置Tab底部线的高度
        mTabstripWeekfrment.setUnderlineHeight((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 4, dm));
        // 设置Tab Indicator的高度
        mTabstripWeekfrment.setIndicatorHeight((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 4, dm));
        //指示器的颜色与底部线的颜色
        mTabstripWeekfrment.setIndicatorColor(Color.parseColor("#03caca"));
        mTabstripWeekfrment.setUnderlineColor(Color.parseColor("#b6f9f9"));
        // 取消点击Tab时的背景色
        mTabstripWeekfrment.setTabBackground(0);
    }

    public void initListener()
    {
        mViewpagerWeekfrment.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        NoteFragment f = (NoteFragment) getParentFragment();
                        f.setEnabled(false);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        NoteFragment f2 = (NoteFragment) getParentFragment();
                        f2.setEnabled(true);
                        break;
                }
                return false;
            }
        });
    }

    @Override
    public void onDestroyView()
    {
        //删除事件缓存
        EventBus.getDefault().removeStickyEvent(WeekDateEven.class);
        if (fragmentList.size()>0) fragmentList.clear();
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

}
