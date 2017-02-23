package com.cjy.flb.customView;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by Administrator on 2015/12/14 0014.
 */
public class MyFrameLayout extends FrameLayout {
    private int with;
    private int height;

    public MyFrameLayout(Context context) {
        super(context);
    }

    public MyFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        with=getHeight();
        height=getHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // 建立Paint 物件
        Paint paint1 = new Paint();
        paint1.setStyle(Paint.Style.FILL);
        // 设定颜色
        paint1.setColor(Color.GREEN);
        // 设定阴影(柔边, X 轴位移, Y 轴位移, 阴影颜色)
        paint1.setShadowLayer(500, 3, 3, 0xFFFF00FF);
        // 实心矩形& 其阴影
        canvas.drawRect(0, 0, with, height, paint1);
        super.onDraw(canvas);

    }
}
