package com.hunliji.hljnotelibrary.views.widgets;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import com.hunliji.hljcommonlibrary.utils.CommonUtil;

/**
 * Created by jinxin on 2017/7/12 0012.
 */

public class SpacesItemDecoration extends RecyclerView.ItemDecoration {

    private int space;
    private int middleSpace;
    private int headCount;
    private int footerCount;

    public SpacesItemDecoration(Context context, int headCount, int footerCount) {
        this.space = CommonUtil.dp2px(context, 10);
        this.middleSpace = CommonUtil.dp2px(context, 8);
        this.headCount = headCount;
        this.footerCount = footerCount;
    }

    @Override
    public void getItemOffsets(
            Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        StaggeredGridLayoutManager.LayoutParams lp = (StaggeredGridLayoutManager.LayoutParams)
                view.getLayoutParams();
        int top = 0;
        int left = 0;
        int right = 0;
        int position = parent.getChildAdapterPosition(view);
        if (position >= headCount && position < parent.getAdapter()
                .getItemCount() - footerCount) {
            top = middleSpace;
            left = lp.getSpanIndex() == 0 ? space : middleSpace / 2;
            right = lp.getSpanIndex() == 0 ? middleSpace / 2 : space;
        }
        outRect.set(left, top, right, 0);
    }
}
