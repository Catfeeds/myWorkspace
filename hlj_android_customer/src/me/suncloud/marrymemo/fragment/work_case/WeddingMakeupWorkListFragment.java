package me.suncloud.marrymemo.fragment.work_case;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.View;

import com.hunliji.hljcommonlibrary.utils.CommonUtil;

import me.suncloud.marrymemo.adpter.work_case.WeddingMakeupWorkRecyclerAdapter;

/**
 * 婚礼跟妆
 * Created by chen_bin on 2017/7/28 0028.
 */
public class WeddingMakeupWorkListFragment extends BaseMerchantServiceWorkListFragment {

    public static BaseMerchantServiceWorkListFragment newInstance(
            long propertyId, long categoryMarkId) {
        WeddingMakeupWorkListFragment fragment = new WeddingMakeupWorkListFragment();
        Bundle args = new Bundle();
        args.putLong("property_id", propertyId);
        args.putLong("category_mark_id", categoryMarkId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initViews() {
        ((SimpleItemAnimator) recyclerView.getRefreshableView()
                .getItemAnimator()).setSupportsChangeAnimations(false);
        recyclerView.setOnRefreshListener(this);
        recyclerView.getRefreshableView()
                .addItemDecoration(new SpacesItemDecoration());
        recyclerView.getRefreshableView()
                .setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new WeddingMakeupWorkRecyclerAdapter(getContext());
        adapter.setFooterView(footerView);
        recyclerView.getRefreshableView()
                .setAdapter(adapter);
    }

    private class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpacesItemDecoration() {
            this.space = CommonUtil.dp2px(getContext(), 10);
        }

        @Override
        public void getItemOffsets(
                Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);
            int top = position < adapter.getItemCount() - adapter.getFooterViewCount() ? space : 0;
            outRect.set(0, top, 0, 0);
        }
    }

}