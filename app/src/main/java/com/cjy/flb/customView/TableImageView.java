package com.cjy.flb.customView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * 项目名称：
 * 类描述：Buttom 子控件中的图片按键
 * 创建人：
 * 创建时间：2015/8/31 0031 17:03
 * 修改人：Administrator
 * 修改时间：2015/8/31 0031 17:03
 * 修改备注：
 */
public class TableImageView extends ImageView{
    private Bitmap clickdeBitmap;
    private Bitmap unclickedBitmap;
    private Paint mPaint;
    private int mAlpha;

    public TableImageView(Context context) {
        this(context,null,0);
    }

    public TableImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TableImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mAlpha=0;
    }

    /**
     * 传递图片资源过来
     * @param clickedDrawableRid R.layout.xxx
     * @param unclickedDrawableRid
     */
    public void initBitmap(int clickedDrawableRid,int unclickedDrawableRid){
        //点击的图片
        clickdeBitmap = BitmapFactory.decodeResource(getResources(),clickedDrawableRid);
        //未点击的图片
        unclickedBitmap=BitmapFactory.decodeResource(getResources(),unclickedDrawableRid);
        /**
         * 设置控件布局参数
         */
        setLayoutParams(new LinearLayout.LayoutParams(clickdeBitmap.getWidth(),clickdeBitmap.getHeight()));
        mPaint=new Paint(Paint.ANTI_ALIAS_FLAG);
        mAlpha=0;
    }

    public void setmAlpha(int alpha){
        mAlpha=alpha;
        invalidate(); //重新调用onDraw方法
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(mPaint!=null){
            mPaint.setAlpha(255-mAlpha);
            canvas.drawBitmap(unclickedBitmap,null,new Rect(0,0,unclickedBitmap.getWidth(),unclickedBitmap.getHeight()),mPaint);
            mPaint.setAlpha(mAlpha);
            canvas.drawBitmap(clickdeBitmap,null,new Rect(0,0,clickdeBitmap.getWidth(),clickdeBitmap.getHeight()),mPaint);
        }
    }
}
