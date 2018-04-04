package me.suncloud.marrymemo.fragment.work_case;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.View;

import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;

import me.suncloud.marrymemo.adpter.work_case.WeddingPhotoAdapter;

/**
 * Created by hua_rong on 2017/7/31.
 * 婚礼摄影
 */

public class WeddingPhotoFragment extends BaseMerchantServiceWorkListFragment {

    public static WeddingPhotoFragment newInstance(String keyword) {
        WeddingPhotoFragment fragment = new WeddingPhotoFragment();
        Bundle args = new Bundle();
        args.putLong("property_id", Merchant.PROPERTY_WEDDING_PHOTO);
        args.putString("keyword", keyword);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initViews() {
        ((SimpleItemAnimator) recyclerView.getRefreshableView()
                .getItemAnimator()).setSupportsChangeAnimations(false);
        recyclerView.setOnRefreshListener(this);
        recyclerView.getRefreshableView()
                .addItemDecoration(new WeddingPhotoItemDecoration(getContext()));
        recyclerView.getRefreshableView()
                .setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new WeddingPhotoAdapter(getContext());
        adapter.setFooterView(footerView);
        recyclerView.getRefreshableView()
                .setAdapter(adapter);
    }

    private class WeddingPhotoItemDecoration extends RecyclerView.ItemDecoration {

        private int bottom;

        WeddingPhotoItemDecoration(Context context) {
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
