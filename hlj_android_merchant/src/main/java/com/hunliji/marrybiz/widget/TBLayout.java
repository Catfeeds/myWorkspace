package com.hunliji.marrybiz.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Scroller;

public class TBLayout extends LinearLayout {
    private OnPullListener pullListener;
    private OnPageChangedListener ctListener;
    private OnScrollListener onScrollListener;

    public void setOnPullListener(OnPullListener listener) {
        this.pullListener = listener;
    }

    public void setOnContentChangeListener(OnPageChangedListener ler) {
        this.ctListener = ler;
    }

    private View mHeader;
    private View mFooter;
    private Scroller scroller;
    private int mTouchSlop = 0;

    private int mLastY;
    private int mLastInterceptY;
    private int mHeaderHeight;

    public final static int SCREEN_HEADER = 11;
    public final static int SCREEN_FOOTER = 12;

    private int screen = SCREEN_HEADER;

    @SuppressLint("NewApi")
    public TBLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public TBLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TBLayout(Context context) {
        super(context);
        init();
    }

    private void init() {
        scroller = new Scroller(getContext());
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (mHeader != null && mFooter != null && getMeasuredHeight() != mHeaderHeight) {
            LayoutParams lps = (LayoutParams) mFooter.getLayoutParams();
            mHeaderHeight = getMeasuredHeight();
            if (screen == SCREEN_FOOTER) {
                scroller.startScroll(0, 0, 0, mHeaderHeight, 0);
            } else if (screen == SCREEN_HEADER) {
                scroller.startScroll(0, 0, 0, 0, 0);
            }
            lps.height = mHeaderHeight;
            mFooter.setLayoutParams(lps);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (hasWindowFocus && mHeader == null && mFooter == null) {
            initData();
        }
    }

    public void initData() {
        mHeader = getChildAt(0);
        mFooter = getChildAt(1);
        ViewGroup.LayoutParams lps = mFooter.getLayoutParams();
        mHeaderHeight = mHeader.getMeasuredHeight();
        lps.height = mHeaderHeight;
        mFooter.setLayoutParams(lps);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean result = false;
        final int y = (int) ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastInterceptY = mLastY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                int dy = y - mLastInterceptY;
                if (dy > mTouchSlop && screen == SCREEN_FOOTER) {// pull down
                    result = (pullListener != null && pullListener
                            .footerHeadReached(ev));
                } else if (dy < -mTouchSlop && screen == SCREEN_HEADER) { // pull up
                    result = (pullListener != null && pullListener
                            .headerFootReached(ev));
                }
                break;
            case MotionEvent.ACTION_UP:
                mLastInterceptY = 0;
                break;
        }
        return result;
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        final int y = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                int dy = y - mLastY;
                switch (screen) {
                    case SCREEN_HEADER:
                        int sy = -dy;
                        if (sy < 0) {
                            sy = 0;
                        } else if (sy > getHeight()) {
                            sy = getHeight();
                        }
                        scrollTo(0, sy);
                        break;
                    case SCREEN_FOOTER:
                        if (dy > 0) {
                            scrollTo(0, mHeaderHeight - dy);
                        } else { // dy < 0
                        }
                        break;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                int t = 0;
                switch (screen) {
                    case SCREEN_HEADER:
                        t = mHeaderHeight / 4;
                        break;
                    case SCREEN_FOOTER:
                        t = mHeaderHeight * 3 / 4;
                        break;
                }
                int sy = getScrollY();
                if (sy > t) { // scroll to footer
                    scroller.startScroll(0, sy, 0, mHeaderHeight - sy, 150);
                    screen = SCREEN_FOOTER;
                    if (ctListener != null) {
                        ctListener.onPageChanged(SCREEN_FOOTER);
                    }
                    invalidate();
                } else { // scroll to header
                    scroller.startScroll(0, sy, 0, -sy, 150);
                    screen = SCREEN_HEADER;
                    if (ctListener != null) {
                        ctListener.onPageChanged(SCREEN_HEADER);
                    }
                    invalidate();
                }
                mLastY = 0;
                break;
        }
        return true;
    }

    @Override
    public void computeScroll() {
        if (scroller.computeScrollOffset()) {
            scrollTo(scroller.getCurrX(), scroller.getCurrY());
            postInvalidate();
        }
    }

    public void scrollToTop(){
        scroller.startScroll(0, 0, 0, 0, 150);
        screen = SCREEN_HEADER;
        if (ctListener != null) {
            ctListener.onPageChanged(SCREEN_HEADER);
        }
        invalidate();
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        if(onScrollListener!=null){
            onScrollListener.onScrollChanged(t);
        }
        super.onScrollChanged(l, t, oldl, oldt);
    }

    public interface OnPullListener {
        boolean headerFootReached(MotionEvent event);

        boolean footerHeadReached(MotionEvent event);
    }

    public interface OnPageChangedListener {
        void onPageChanged(int stub);
    }

    public void setOnScrollListener(OnScrollListener onScrollListener) {
        this.onScrollListener = onScrollListener;
    }

    public interface OnScrollListener {
        void onScrollChanged(int t);
    }
}
