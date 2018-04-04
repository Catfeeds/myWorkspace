package me.suncloud.marrymemo.fragment.work_case;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import com.hunliji.hljcommonlibrary.utils.CommonUtil;

import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.work_case.WeddingDressWorkRecyclerAdapter;

/**
 * Created by mo_yu on 2017/8/3.婚纱礼服（本地购买，本地租赁）227租赁 228出售
 */

public class WeddingDressWorkListFragment extends BaseMerchantServiceWorkListFragment {

    public static WeddingDressWorkListFragment newInstance(long propertyId, int saleWay) {
        WeddingDressWorkListFragment fragment = new WeddingDressWorkListFragment();
        Bundle args = new Bundle();
        args.putLong("property_id", propertyId);
        args.putInt("sale_way", saleWay);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initViews() {
        ((SimpleItemAnimator) recyclerView.getRefreshableView()
                .getItemAnimator()).setSupportsChangeAnimations(false);
        recyclerView.getRefreshableView()
                .setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
        recyclerView.setOnRefreshListener(this);
        recyclerView.getRefreshableView()
                .setPadding(0, CommonUtil.dp2px(getContext(), 8), 0, 0);
        recyclerView.getRefreshableView()
                .addItemDecoration(new SpacesItemDecoration());
        recyclerView.getRefreshableView()
                .setLayoutManager(new StaggeredGridLayoutManager(2,
                        StaggeredGridLayoutManager.VERTICAL));
        adapter = new WeddingDressWorkRecyclerAdapter(getContext());
        adapter.setFooterView(footerView);
        recyclerView.getRefreshableView()
                .setAdapter(adapter);
    }

    private class SpacesItemDecoration extends RecyclerView.ItemDecoration {

        private int space;

        SpacesItemDecoration() {
            this.space = CommonUtil.dp2px(getContext(), 8);
        }

        @Override
        public void getItemOffsets(
                Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            StaggeredGridLayoutManager.LayoutParams lp = (StaggeredGridLayoutManager
                    .LayoutParams) view.getLayoutParams();
            int top = 0;
            int left = 0;
            int right = 0;
            int position = parent.getChildAdapterPosition(view);
            if (position < adapter.getItemCount() - adapter.getFooterViewCount()) {
                left = lp.getSpanIndex() == 0 ? 0 : space / 2;
                right = lp.getSpanIndex() == 0 ? space / 2 : 0;
            }
            outRect.set(left, top, right, 0);
        }
    }
}
