package com.cjy.flb.customView;

import android.content.Context;

import android.support.v4.view.ActionProvider;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;

import com.cjy.flb.R;

/**
 * Created by Administrator on 2015/11/30 0030.
 */
public class PlusActionProvider extends ActionProvider {
    private Context context;

    /**
     * Creates a new instance.
     *
     * @param context Context for accessing resources.
     */
    public PlusActionProvider(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public View onCreateActionView() {
        return null;
    }

    @Override
    public void onPrepareSubMenu(SubMenu subMenu) {

        subMenu.clear();
        subMenu.add(context.getString(R.string.more))
                .setIcon(R.drawable.toolbar_move_add)
                .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        return false;
                    }
                });
        subMenu.add(context.getString(R.string.more))
                .setIcon(R.drawable.toolbar_more_handover)
                .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        return false;
                    }
                });
        subMenu.add(context.getString(R.string.more))
                .setIcon(R.drawable.toolbar_more_call)
                .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        return false;
                    }
                });
        subMenu.add(context.getString(R.string.more))
                .setIcon(R.drawable.toolbar_more_alter)
                .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        return false;
                    }
                });

    }

    @Override
    public boolean hasSubMenu() {
        return true;
    }
}
