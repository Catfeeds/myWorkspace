package com.hunliji.hljquestionanswer.widgets;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljquestionanswer.HljQuestionAnswer;
import com.hunliji.hljquestionanswer.R;

import java.lang.reflect.Field;


/**
 * Created by mo_yu on 16/6/3.回答详情引导
 */
public class AnswerDetailHintView extends FrameLayout implements View.OnClickListener {

    private int width;
    private int height;
    private ImageView imgCollect;
    private ImageView imgPraise;
    private int tabHeight;
    private View contentView;
    private View targetView;
    private Handler handler;
    private Context context;

    public AnswerDetailHintView(Context context) {
        this(context, null);
    }

    public AnswerDetailHintView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AnswerDetailHintView(
            Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setWillNotDraw(false);
        inflate(context, R.layout.answer_detail_hint___qa, this);
        imgCollect = (ImageView) findViewById(R.id.img_collect_answer);
        imgPraise = (ImageView) findViewById(R.id.img_praise_answer);
        contentView = findViewById(R.id.content_layout);

        this.context = context;
        handler = new Handler();
        Point point = CommonUtil.getDeviceSize(context);
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
            getContext().getSharedPreferences(HljCommon.FileNames.PREF_FILE, Context.MODE_PRIVATE)
                    .edit()
                    .putBoolean(HljQuestionAnswer.ANSWER_DETAIL_HINT, true)
                    .apply();
        }
        super.setVisibility(visibility);
    }

    public void showHint() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setVisibility(GONE);
            }
        }, 3000);
    }

    private void resetLayout() {
        if (targetView != null && height > 0) {
            final MarginLayoutParams layoutParams2 = (MarginLayoutParams) imgPraise
                    .getLayoutParams();
            ViewTreeObserver vto = imgPraise.getViewTreeObserver();
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void onGlobalLayout() {
                    imgPraise.getViewTreeObserver()
                            .removeOnGlobalLayoutListener(this);
                    tabHeight = targetView.getHeight();
                    int[] position = getViewPositionOnScreen(targetView);
                    layoutParams2.topMargin = position[1] - getStatusBarHeight(context) -
                            CommonUtil.dp2px(context, 40) + tabHeight;
                    imgPraise.setLayoutParams(layoutParams2);
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

    private static int statusBarHeight;

    public static int getStatusBarHeight(Context context) {
        if (statusBarHeight == 0) {
            Class<?> c;
            Object obj;
            Field field;
            int x;
            try {
                c = Class.forName("com.android.internal.R$dimen");
                obj = c.newInstance();
                field = c.getField("status_bar_height");
                x = Integer.parseInt(field.get(obj)
                        .toString());
                statusBarHeight = context.getResources()
                        .getDimensionPixelSize(x);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        return statusBarHeight;

    }

    @Override
    public void onClick(View v) {
        setVisibility(GONE);
    }
}
