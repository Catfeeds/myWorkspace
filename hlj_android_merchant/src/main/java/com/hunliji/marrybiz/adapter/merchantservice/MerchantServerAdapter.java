package com.hunliji.marrybiz.adapter.merchantservice;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.model.merchantservice.MarketItem;
import com.hunliji.marrybiz.util.MerchantServerUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mo_yu on 2018/1/24 0024.
 */

public class MerchantServerAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private final int TYPE_ITEM = 10;
    private final int TYPE_FOOTER = 11;
    private final int TYPE_HEADER = 12;

    private Context mContext;
    private LayoutInflater inflater;
    private List<MarketItem> marketItems;
    private View footerView;
    private View headerView;

    public MerchantServerAdapter(Context mContext) {
        this.mContext = mContext;
        inflater = LayoutInflater.from(mContext);
        marketItems = new ArrayList<>();
    }

    public void setMarketItems(List<MarketItem> marketItems) {
        this.marketItems.clear();
        if (!CommonUtil.isCollectionEmpty(marketItems)) {
            this.marketItems.addAll(marketItems);
        }
        notifyDataSetChanged();
    }

    public void setFooterView(View footerView) {
        this.footerView = footerView;
    }

    public void setHeaderView(View headerView) {
        this.headerView = headerView;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = null;
        switch (viewType) {
            case TYPE_ITEM:
                itemView = inflater.inflate(R.layout.merchant_ultimate_list_item, parent, false);
                return new MarketItemViewHolder(itemView);
            case TYPE_FOOTER:
                return new ExtraBaseViewHolder(footerView);
            case TYPE_HEADER:
                return new ExtraBaseViewHolder(headerView);
            default:
                break;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        if (holder instanceof MarketItemViewHolder) {
            MarketItemViewHolder serverViewHolder = (MarketItemViewHolder) holder;
            serverViewHolder.setView(mContext, getItem(position), position, TYPE_ITEM);
        }
    }

    private MarketItem getItem(int position) {
        if (headerView != null) {
            position = position - 1;
        }
        return marketItems.get(position);
    }

    @Override
    public int getItemViewType(int position) {
        if (headerView != null && position == 0) {
            return TYPE_HEADER;
        } else if (footerView != null && position == getItemCount() - 1) {
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }

    @Override
    public int getItemCount() {
        return CommonUtil.getCollectionSize(marketItems) + (headerView == null ? 0 : 1) +
                (footerView == null ? 0 : 1);
    }

    class MarketItemViewHolder extends BaseViewHolder<MarketItem> {
        @BindView(R.id.img_merchant_ultimate_item)
        ImageView imgMerchantUltimateItem;
        @BindView(R.id.tv_marketing_title)
        TextView tvMarketingTitle;
        @BindView(R.id.tv_marketing_content)
        TextView tvMarketingContent;
        @BindView(R.id.marketing_text_layout)
        LinearLayout marketingTextLayout;
        @BindView(R.id.line_layout)
        View lineLayout;

        public MarketItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        protected void setViewData(
                Context mContext, MarketItem item, int position, int viewType) {
            tvMarketingTitle.setText(item.getTitle());
            tvMarketingContent.setText(item.getSubTitle2());
            int imgRes = MerchantServerUtil.getInstance()
                    .getMerchantServerRoundImgRes(item.getProductId());
            if (imgRes > 0) {
                imgMerchantUltimateItem.setImageResource(imgRes);
            }
            if (position == (headerView == null ? 0 : 1)) {
                lineLayout.setVisibility(View.GONE);
            } else {
                lineLayout.setVisibility(View.VISIBLE);
            }
        }
    }
}
