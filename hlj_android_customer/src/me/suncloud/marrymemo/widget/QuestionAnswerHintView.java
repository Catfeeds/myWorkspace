package me.suncloud.marrymemo.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;

import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.Util;

/**
 * Created by werther on 16/6/3.
 */
public class QuestionAnswerHintView extends FrameLayout implements View.OnClickListener {

    private int width;
    private int height;
    private ImageView imgCreate;
    private ImageView imgQaTab;
    private int tabWidth;
    private View contentView;
    private View targetView;
    private Handler handler;

    public QuestionAnswerHintView(Context context) {
        this(context, null);
    }

    public QuestionAnswerHintView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public QuestionAnswerHintView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setWillNotDraw(false);
        inflate(context, R.layout.question_answer_hint_layout, this);
        imgCreate = (ImageView) findViewById(R.id.img_create_thread_question);
        imgQaTab = (ImageView) findViewById(R.id.img_question_answer_tab);
        contentView = findViewById(R.id.content_layout);

        handler = new Handler();
        Point point = JSONUtil.getDeviceSize(context);
        width = point.x;

        contentView.setOnClickListener(this);
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
                    .putBoolean(Constants.PREF_QUESTION_ANSWER_HINT_CLICKED, true)
                    .apply();
        }
        super.setVisibility(visibility);
    }

    public void showHint(){
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setVisibility(GONE);
            }
        }, 3000);
    }

    private void resetLayout() {
        if (targetView != null && height > 0) {
            int[] position2 = JSONUtil.getViewCenterPositionOnScreen(targetView);
            final int left2 = position2[0];
            final int top2 = position2[1] - Util.getStatusBarHeight(getContext());
            final MarginLayoutParams layoutParams2 = (MarginLayoutParams) imgQaTab.getLayoutParams();
            ViewTreeObserver vto = imgQaTab.getViewTreeObserver();
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    imgQaTab.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    tabWidth = imgQaTab.getWidth();
                    layoutParams2.topMargin = top2 + targetView.getHeight()/2 - 10;
                    layoutParams2.leftMargin = left2 - tabWidth / 2;
                    imgQaTab.setLayoutParams(layoutParams2);
                    invalidate();
                }
            });
        }
    }



    //获取View坐标
    public static int[] getViewPositionOnScreen(View view) {
        if (view == null) {
            return null;
        }
        int[] position = new int[2];
        view.getLocationOnScreen(position);
        position[0] = position[0];
        position[1] = position[1];
        return position;
    }

    @Override
    public void onClick(View v) {
        setVisibility(GONE);
    }
}
