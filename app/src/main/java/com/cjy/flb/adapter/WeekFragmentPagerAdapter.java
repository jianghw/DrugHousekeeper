package com.cjy.flb.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by Administrator on 2015/12/2 0002.
 */
public class WeekFragmentPagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> fragmentList;

    public WeekFragmentPagerAdapter(FragmentManager supportFragmentManager, List<Fragment> fragmentList) {
        super(supportFragmentManager);
        this.fragmentList = fragmentList;
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    private final String[] titles = { "", "", "" };

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }
}
