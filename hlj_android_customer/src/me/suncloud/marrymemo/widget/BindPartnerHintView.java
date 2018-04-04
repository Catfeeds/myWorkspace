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
 * Created by werther on 16/6/3.
 * 设置页面绑定伴侣的初次介绍提示页面
 */
public class BindPartnerHintView extends FrameLayout implements View.OnClickListener {

    private int width;
    private int height;
    private ImageView imgHint;
    private ImageButton btnClose;
    private View targetView;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private int mOldHeight;
    private int mOldWidth;
    private Paint mEraser;
    private View partnerView;

    public BindPartnerHintView(Context context) {
        this(context, null);
    }

    public BindPartnerHintView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BindPartnerHintView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setWillNotDraw(false);
        inflate(context, R.layout.bind_partner_hint_layout, this);
        imgHint = (ImageView) findViewById(R.id.img_hint);
        btnClose = (ImageButton) findViewById(R.id.btn_close);
        partnerView = findViewById(R.id.partner_layout);
        partnerView.setOnClickListener(this);
        Point point = JSONUtil.getDeviceSize(context);
        width = point.x;

        btnClose.setOnClickListener(this);
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
                    .putBoolean(Constants.PREF_BIND_PARTNER_HINT_CLICKED, true)
                    .commit();
        }
        super.setVisibility(visibility);
    }

    private void resetLayout() {
        if (partnerView != null) {
            int[] position = getViewPositionOnScreen(targetView);
            int top = position[1] - Util.getStatusBarHeight(getContext());
            MarginLayoutParams layoutParams = (MarginLayoutParams) partnerView.getLayoutParams();
            layoutParams.topMargin = top;
            partnerView.setLayoutParams(layoutParams);
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
            //画透明矩形
            RectF oval2 = new RectF();                     //RectF对象
            oval2.left = position2[0];                              //左边
            oval2.top = position2[1] - Util.getStatusBarHeight(getContext());   //上边
            oval2.right = position2[0] + targetView.getWidth();                             //右边
            oval2.bottom = position2[1] - Util.getStatusBarHeight(getContext()) + targetView
                    .getHeight();//下边
            mCanvas.drawRect(oval2, mEraser);
        }

        canvas.drawBitmap(mBitmap, 0, 0, null);
    }

    @Override
    public void onClick(View v) {
        setVisibility(GONE);
        if (v.getId() == R.id.partner_layout) {
            Intent intent = new Intent(getContext(), BindingPartnerActivity.class);
            getContext().startActivity(intent);
        }
    }
}
