package com.cjy.flb.utils;

import android.content.Context;
import android.widget.Toast;

import com.cjy.flb.activity.MyApplication;

/**
 * Created by Administrator on 2015/8/31 0031.
 */
public class ToastUtil
{

    private ToastUtil()
    {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    public static boolean isShow = true;

    private static Context getContext()
    {
        return MyApplication.getInstance();
    }

    /**
     * 自定义显示Toast时间
     *  @param message
     * @param duration
     */
    public static void show(CharSequence message, int duration)
    {
        if (isShow)
            Toast.makeText(getContext(), message, duration).show();
    }

    /**
     * 短时间显示Toast
     *
     * @param message
     */
    public static void showShort(CharSequence message)
    {
        if (isShow)
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    public static void showLong(CharSequence message)
    {
        if (isShow)
            Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }

}
