package com.hunliji.hljcommonlibrary.views.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import static android.view.View.MeasureSpec.EXACTLY;
import static android.view.View.MeasureSpec.makeMeasureSpec;

/**
 * Created by wangtao on 2017/1/10.
 */

public class HljGridView extends ViewGroup {

    private int mColumnCount;    // 列数
    private int mHorizontalSpacing; //行间隔
    private int mVerticalSpacing; // 列间隔
    private int itemWidth;
    private int itemHeight;

    private GridInterface gridInterface;
    private GridItemClickListener itemClickListener;

    private List mDate;

    public HljGridView(Context context) {
        this(context, null);
    }

    public HljGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = getPaddingTop() + getPaddingBottom();
        int totalWidth = width - getPaddingLeft() - getPaddingRight();
        int childCount = getChildCount();
        if (gridInterface != null) {
            mHorizontalSpacing = gridInterface.getSpacing(childCount, MeasureType.HORIZONTAL);
            mVerticalSpacing = gridInterface.getSpacing(childCount, MeasureType.VERTICAL);
            mColumnCount = gridInterface.getColumnCount(childCount);
            int mRowCount = (childCount + mColumnCount - 1) / mColumnCount;
            itemWidth = gridInterface.getItemSize(totalWidth, childCount, MeasureType.WIDTH);
            itemHeight = gridInterface.getItemSize(totalWidth, childCount, MeasureType.HEIGHT);
            height += itemHeight * mRowCount + mHorizontalSpacing * (mRowCount - 1);
        }
        if (childCount > 0) {
            for (int i = 0; i < childCount; i++) {
                View child = getChildAt(i);
                child.measure(itemWidth, itemHeight);
            }
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
//        Log.e("onLayout", toString());
        layoutChildrenView();
    }

    public void setDate(final List mDate) {
        this.mDate = mDate;
        if (gridInterface == null) {
            return;
        }
        int count = getChildCount();
        int size = mDate == null ? 0 : mDate.size();
        if (count > size) {
            removeViews(size, count - size);
        } else if (size > count) {
            for (int i = count; i < size; i++) {
                View childrenView = gridInterface.getItemView(this);
                addView(childrenView, generateDefaultLayoutParams());
                final int position = i;
                childrenView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (itemClickListener != null) {
                            itemClickListener.onItemClick(v, position);
                        }
                    }
                });
            }
        }
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                View childrenView = getChildAt(i);
                gridInterface.setViewValue(getContext(), childrenView, i, mDate);
                HljGridImageView imageView = gridInterface.getGridImageView(childrenView);
                if (imageView != null) {
                    imageView.setItem(mDate.get(i));
                }
            }
        }
    }

    public void setGridInterface(GridInterface gridInterface) {
        this.gridInterface = gridInterface;
    }

    public void setItemClickListener(GridItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    private void layoutChildrenView() {
        if (mDate == null || gridInterface == null) {
            return;
        }
        int size = mDate.size();
        for (int i = 0; i < size; i++) {
            View childrenView = getChildAt(i);
            int rowNum = i / mColumnCount;
            int columnNum = i % mColumnCount;
            int left = (itemWidth + mVerticalSpacing) * columnNum + getPaddingLeft();
            int top = (itemHeight + mHorizontalSpacing) * rowNum + getPaddingTop();
            int right = left + itemWidth;
            int bottom = top + itemHeight;

            if (itemWidth != childrenView.getMeasuredWidth() || itemHeight != childrenView
                    .getMeasuredHeight()) {
                childrenView.measure(makeMeasureSpec(itemWidth, EXACTLY),
                        makeMeasureSpec(itemHeight, EXACTLY));
            }
            childrenView.layout(left, top, right, bottom);
        }
    }

    public enum MeasureType {
        HORIZONTAL, VERTICAL, WIDTH, HEIGHT
    }

    public interface GridInterface<T> {

        void setViewValue(Context context, View itemView, int position, List<T> data);

        View getItemView(ViewGroup viewGroup);

        int getColumnCount(int size);

        int getSpacing(int size, MeasureType type);

        int getItemSize(int totalWidth, int size, MeasureType type);

        HljGridImageView getGridImageView(View itemView);
    }

    public interface GridItemClickListener {
        void onItemClick(View itemView, int position);
    }
}
