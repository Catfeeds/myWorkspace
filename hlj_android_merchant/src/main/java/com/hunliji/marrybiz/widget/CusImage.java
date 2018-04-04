
package com.hunliji.marrybiz.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;

import com.hunliji.marrybiz.R;

public class CusImage extends View {

	private Paint myPaint;
	private Paint myFramePaint;
	public TextView value;
	private float startAngle;
	public float temp;
	float sweepAngle;
	private int flag = 0;
	RectF rect;
	int pix = 0;

	public CusImage(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public CusImage(Context context) {
		super(context);
		init();
	}

	private void init() {

		myPaint = new Paint();
		DisplayMetrics dm=getResources().getDisplayMetrics();
		pix = Math.round(dm.density*55);

		myPaint.setAntiAlias(true);
		myPaint.setStyle(Paint.Style.STROKE);
		myPaint.setColor(getResources().getColor(R.color.colorPrimary));  //Edit this to change progress arc color.
		myPaint.setStrokeWidth(2*dm.density);

		myFramePaint = new Paint();
		myFramePaint.setAntiAlias(true);
		myFramePaint.setColor(Color.TRANSPARENT);

		float startx = (float) (pix * 0.05);
		float endx = (float) (pix * 0.95);
		float starty = (float) (pix * 0.05);
		float endy = (float) (pix * 0.95);
		rect = new RectF(startx, starty, endx, endy);
	}

	public void setupprogress(int progress) {
		sweepAngle = (float) (progress * 3.6);
		invalidate();
		
	}

	public void reset() {
		sweepAngle = 0;
		invalidate();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		int desiredWidth = pix;
		int desiredHeight = pix;
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);

		int width;
		int height;

		
		if (widthMode == MeasureSpec.EXACTLY) {
			
			width = widthSize;
		} else if (widthMode == MeasureSpec.AT_MOST) {
			
			width = Math.min(desiredWidth, widthSize);
		} else {
			
			width = desiredWidth;
		}

		
		if (heightMode == MeasureSpec.EXACTLY) {
			
			height = heightSize;
		} else if (heightMode == MeasureSpec.AT_MOST) {
			
			height = Math.min(desiredHeight, heightSize);
		} else {
			
			height = desiredHeight;
		}

		
		setMeasuredDimension(width, height);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawArc(rect, -90, sweepAngle, false, myPaint);
	}
}
