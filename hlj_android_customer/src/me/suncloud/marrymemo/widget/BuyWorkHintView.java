package me.suncloud.marrymemo.widget;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.view.binding_partner.BindingPartnerActivity;

/**
 * 买套餐页面引导
 * Created by jinxin on 2016/12/10
 */
public class BuyWorkHintView extends FrameLayout implements View.OnClickListener {
    private int width;
    private int height;
    private ImageView imgHint;
    private View targetView;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private int mOldHeight;
    private int mOldWidth;
    private Paint mEraser;
    private View itemView;

    public BuyWorkHintView(Context context) {
        this(context, null);
    }

    public BuyWorkHintView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BuyWorkHintView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setWillNotDraw(false);
        inflate(context, R.layout.buy_work_hint_layout, this);
        imgHint = (ImageView) findViewById(R.id.img_hint);
        itemView = findViewById(R.id.item_view);
        itemView.setOnClickListener(this);
        Point point = JSONUtil.getDeviceSize(context);
        width = point.x;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        height = h;
        if (getVisibility() == VISIBLE) {
            resetLayout();
        }
        super.onSizeChanged(w, h, oldw, oldh);
    }

    public void setTargetView(View targetView) {
        this.targetView = targetView;
        if (getVisibility() == VISIBLE) {
            resetLayout();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }

    @Override
    public void setVisibility(int visibility) {
        if (visibility == VISIBLE) {
            resetLayout();
        } else if (getVisibility() == VISIBLE) {
            getContext().getSharedPreferences(Constants.PREF_FILE, Context.MODE_PRIVATE)
                    .edit()
                    .putBoolean(Constants.PREF_BUY_WORK_HINT_CLICKED, true)
                    .commit();
        }
        super.setVisibility(visibility);
    }

    private void resetLayout() {
        if (imgHint != null) {
            //            int[] position = getViewPositionOnScreen(targetView);
            //            int top = position[1] - Util.getStatusBarHeight(getContext());
            //            MarginLayoutParams layoutParams = (MarginLayoutParams) imgHint
            // .getLayoutParams();
            //            layoutParams.topMargin = top;
            //            imgHint.setLayoutParams(layoutParams);
        }
        invalidate();
    }

    //获取View坐标
    public static int[] getViewPositionOnScreen(View view) {
        if (view == null) {
            return null;
        }
        int[] position = new int[2];
        view.getLocationOnScreen(position);
        return position;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // get current dimensions
        final int width = getMeasuredWidth();
        final int height = getMeasuredHeight();

        // don't bother drawing if there is nothing to draw on
        if (width <= 0 || height <= 0)
            return;

        // build a new canvas if needed i.e first pass or new dimensions
        if (mBitmap == null || mCanvas == null || mOldHeight != height || mOldWidth != width) {

            if (mBitmap != null)
                mBitmap.recycle();

            mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

            mCanvas = new Canvas(mBitmap);
        }

        // save our 'old' dimensions
        mOldWidth = width;
        mOldHeight = height;

        // clear canvas
        mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

        // draw solid background
        mCanvas.drawColor(ContextCompat.getColor(getContext(), R.color.transparent_black5));

        // Prepare eraser Paint if needed
        if (mEraser == null) {
            mEraser = new Paint();
            mEraser.setColor(0xFFFFFFFF);
            mEraser.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            mEraser.setFlags(Paint.ANTI_ALIAS_FLAG);
        }
        if (targetView != null) {
            int[] position2 = getViewPositionOnScreen(targetView);
            RectF oval2 = new RectF();                     //RectF对象
            oval2.left = position2[0] + targetView.getWidth() / 4;                              //左边
            oval2.top = position2[1] - Util.getStatusBarHeight(getContext());   //上边
            oval2.right = position2[0] + targetView.getWidth() - targetView.getWidth() / 4;
            //右边
            oval2.bottom = position2[1] - Util.getStatusBarHeight(getContext()) + targetView
                    .getHeight();//下边
            mCanvas.drawOval(oval2, mEraser);
        }

        canvas.drawBitmap(mBitmap, 0, 0, null);
    }

    @Override
    public void onClick(View v) {
        setVisibility(GONE);
    }
}
