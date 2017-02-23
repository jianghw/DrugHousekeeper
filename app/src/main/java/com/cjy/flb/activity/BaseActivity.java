package com.cjy.flb.activity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cjy.flb.R;
import com.cjy.flb.manager.AppManager;
import com.cjy.flb.qrcode.capture.CaptureActivity;
import com.cjy.flb.utils.SharedPreUtil;

import java.util.Map;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;

/**
 * @author hxm
 * @version 1.0
 * @data 2015-11-23 下午3:04:10
 */
public abstract class BaseActivity extends AppCompatActivity {
    // 配置信息
    public Context context;

    //顶部标题栏信息
    private TextView tvTitle;
    private ImageView imgScan;
    private ImageView imgSeparator;
    protected TextView tvMore;
    protected LinearLayout llTopBack;

    @Override
    public void setContentView(int layoutResID) {
        int mode = SharedPreUtil.getInteger("Theme_Font", 2);
        if (mode == 1) {
            this.setTheme(R.style.Theme_Small);
        } else if (mode == -1 || mode == 2) {
            this.setTheme(R.style.Theme_Medium);
        } else if (mode == 3) {
            this.setTheme(R.style.Theme_Large);
        }

       /* // 在这里我们获取了主题暗色，并设置了status bar的颜色
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorPrimaryDark, typedValue, true);
        int color = typedValue.data;

        // 注意setStatusBarBackgroundColor方法需要你将fitsSystemWindows设置为true才会生效
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.my_drawer_layout);
        drawerLayout.setStatusBarBackgroundColor(color);*/

        super.setContentView(layoutResID);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        context = this;
        AppManager.getAppManager().addActivity(this);
        //注解框架会执行这里的this  如有
        initView();
        initData();
        initListener();
    }


    /**
     * 初始化view
     */
    public abstract void initView();

    /**
     * 初始化数据
     */
    public abstract void initData();

    /**
     * 初始化监听事件
     */
    public abstract void initListener();

    /**
     * 跳转到下一个Activity
     *
     * @param cls 将要跳转的Class
     */
    public void toActivity(Class<?> cls) {
        Intent intent = new Intent();
        intent.setClass(context, cls);
        startActivity(intent);
    }

    /**
     * 设置顶部标题栏
     *
     * @param content 标题栏内容
     * @param img     是否显示图标
     * @param more    是否显示文字
     */
    public void initBar(String content, boolean img, boolean more) {

        llTopBack = (LinearLayout) findViewById(R.id.ll_top_back);
        tvTitle = (TextView) findViewById(R.id.tv_top_title);
        imgScan = (ImageView) findViewById(R.id.img_top_scan);
        imgSeparator = (ImageView) findViewById(R.id.img_toolbar_separator);
        tvMore = (TextView) findViewById(R.id.tv_top_more);
        //点击时返回上一界面
        llTopBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             finish();
            }
        });
        tvTitle.setText(content);
        if (img) {
            imgScan.setVisibility(View.VISIBLE);
            imgSeparator.setVisibility(View.VISIBLE);
            imgScan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //跳转到扫描二维码界面
                    Intent openCameraIntent = new Intent(context, CaptureActivity.class);
                    startActivityForResult(openCameraIntent, 0);
                }
            });
        }
        if (more) {
            tvMore.setVisibility(View.VISIBLE);
            imgSeparator.setVisibility(View.VISIBLE);
        }
    }


    /**
     * 意图
     *
     * @param clazz
     */
    protected void intent(Class<?> clazz) {
        startActivity(new Intent(this, clazz));
    }

    protected void intent(Class<?> cls, Map<String, String> map) {
        Set<String> set = map.keySet();
        Intent intent = new Intent(this, cls);
        for (String key : set) {
            intent.putExtra(key, map.get(key));
        }
        startActivity(intent);
    }



    @Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.getAppManager().finishActivity(this);
    }

}
