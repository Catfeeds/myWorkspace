package com.hunliji.hljcommonlibrary.views.widgets;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowInsets;
import android.widget.FrameLayout;

/**
 * Created by luohanlin on 2017/8/3.
 */

public class SwipeActivityRootViewV20 extends SwipeActivityRootView {


    public SwipeActivityRootViewV20(@NonNull Context context) {
        super(context);
    }

    public SwipeActivityRootViewV20(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SwipeActivityRootViewV20(
            @NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT_WATCH)
    @Override
    public final WindowInsets onApplyWindowInsets(WindowInsets insets) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return super.onApplyWindowInsets(insets.replaceSystemWindowInsets(0,
                    0,
                    0,
                    insets.getSystemWindowInsetBottom()));
        } else {
            return insets;
        }
    }
}
