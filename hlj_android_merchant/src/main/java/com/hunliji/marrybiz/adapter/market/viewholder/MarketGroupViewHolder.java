package com.hunliji.marrybiz.adapter.market.viewholder;

import android.content.Context;
import android.support.v7.widget.GridLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.model.merchantservice.MarketGroup;
import com.hunliji.marrybiz.model.merchantservice.MarketItem;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by hua_rong on 2017/12/25
 */

public class MarketGroupViewHolder extends BaseViewHolder<MarketGroup> {

    @BindView(R.id.grid_layout)
    GridLayout gridLayout;
    @BindView(R.id.tv_title)
    TextView tvTitle;

    private OnItemClickListener onItemClickListener;

    private int itemWidth;

    public MarketGroupViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        Context context = itemView.getContext();
        itemWidth = Math.round((CommonUtil.getDeviceSize(context).x - CommonUtil.dp2px(context,
                32)) / 4);
    }

    @Override
    protected void setViewData(
            Context context, MarketGroup marketGroup, int position, int viewType) {
        List<MarketItem> marketItems = marketGroup.getMarketItems();
        tvTitle.setText(marketGroup.getTitle());
        if (!CommonUtil.isCollectionEmpty(marketItems)) {
            int size = marketItems.size();
            int childCount = gridLayout.getChildCount();
            if (childCount > size) {
                gridLayout.removeViews(size, childCount - size);
            }
            for (int i = 0; i < size; i++) {
                View childView = null;
                if (i < childCount) {
                    childView = gridLayout.getChildAt(i);
                }
                if (childView == null) {
                    childView = LayoutInflater.from(context)
                            .inflate(R.layout.market_privilege_record_item, gridLayout, false);
                    childView.getLayoutParams().width = itemWidth;
                    gridLayout.addView(childView);
                }
                MarketItemViewHolder holder = (MarketItemViewHolder) childView.getTag();
                if (holder == null) {
                    holder = new MarketItemViewHolder(childView);
                    holder.setOnItemClickListener(onItemClickListener);
                    childView.setTag(holder);
                }
                holder.setView(context, marketItems.get(i), i, getItemViewType());
            }
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

}




