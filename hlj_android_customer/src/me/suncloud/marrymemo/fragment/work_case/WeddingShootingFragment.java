package me.suncloud.marrymemo.fragment.work_case;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.View;

import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;

import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.work_case.WeddingShootingAdapter;

/**
 * Created by hua_rong on 2017/7/31.
 * 婚礼摄像
 */

public class WeddingShootingFragment extends BaseMerchantServiceWorkListFragment {


    public static WeddingShootingFragment newInstance(String keyword) {
        WeddingShootingFragment fragment = new WeddingShootingFragment();
        Bundle args = new Bundle();
        args.putLong("property_id", Merchant.PROPERTY_WEDDING_SHOOTING);
        args.putString("keyword", keyword);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initViews() {
        ((SimpleItemAnimator) recyclerView.getRefreshableView()
                .getItemAnimator()).setSupportsChangeAnimations(false);
        recyclerView.setOnRefreshListener(this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (position == adapter.getItemCount() - adapter.getFooterViewCount()) {
                    return 2;
                } else {
                    return 1;
                }
            }
        });
        recyclerView.getRefreshableView()
                .setLayoutManager(gridLayoutManager);
        recyclerView.getRefreshableView()
                .addItemDecoration(new CompereItemDecoration(getContext()));
        recyclerView.getRefreshableView()
                .setPadding(0, CommonUtil.dp2px(getContext(), 10), 0, 0);
        recyclerView.getRefreshableView()
                .setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
        adapter = new WeddingShootingAdapter(getContext());
        adapter.setFooterView(footerView);
        recyclerView.getRefreshableView()
                .setAdapter(adapter);
    }

    private class CompereItemDecoration extends RecyclerView.ItemDecoration {

        private int left;
        private int space;
        private int right;

        CompereItemDecoration(Context context) {
            left = CommonUtil.dp2px(context, 10);
            space = CommonUtil.dp2px(context, 4);
            right = CommonUtil.dp2px(context, 10);
        }

        @Override
        public void getItemOffsets(
                Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);
            if (position % 2 == 0) {
                outRect.right = space;
                outRect.left = left;
            } else {
                outRect.left = space;
                outRect.right = right;
            }
        }
    }

}
