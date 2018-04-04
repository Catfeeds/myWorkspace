package com.hunliji.hljpushlibrary.widgets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PixelFormat;
import android.support.annotation.StyleRes;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import com.hunliji.hljpushlibrary.R;

import java.util.Timer;
import java.util.TimerTask;

import static me.imid.swipebacklayout.lib.ViewDragHelper.STATE_IDLE;

/**
 * Created by wangtao on 2017/12/1.
 */

public class HljNotify {


    private WindowManager manger;
    @SuppressLint("StaticFieldLeak")
    private static HljNotify lastHljNotify;
    private SwipeNotifyLayout contentView;
    private WindowManager.LayoutParams params;
    private static Timer timer;
    private int duration;


    public HljNotify(Context context, View view) {
        this(context, view, 8000, R.style.dialog_anim_top_style);
    }


    public HljNotify(Context context, View view, int duration) {
        this(context, view, duration, R.style.dialog_anim_top_style);
    }

    public HljNotify(Context context, View view, int duration, @StyleRes int animId) {
        this.duration = duration;
        manger = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        SwipeNotifyLayout swipeNotifyLayout = new SwipeNotifyLayout(context);
        swipeNotifyLayout.addView(view);
        swipeNotifyLayout.setListener(new SwipeNotifyLayout.SwipeListener() {
            @Override
            public void onScrollStateChange(int state, float scrollPercent) {
            }

            @Override
            public void onFinish() {
                HljNotify.this.cancel();
            }
        });
        contentView = swipeNotifyLayout;
        params = new WindowManager.LayoutParams();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.format = PixelFormat.TRANSLUCENT;
        params.windowAnimations = animId;
        params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager
                .LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_SPLIT_TOUCH;
        params.gravity = Gravity.TOP;
    }


    public void show() {
        if (lastHljNotify != null) {
            lastHljNotify.cancel();
        }
        manger.addView(contentView, params);
        lastHljNotify = this;
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (contentView == null) {
                    return;
                }
                contentView.post(new Runnable() {
                    @Override
                    public void run() {
                        HljNotify.this.cancel();
                    }
                });
            }
        }, duration);
    }

    public void cancel() {
        if (contentView != null) {
            contentView.setListener(null);
            contentView.setSwipeEnable(false);
            try {
                manger.removeView(contentView);
            } catch (Exception e) {
                e.printStackTrace();
            }
            contentView = null;
        }
        if (timer != null) {
            timer.cancel();
        }
        lastHljNotify = null;
    }
}
