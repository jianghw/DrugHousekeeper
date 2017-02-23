package com.cjy.flb.customView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cjy.flb.R;

/**
 * Created by Administrator on 2015/12/1 0001.
 */
public class isWidgetToSet extends LinearLayout {
    private TextView text_0;
    private TextView text_1;
    private TextView text_2;
    private TextView text_3;
    private ImageView leftImg;
    private ImageView rightImg;

    public isWidgetToSet(Context context) {
        this(context, null, 0);
    }

    public isWidgetToSet(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public isWidgetToSet(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.isWidgetToSet, defStyleAttr, 0);
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.widget_fragment_set, this);

        leftImg = (ImageView) view.findViewById(R.id.imageView2);
        rightImg = (ImageView) view.findViewById(R.id.imageView3);
        text_0 = (TextView) view.findViewById(R.id.textView);
        text_1 = (TextView) view.findViewById(R.id.textView2);
        text_2 = (TextView) view.findViewById(R.id.textView3);
        text_3 = (TextView) view.findViewById(R.id.textView4);

        int count = typedArray.getIndexCount();
        for (int i = 0; i < count; i++) {
            int attr = typedArray.getIndex(i);
            switch (attr) {
                case R.styleable.isWidgetToSet_image_src:
                    Drawable drawableL = typedArray.getDrawable(attr);
                    leftImg.setImageDrawable(drawableL);
                    break;
                case R.styleable.isWidgetToSet_image_src_2:
                    Drawable drawableR = typedArray.getDrawable(attr);
                    rightImg.setImageDrawable(drawableR);
                    break;
                case R.styleable.isWidgetToSet_text_str:
                    String str=typedArray.getString(attr);
                    text_1.setText(str);
                    text_0.setText(str);
                    break;
                case R.styleable.isWidgetToSet_text_str_2:
                    String str_2=typedArray.getString(attr);
                    text_2.setText(str_2);
                    break;
                case R.styleable.isWidgetToSet_text_str_3:
                    String str_3=typedArray.getString(attr);
                    text_3.setText(str_3);
                    break;
                case R.styleable.isWidgetToSet_invisible:
                    boolean isVisible=typedArray.getBoolean(attr,false);
                    if(isVisible){
                        text_3.setVisibility(View.INVISIBLE);
                        rightImg.setVisibility(View.INVISIBLE);
                    }
                    break;
                case R.styleable.isWidgetToSet_text_hid_2:
                    String hid=typedArray.getString(attr);
                    text_2.setText("");
                    text_2.setTextColor(Color.parseColor("#a0a0a0"));
                    text_2.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
                    text_2.setText(hid);
                    break;
                case R.styleable.isWidgetToSet_text_gone:
                    boolean isGone=typedArray.getBoolean(attr,false);
                    if(isGone){
                        text_2.setVisibility(View.INVISIBLE);
                        text_3.setVisibility(View.INVISIBLE);
                    }
                    break;
                case R.styleable.isWidgetToSet_text_only:
                    boolean isOnly=typedArray.getBoolean(attr,false);
                    if(isOnly){
                        text_0.setVisibility(View.VISIBLE);
                        text_1.setVisibility(View.GONE);
                        text_2.setVisibility(View.GONE);
                        text_3.setVisibility(View.GONE);
                        rightImg.setVisibility(View.VISIBLE);
                    }
                    break;
            }
        }
        typedArray.recycle();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
/*        switch(ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                view.setBackgroundColor(getResources().getColor(R.color.all_row_bg));
                break;
        }*/
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }

    public TextView getText_1() {
        return text_1;
    }

    public void setText_1(TextView text_1) {
        this.text_1 = text_1;
    }

    public TextView getText_2() {
        return text_2;
    }

    public void setText_2(TextView text_2) {
        this.text_2 = text_2;
    }

    public TextView getText_3() {
        return text_3;
    }

    public void setText_3(TextView text_3) {
        this.text_3 = text_3;
    }

    public ImageView getLeftImg() {
        return leftImg;
    }

    public void setLeftImg(ImageView leftImg) {
        this.leftImg = leftImg;
    }

    public ImageView getRightImg() {
        return rightImg;
    }

    public void setRightImg(ImageView rightImg) {
        this.rightImg = rightImg;
    }
}
