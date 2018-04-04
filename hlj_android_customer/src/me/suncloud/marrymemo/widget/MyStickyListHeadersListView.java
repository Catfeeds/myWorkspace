package me.suncloud.marrymemo.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.hunliji.headerview.StickyListHeadersListView;

/**
 * Created by Suncloud on 2015/5/6.
 */
public class MyStickyListHeadersListView extends StickyListHeadersListView {

    private boolean b;
    private GestureDetector mGestureDetector;

    public MyStickyListHeadersListView(Context context) {
        super(context);
        mGestureDetector = new GestureDetector(context, new YScrollDetector());
    }

    public MyStickyListHeadersListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mGestureDetector = new GestureDetector(context, new YScrollDetector());
    }

    public MyStickyListHeadersListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mGestureDetector = new GestureDetector(context, new YScrollDetector());
    }


    @Override
    public boolean onInterceptTouchEvent(@NonNull MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        return super.onInterceptTouchEvent(event) && b;
    }

    class YScrollDetector extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {
            if (Math.abs(distanceX) > Math.abs(distanceY)) {
                b = false;
                return false;
            }
            b = true;
            return true;
        }
    }

}
