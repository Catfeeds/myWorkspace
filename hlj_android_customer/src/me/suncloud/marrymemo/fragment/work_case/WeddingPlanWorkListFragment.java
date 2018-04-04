package me.suncloud.marrymemo.fragment.work_case;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.View;

import com.hunliji.hljcommonlibrary.utils.CommonUtil;

import me.suncloud.marrymemo.adpter.work_case.WeddingPlanWorkRecyclerAdapter;

/**
 * 婚礼策划套餐列表
 * Created by chen_bin on 2017/7/28 0028.
 */
public class WeddingPlanWorkListFragment extends BaseMerchantServiceWorkListFragment {

    public static WeddingPlanWorkListFragment newInstance(long propertyId, long categoryMarkId,String parentName) {
        WeddingPlanWorkListFragment fragment = new WeddingPlanWorkListFragment();
        Bundle args = new Bundle();
        args.putLong("property_id", propertyId);
        args.putLong("category_mark_id", categoryMarkId);
        args.putString(ARG_PARENT_NAME,parentName);
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
        adapter = new WeddingPlanWorkRecyclerAdapter(getContext());
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
            int top = position > 0 && position < adapter.getItemCount() - adapter
                    .getFooterViewCount() ? space : 0;
            outRect.set(0, top, 0, 0);
        }
    }
}