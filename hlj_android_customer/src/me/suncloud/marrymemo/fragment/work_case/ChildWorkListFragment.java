package me.suncloud.marrymemo.fragment.work_case;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.View;

import com.hunliji.hljcommonlibrary.utils.CommonUtil;

import me.suncloud.marrymemo.adpter.work_case.ChildWorkListAdapter;

/**
 * 儿童婚纱摄影二级页
 * Created by jinxin on 2017/12/11 0011.
 */

public class ChildWorkListFragment extends BaseMerchantServiceWorkListFragment {

    public static ChildWorkListFragment getInstance(
            long propertyId,
            long categoryId,
            long secondCategoryId) {
        ChildWorkListFragment childWorkListFragment = new ChildWorkListFragment();
        Bundle args = new Bundle();
        args.putLong("property_id", propertyId);
        args.putLong("category_id", categoryId);
        args.putLong("filter_second_category", secondCategoryId);
        childWorkListFragment.setArguments(args);
        return childWorkListFragment;
    }

    @Override
    protected void initViews() {
        ((SimpleItemAnimator) recyclerView.getRefreshableView()
                .getItemAnimator()).setSupportsChangeAnimations(false);
        recyclerView.setOnRefreshListener(this);
        recyclerView.getRefreshableView()
                .addItemDecoration(new ChildWorkItemDecoration(getContext()));
        recyclerView.getRefreshableView()
                .setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ChildWorkListAdapter(getContext());
        adapter.setFooterView(footerView);
        recyclerView.getRefreshableView()
                .setAdapter(adapter);
    }


    private class ChildWorkItemDecoration extends RecyclerView.ItemDecoration {

        private int bottom;

        ChildWorkItemDecoration(Context context) {
            bottom = CommonUtil.dp2px(context, 10);
        }

        @Override
        public void getItemOffsets(
                Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            if (parent.getChildAdapterPosition(view) == 0) {
                outRect.top = bottom;
            }
            outRect.bottom = bottom;
        }
    }
}
