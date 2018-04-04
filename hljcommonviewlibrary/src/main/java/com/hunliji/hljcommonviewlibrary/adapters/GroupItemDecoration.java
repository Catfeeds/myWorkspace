package com.hunliji.hljcommonviewlibrary.adapters;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by wangtao on 2017/9/28.
 */

public class GroupItemDecoration extends RecyclerView.ItemDecoration {

    protected int space;

    public GroupItemDecoration(int space){
        this.space=space;
    }

    @Override
    public void getItemOffsets(
            Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if(parent.getAdapter()!=null&&parent.getAdapter() instanceof GroupRecyclerAdapter){
            int position=parent.getChildAdapterPosition(view);
            GroupRecyclerAdapter adapter= (GroupRecyclerAdapter) parent.getAdapter();
            if(position>0&&adapter.getGroupIndex(position)!=adapter.getGroupIndex(position-1)){
                outRect.set(0, space, 0, 0);
                return;
            }
        }
        super.getItemOffsets(outRect, view, parent, state);
    }
}
