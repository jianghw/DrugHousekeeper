package com.cjy.flb.customView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cjy.flb.R;
import com.cjy.flb.activity.MainActivity;


/**
 * 项目名称：
 * 类描述：主界面底部标签栏
 * 创建人：jianghw
 * 修改备注：
 */
public class UITableBottom extends LinearLayout implements View.OnClickListener {
    private Context context;
    private int colorClick;
    private int colorUnclick;
    //子控件
    private UITableItem table_0;
    private UITableItem table_1;
    private UITableItem table_2;
    private UITableItem table_3;

    private int index = 5;
    private int currentPosition = 0;
    private OnUITabChangListener changeListener; //ui Tab 改变监听器
    private MainActivity activity;


    public UITableBottom(Context context) {
        this(context, null, 0);
    }

    public UITableBottom(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UITableBottom(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init() {
        colorClick = getResources().getColor(R.color.select);
        colorUnclick = getResources().getColor(R.color.unselect);
        /**拿控件的高度*/
        int tableBottomHeight = ViewGroup.LayoutParams.MATCH_PARENT;
        //设置父类控件的方向
        setOrientation(LinearLayout.HORIZONTAL);
        /**创建子控件，并标记*/
        table_0 = getChileItem(tableBottomHeight, 0);
        table_1 = getChileItem(tableBottomHeight, 1);
        table_2 = getChileItem(tableBottomHeight, 2);
        table_3 = getChileItem(tableBottomHeight, 3);
        selectTab(currentPosition);
    }

    /**
     * 建立布局子控件Item
     *
     * @param tableBottomHeight 父类控件高度
     * @param i                 子控件位号
     * @return zi控件对象
     */
    private UITableItem getChileItem(int tableBottomHeight, int i) {

        UITableItem table = newChildItem(i);
        //用于建立控件，所用参数
        LayoutParams layoutParams = new LayoutParams(0, tableBottomHeight);
        layoutParams.weight = 1;

        if (i == currentPosition) {
            table.labelView.setTextColor(colorClick);
        } else {
            table.labelView.setTextColor(colorUnclick);
        }
        switch (i) {
            case 0:
                table.labelView.setText(context.getString(R.string.medical_home));
                table.iconView.initBitmap(R.drawable.bottom_fulebo_sele, R.drawable.bottom_fulebo_unsele);
                break;
            case 1:
                table.labelView.setText(context.getString(R.string.setting));
                table.iconView.initBitmap(R.drawable.bottom_setting_sele, R.drawable.bottom_setting_unsele);
                break;
            case 2:
                table.labelView.setText(context.getString(R.string.record));
                table.iconView.initBitmap(R.drawable.bottom_note_sele, R.drawable.bottom_note_unsele);
                break;
            case 3:
                table.labelView.setText(context.getString(R.string.me));
                table.iconView.initBitmap(R.drawable.bottom_me_sele, R.drawable.bottom_me_unsele);
                break;
            default:
                break;
        }

        /**
         *加入到父控件中
         */
        addView(table.parent, layoutParams);
        return table;
    }

    /**
     * Button控件 子item包含组件
     *
     * @param i 标记位号
     * @return
     */
    private UITableItem newChildItem(int i) {
        UITableItem tableItem = new UITableItem();
        tableItem.parent = LayoutInflater.from(getContext()).inflate(R.layout.item_buttom_main, null);
        tableItem.iconView = (TableImageView) tableItem.parent.findViewById(R.id.mTabIcon);
        tableItem.labelView = (TextView) tableItem.parent.findViewById(R.id.mTabText);

        tableItem.parent.setTag(i);
        tableItem.parent.setOnClickListener(this);
        return tableItem;
    }

    @Override
    public void onClick(View v) {
        int i = (Integer) v.getTag();
        //跳转到ViewPager的指定页面
        activity.setCurrentItem(i, false);
        selectTab(i);
    }

    /**
     * OnPagerChangListener 时会被调用
     *
     * @param i
     */
    public void selectTab(int i) {
        if (index == i) {
            return;
        }
        index = i;
        if (changeListener != null) {
            changeListener.onTabChang(index);
        }
        //mIndex表示处于mIndex到mIndex+1页面之间
        switch (index) {
            case 0:
                table_0.iconView.setmAlpha(255);
                table_1.iconView.setmAlpha(0);
                table_2.iconView.setmAlpha(0);
                table_3.iconView.setmAlpha(0);
                table_0.labelView.setTextColor(colorClick);
                table_1.labelView.setTextColor(colorUnclick);
                table_2.labelView.setTextColor(colorUnclick);
                table_3.labelView.setTextColor(colorUnclick);
                break;
            case 1:
                table_0.iconView.setmAlpha(0);
                table_1.iconView.setmAlpha(255);
                table_2.iconView.setmAlpha(0);
                table_3.iconView.setmAlpha(0);
                table_0.labelView.setTextColor(colorUnclick);
                table_1.labelView.setTextColor(colorClick);
                table_2.labelView.setTextColor(colorUnclick);
                table_3.labelView.setTextColor(colorUnclick);
                break;
            case 2:
                table_0.iconView.setmAlpha(0);
                table_1.iconView.setmAlpha(0);
                table_2.iconView.setmAlpha(255);
                table_3.iconView.setmAlpha(0);
                table_0.labelView.setTextColor(colorUnclick);
                table_1.labelView.setTextColor(colorUnclick);
                table_2.labelView.setTextColor(colorClick);
                table_3.labelView.setTextColor(colorUnclick);
                break;
            case 3:
                table_0.iconView.setmAlpha(0);
                table_1.iconView.setmAlpha(0);
                table_2.iconView.setmAlpha(0);
                table_3.iconView.setmAlpha(255);
                table_0.labelView.setTextColor(colorUnclick);
                table_1.labelView.setTextColor(colorUnclick);
                table_2.labelView.setTextColor(colorUnclick);
                table_3.labelView.setTextColor(colorClick);
                break;
            default:
                break;
        }
    }

    /**
     * UITab 改变监听器
     */
    private static interface OnUITabChangListener {
        void onTabChang(int index);
    }

    public OnUITabChangListener getChangeListener() {
        return changeListener;
    }

    public void setChangeListener(OnUITabChangListener changeListener) {
        this.changeListener = changeListener;
    }

    public void setmViewPager(MainActivity activity, int currentPosition) {
        this.activity = activity;
        this.currentPosition = currentPosition;
        selectTab(currentPosition);
    }
}
