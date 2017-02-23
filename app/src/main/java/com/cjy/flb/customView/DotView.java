package com.cjy.flb.customView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.cjy.flb.R;

public class DotView extends View {

	private boolean isInit = false;
	private boolean isSelected = false;
	private float mViewHeight;
	private float mViewWidth;
	private float mRadius;
	private Paint mPaintBg = new Paint();
	private int	  mBgUnselectedColor = 0xffffff;
	private int	  mBgSelectedColor = 0x37be82;
	private float mArcWidth = 2.0f;
	
	public DotView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public DotView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public DotView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (!isInit) {
			isInit = true;
			mViewHeight = getHeight();
			mViewWidth = getWidth();
			if (mViewHeight >= mViewWidth) {
				mRadius = mViewWidth/2.f;
			}
			else  {
				mRadius = mViewHeight/2.f;
			}
		}
		
		if (isSelected)
			drawSelectedDot(canvas);
		else
			drawUnSelectedDot(canvas);
	}
	
	private void drawSelectedDot(Canvas canvas) {
		mPaintBg.setAntiAlias(true);
		mPaintBg.setColor(Color.parseColor("#37bf83"));
		mPaintBg.setStyle(Style.FILL);
		
		canvas.drawCircle(mViewWidth / 2.f, mViewHeight / 2.f, mRadius-1.f, mPaintBg);
		
		/*mPaintBg.setStyle(Style.STROKE);
		float offset = 1.f + mArcWidth;
		RectF oval = new RectF(mViewWidth/2.f - mRadius + offset, mViewHeight/2.f - mRadius + offset, 
				mViewWidth/2.f + mRadius - offset, mViewHeight/2.f + mRadius - offset);
		canvas.drawArc(oval, 0.f, 360.f, false, mPaintBg);*/
	}
	
	private void drawUnSelectedDot(Canvas canvas) {
	/*	mPaintBg.setAntiAlias(true);
		mPaintBg.setColor(Color.parseColor("#37bf83"));
		mPaintBg.setStyle(Style.FILL);
		
		canvas.drawCircle(mViewWidth/2.f, mViewHeight/2.f, mRadius-8.f, mPaintBg);*/

		mPaintBg.setColor(Color.parseColor("#37bf83"));
		mPaintBg.setStyle(Style.STROKE);
		float offset = 1.f + mArcWidth;
		RectF oval = new RectF(mViewWidth/2.f - mRadius + offset, mViewHeight/2.f - mRadius + offset,
				mViewWidth/2.f + mRadius - offset, mViewHeight/2.f + mRadius - offset);
		canvas.drawArc(oval, 0.f, 360.f, false, mPaintBg);
	}
	
	public void setIsSelected(boolean isSelected) {
		this.isSelected = isSelected;
		this.invalidate();
	}
	
}