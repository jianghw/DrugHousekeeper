package com.cjy.flb.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cjy.flb.R;
import com.cjy.flb.activity.MainActivity;
import com.cjy.flb.adapter.WeekFragmentPagerAdapter;
import com.cjy.flb.customView.PagerSlidingTabStrip;
import com.cjy.flb.utils.MHttpUtils;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2015/12/2 0002.
 * 用于显示 上月 本月 下月
 */
public class MonthFragment extends BaseFragment {

    @Bind(R.id.viewpager_monthfrment)
    ViewPager mViewpagerMonthfrment;
    @Bind(R.id.tabstrip_weekfrment)
    PagerSlidingTabStrip mTabstripWeekfrment;
    @Bind(R.id.textView_date)
    TextView mTextViewDate;
    @Bind(R.id.ll_webView)
    LinearLayout mLlWebView;
    /**
     * 获取当前屏幕的密度
     */
    private DisplayMetrics dm;
    private ArrayList<Fragment> fragmentList = new ArrayList<>();
    @Override
    protected LoadingPager.LoadResult load() {
        return LoadingPager.LoadResult.WRITE;
    }

    @Override
    public View setView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_month, container, false);
        dm = getResources().getDisplayMetrics();
        ButterKnife.bind(this, view);
        if (isAdded()) {
            initListener();
        }
        return view;
    }

    public LoadingPager.LoadResult readDatabase() {

        if (fragmentList.size() == 0) {
            fragmentList.add(new LastMonthFragment());
            fragmentList.add(new CMonthFragment());
        }
        mViewpagerMonthfrment.setAdapter(new WeekFragmentPagerAdapter(
                getChildFragmentManager(), fragmentList));
        mViewpagerMonthfrment.setCurrentItem(1);

        mTabstripWeekfrment.setViewPager(mViewpagerMonthfrment);
        setTabsValue();
        return LoadingPager.LoadResult.READ;
    }

    private void setTabsValue() {
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

    public void initListener() {
        mLlWebView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = MHttpUtils.CJYUN_URL;
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent .setData(Uri.parse(url));
                startActivity(intent);
            }
        });

        mViewpagerMonthfrment.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            //表示在前一个页面滑动到后一个页面的时候，在前一个页面滑动前调用的方法
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            //arg0是表示你当前选中的页面，这事件是在你页面跳转完毕的时候调用的
            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        LastMonthFragment f = (LastMonthFragment) fragmentList.get(position);
                        mTextViewDate.setText(String.valueOf(f.getAbnormalNumberOfDays()));
                        break;
                    case 1:
                        CMonthFragment f1 = (CMonthFragment) fragmentList.get(position);
                        mTextViewDate.setText(String.valueOf(f1.getAbnormalNumberOfDays()));
                        break;
                }
            }

            //arg0 ==1的时候表示正在滑动，arg0==2的时候表示滑动完毕了
            //arg0==0的时候表示什么都没做，就是停在那
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        mViewpagerMonthfrment.setOnTouchListener(new View.OnTouchListener() {
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

    public void setTextViewDate(String s) {
        mTextViewDate.setText(s);
    }

    @Override
    public void onDestroyView() {
        if (fragmentList.size()>0) fragmentList.clear();
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

}
