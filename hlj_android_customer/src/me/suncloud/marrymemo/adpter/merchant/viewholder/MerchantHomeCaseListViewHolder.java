package me.suncloud.marrymemo.adpter.merchant.viewholder;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.models.Work;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.ThemeUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.work_case.MerchantRecommendCaseRecyclerAdapter;

/**
 * Created by wangtao on 2017/9/29.
 */

public class MerchantHomeCaseListViewHolder extends BaseViewHolder<List<Work>> {


    @BindView(R.id.recycler_view)
    RecyclerView recommendCaseRecyclerView;

    public MerchantHomeCaseListViewHolder(ViewGroup parent) {
        this(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_view_layout, parent, false));
    }

    private MerchantHomeCaseListViewHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
        recommendCaseRecyclerView.setFocusable(false);
        recommendCaseRecyclerView.setBackgroundColor(ThemeUtil.getAttrColor(itemView.getContext(),
                R.attr.hljColorSegmentBackground,
                Color.WHITE));
        recommendCaseRecyclerView.setPadding(0,0,0,CommonUtil.dp2px(itemView.getContext(),16));
        recommendCaseRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(),
                LinearLayoutManager.HORIZONTAL,
                false));
        recommendCaseRecyclerView.addItemDecoration(new CaseSpacesItemDecoration(view.getContext
                ()));
        MerchantRecommendCaseRecyclerAdapter recommendCaseAdapter = new
                MerchantRecommendCaseRecyclerAdapter(
                view.getContext(),
                null);
        recommendCaseRecyclerView.setAdapter(recommendCaseAdapter);
    }

    @Override
    protected void setViewData(
            Context mContext, List<Work> cases, int position, int viewType) {
        if (recommendCaseRecyclerView.getAdapter() != null && recommendCaseRecyclerView
                .getAdapter() instanceof MerchantRecommendCaseRecyclerAdapter) {
            ((MerchantRecommendCaseRecyclerAdapter) recommendCaseRecyclerView.getAdapter())
                    .setCases(
                    cases);
        }

    }

    private class CaseSpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;
        private int middleSpace;

        CaseSpacesItemDecoration(Context context) {
            this.space = CommonUtil.dp2px(context, 16);
            this.middleSpace = CommonUtil.dp2px(context, 4);
        }

        @Override
        public void getItemOffsets(
                Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int left = middleSpace;
            int right = 0;
            int position = parent.getChildAdapterPosition(view);
            if (position == 0) {
                left = space;
            } else if (position == parent.getAdapter()
                    .getItemCount() - 1) {
                right = space;
            }
            outRect.set(left, 0, right, 0);
        }
    }

}
