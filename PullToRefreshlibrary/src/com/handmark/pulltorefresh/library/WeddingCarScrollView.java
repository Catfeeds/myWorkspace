package com.handmark.pulltorefresh.library;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.ScrollView;

/**
 * Created by WangTao on 2015/10/2.
 */
public class WeddingCarScrollView extends ScrollView{

    private ListView listView1;
    private ListView listView2;
    private View carSelfMenuVew;
    private View menuLayout;
    private float mLastInterceptY;
    private int kind;


    public WeddingCarScrollView(Context context) {
        super(context);
    }

    public WeddingCarScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WeddingCarScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setMenuLayout(View menuLayout) {
        this.menuLayout = menuLayout;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
    }

    @Override
    public boolean onInterceptTouchEvent(@NonNull MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastInterceptY =  ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                boolean b=(carSelfMenuVew==null||carSelfMenuVew.getVisibility()==GONE)&&(getScrollY()< menuLayout.getTop()||(ev.getY() - mLastInterceptY>0&&isListTop()));
                mLastInterceptY =  ev.getY();
                return super.onInterceptTouchEvent(ev)&&b;
            case MotionEvent.ACTION_UP:
                mLastInterceptY = 0;
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    public void setListView1(ListView listView) {
        this.listView1 = listView;
    }

    public void setCarSelfMenuVew(View carSelfMenuVew) {
        this.carSelfMenuVew = carSelfMenuVew;
    }

    public void setListView2(ListView listView2) {
        this.listView2 = listView2;
    }

    public void setKind(int kind){
        this.kind=kind;
    }

    private boolean isListTop() {
        ListView listView=kind!=0?listView2:listView1;
        return listView != null && (listView.getCount() == 0 || listView.getChildAt(0).getTop() == 0 && listView.getFirstVisiblePosition() == 0);
    }

    public void scrollToTop(){
        smoothScrollTo(0, menuLayout.getTop());
    }


    public boolean isReadyForPullStart() {
        return getScrollY() == 0&&isListTop()&&(carSelfMenuVew==null||carSelfMenuVew.getVisibility()==GONE);
    }

    public boolean isReadyForPullEnd() {
        View scrollViewChild = getChildAt(0);
        return null != scrollViewChild && getScrollY() >= (scrollViewChild.getHeight() - getHeight());
    }
}
