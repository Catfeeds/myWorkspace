package me.suncloud.marrymemo.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.DataSetObservable;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;
import android.widget.SectionIndexer;

import java.util.ArrayList;
import java.util.Arrays;

import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.CityListAdapter;

/**
 * Created by Suncloud on 2015/11/2.
 */
public class NewSideBar extends View {

    private ListView list;
    private CityListAdapter adapter;
    private ArrayList<String> l;
    private float m_nItemHeight = 16;
    private SectionIndexer sectionIndexter = null;

    public NewSideBar(Context context) {
        super(context);
        l=new ArrayList<>();
    }

    public NewSideBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        l=new ArrayList<>();
    }

    public NewSideBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        l=new ArrayList<>();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public NewSideBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        l=new ArrayList<>();
    }

    public void setL(String[] l) {
        this.l = new ArrayList<>(Arrays.asList(l));
        requestLayout();
    }

    public void setListView(ListView listView) {
        this.list = listView;
    }

    public SectionIndexer getSectionIndexter() {
        if(list.getAdapter() instanceof HeaderViewListAdapter){
            HeaderViewListAdapter ha = (HeaderViewListAdapter) list.getAdapter();
            return (SectionIndexer) ha.getWrappedAdapter();
        }
        return (SectionIndexer)list.getAdapter();
    }

    public void setAdapter(CityListAdapter adapter) {
        this.adapter = adapter;
    }

    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        int i = (int) event.getY();
        int idx = (int) (i / m_nItemHeight);
        if (idx >= l.size()) {
            idx = l.size() - 1;
        } else if (idx < 0) {
            idx = 0;
        }
        if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
            if (sectionIndexter == null) {
                sectionIndexter = getSectionIndexter();
            }
            int position = idx>0?sectionIndexter.getPositionForSection(idx)+list.getHeaderViewsCount()-1:0;
            if (position == -1) {
                return true;
            }
            list.setSelection(position);
        }
        return true;
    }

    protected void onDraw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(0xff595c61);
        paint.setTextSize(getResources().getDimension(R.dimen.small_text_size));
        paint.setTextAlign(Paint.Align.CENTER);
        float widthCenter = getMeasuredWidth() / 2;
        m_nItemHeight = (float) getMeasuredHeight() / 27;
        for (int i = 0; i < l.size(); i++) {
            canvas.drawText(String.valueOf(l.get(i)), widthCenter, (i + 1) * m_nItemHeight, paint);
        }
        super.onDraw(canvas);
    }
}
