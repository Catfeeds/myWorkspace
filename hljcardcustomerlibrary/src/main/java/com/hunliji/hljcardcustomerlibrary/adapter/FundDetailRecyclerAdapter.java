package com.hunliji.hljcardcustomerlibrary.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hunliji.hljcardcustomerlibrary.R;
import com.hunliji.hljcardcustomerlibrary.R2;
import com.hunliji.hljcardcustomerlibrary.models.Balance;
import com.hunliji.hljcardcustomerlibrary.models.FundDetail;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;
import com.hunliji.hljimagelibrary.adapters.viewholders.ExtraViewHolder;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mo_yu on 2017/11/24.理财明细adapter
 */
public class FundDetailRecyclerAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    public static final int ITEM_TYPE = 0;
    public static final int FOOTER_TYPE = 1;
    private Context mContext;
    private ArrayList<FundDetail> dataList;
    private View footerView;

    public FundDetailRecyclerAdapter(Context context) {
        this.mContext = context;
    }

    public void setDataList(ArrayList<FundDetail> dataList) {
        this.dataList = dataList;
    }

    public void setFooterView(View footerView) {
        this.footerView = footerView;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case FOOTER_TYPE:
                return new ExtraViewHolder<>(footerView);
            case ITEM_TYPE:
                return new ViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.fund_detail_list_item, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        int itemType = getItemViewType(position);
        if (itemType == ITEM_TYPE) {
            holder.setView(mContext, dataList.get(position), position, itemType);
        }
    }

    @Override
    public int getItemCount() {
        return (footerView != null ? 1 : 0) + (dataList != null ? dataList.size() : 0);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1 && footerView != null) {
            return FOOTER_TYPE;
        }
        return ITEM_TYPE;
    }

    static class ViewHolder extends BaseViewHolder<FundDetail> {
        @BindView(R2.id.tv_fund_title)
        TextView tvFundTitle;
        @BindView(R2.id.tv_create_time)
        TextView tvCreateTime;
        @BindView(R2.id.tv_fund_price)
        TextView tvFundPrice;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        @Override
        protected void setViewData(
                Context mContext, FundDetail item, int position, int viewType) {
            if (item.getCreatedAt() != null) {
                tvCreateTime.setText(item.getCreatedAt()
                        .toString(HljTimeUtils.DATE_FORMAT_MIDDLE));
            }
            if (item.getValue() >= 0) {
                tvFundPrice.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
                tvFundPrice.setText("+" + String.valueOf(item.getValue()));
            } else {
                tvFundPrice.setTextColor(ContextCompat.getColor(mContext, R.color.colorBlack2));
                tvFundPrice.setText(String.valueOf(item.getValue()));
            }
            tvFundTitle.setText(item.getTitle());
        }
    }
}
