package com.cjy.flb.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cjy.flb.R;
import com.cjy.flb.adapter.GuidePagerAdapter;
import com.cjy.flb.customView.DotView;
import com.cjy.flb.manager.AppManager;

import java.util.ArrayList;

/**
 * 项目名称：
 * 类描述：引导界面
 * 创建人：Administrator
 * 创建时间：2015/10/8 0008 16:48
 * 修改人：
 * 修改时间：
 * 修改备注：
 * version:
 */
public class GuideActivity extends BaseActivity implements ViewPager.OnPageChangeListener {

    private ArrayList<View> views;
    private GuidePagerAdapter vpAdapter;
    private ViewPager vp;
    //底部小圆点图片
    private DotView[] dots;
    //记录当前选中位置
    private int currentIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
 /*       setContentView(R.layout.activity_guide);
        //初始化页面
        initViews();
        //初始化底部小圆点
        initDots();*/
    }

    @Override
    public void initView() {
        setContentView(R.layout.activity_guide);
        //初始化页面
        initViews();
        //初始化底部小圆点
        initDots();
    }

    @Override
    public void initData() {

    }

    @Override
    public void initListener() {

    }

    private void initViews() {
        LayoutInflater inflater = LayoutInflater.from(this);
        views = new ArrayList<View>();
        //初始化引导图片列表
        views.add(inflater.inflate(R.layout.what_new_one, null));
        views.add(inflater.inflate(R.layout.what_new_two, null));
        views.add(inflater.inflate(R.layout.what_new_three, null));
        //        views.add(inflater.inflate(R.layout.what_new_four, null));
        //初始化Adapter
        vpAdapter = new GuidePagerAdapter(views, this);
        vp = (ViewPager) findViewById(R.id.viewpager);
        vp.setAdapter(vpAdapter);
        //绑定回调
        vp.addOnPageChangeListener(this);

            TextView imageButton = (TextView)findViewById(R.id.tv_back);
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppManager.getAppManager().finishActivity(GuideActivity.class);
                }
            });
    }

    private void initDots() {
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.ll_guid_dot);
        dots = new DotView[views.size()];
        //循环获得小点图片
        for (int i = 0; i < views.size(); i++) {
            dots[i] = (DotView) linearLayout.getChildAt(i);
            // 都设为灰色
            dots[i].setIsSelected(false);
        }
        currentIndex = 0;
        // 设置为白色，即选中状态
        dots[currentIndex].setIsSelected(true);
    }

    //当前页面被滑动时调用
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    // 当新的页面被选中时调用
    @Override
    public void onPageSelected(int position) {
        // 设置底部小点选中状态
        setCurrentDot(position);
    }

    // 当滑动状态改变时调用
    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private void setCurrentDot(int position) {
        if (position < 0 || position > views.size() - 1 || currentIndex == position) {
            return;
        }
        dots[currentIndex].setIsSelected(false);
        dots[position].setIsSelected(true);

        currentIndex = position;
    }

    // 不可点击返回键
/*    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }*/
}
