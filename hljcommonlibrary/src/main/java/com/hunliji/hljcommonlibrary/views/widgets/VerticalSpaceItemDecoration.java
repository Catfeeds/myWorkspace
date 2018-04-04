package com.hunliji.hljcommonlibrary.views.widgets;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by werther on 16/11/17.
 * 通用的给RecyclerView添加分割线的Decoration
 */

public class VerticalSpaceItemDecoration extends RecyclerView.ItemDecoration {
    private final int verticalSpaceHeight;
    private int position;

    public VerticalSpaceItemDecoration(int verticalSpaceHeight, int position) {
        this.verticalSpaceHeight = verticalSpaceHeight;
        this.position = position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public void getItemOffsets(
            Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        if (position >= 0 && parent.getChildAdapterPosition(view) == this.position) {
            outRect.bottom = verticalSpaceHeight;
        }
    }
}
